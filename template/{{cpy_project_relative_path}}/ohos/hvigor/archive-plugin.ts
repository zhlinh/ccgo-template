/**
 * Custom Hvigor plugin for archiving OHOS project build artifacts.
 *
 * This plugin provides an 'archiveProject' task similar to Gradle's archiveProject,
 * which packages the HAR file and related build artifacts into an archive ZIP.
 *
 * Usage:
 *   hvigorw archiveProject
 */

import { HvigorNode, HvigorPlugin } from '@ohos/hvigor';
import * as fs from 'fs';
import * as path from 'path';
import { execSync } from 'child_process';

/**
 * Get version suffix from git tags or branch
 */
function getVersionSuffix(): string {
    try {
        // Try to get suffix from git tag
        const gitTag = execSync('git describe --tags --abbrev=0 2>/dev/null', { encoding: 'utf-8' }).trim();
        if (gitTag && gitTag.includes('-')) {
            return gitTag.split('-', 2)[1];
        }

        // Check branch name
        const gitBranch = execSync('git rev-parse --abbrev-ref HEAD 2>/dev/null', { encoding: 'utf-8' }).trim();
        if (gitBranch === 'master' || gitBranch === 'main') {
            return 'release';
        }

        return 'beta.0';
    } catch {
        return 'beta.0';
    }
}

/**
 * Get version name from build_config.py
 */
function getVersionName(rootDir: string): string {
    try {
        const buildConfigPath = path.join(rootDir, 'build_config.py');
        const content = fs.readFileSync(buildConfigPath, 'utf-8');
        const match = content.match(/VERSION_NAME\s*=\s*["']([^"']+)["']/);
        return match ? match[1] : '1.0.0';
    } catch {
        return '1.0.0';
    }
}

/**
 * Get project name from directory name (same as Python's os.path.basename(SCRIPT_PATH))
 */
function getProjectName(rootDir: string): string {
    // Get the last component of the path (directory name)
    return path.basename(rootDir);
}

/**
 * Copy directory recursively
 */
function copyDirRecursive(src: string, dest: string): void {
    if (!fs.existsSync(src)) {
        return;
    }

    fs.mkdirSync(dest, { recursive: true });

    const entries = fs.readdirSync(src, { withFileTypes: true });
    for (const entry of entries) {
        const srcPath = path.join(src, entry.name);
        const destPath = path.join(dest, entry.name);

        if (entry.isDirectory()) {
            copyDirRecursive(srcPath, destPath);
        } else {
            fs.copyFileSync(srcPath, destPath);
        }
    }
}

/**
 * Create ZIP archive from directory using system zip command
 */
function createZipArchive(sourceDir: string, zipPath: string): void {
    const parentDir = path.dirname(sourceDir);
    const dirName = path.basename(sourceDir);

    // Remove existing zip file if it exists
    if (fs.existsSync(zipPath)) {
        fs.unlinkSync(zipPath);
    }

    // Use system zip command: cd to parent dir and zip the directory
    const zipCmd = `cd "${parentDir}" && zip -r "${zipPath}" "${dirName}"`;
    execSync(zipCmd, { stdio: 'pipe' });

    // Get file size
    const stats = fs.statSync(zipPath);
    const sizeMB = (stats.size / 1024 / 1024).toFixed(2);
    console.log(`Archive created: ${zipPath} (${sizeMB} MB)`);
}

/**
 * Archive OHOS build artifacts into ZIP package
 */
function archiveOhosArtifacts(rootDir: string): void {
    console.log('==================Archive OHOS Project========================');

    const versionName = getVersionName(rootDir);
    const versionSuffix = getVersionSuffix();
    const projectName = getProjectName(rootDir);
    const projectNameUpper = projectName.toUpperCase();
    const fullVersion = `${versionName}-${versionSuffix}`;

    console.log(`Project: ${projectName}`);
    console.log(`Version: ${fullVersion}`);

    const binDir = path.join(rootDir, 'bin');
    const ohosMainSdk = path.join(rootDir, 'ohos', 'main_ohos_sdk');

    // Create bin directory if not exists
    fs.mkdirSync(binDir, { recursive: true });

    // Find and copy HAR file
    const harSearchPath = path.join(ohosMainSdk, 'build', 'default', 'outputs', 'default');
    let harFiles: string[] = [];
    if (fs.existsSync(harSearchPath)) {
        harFiles = fs.readdirSync(harSearchPath).filter(f => f.endsWith('.har'));
    }

    if (harFiles.length === 0) {
        console.warn(`WARNING: No HAR file found in ${harSearchPath}`);
    } else {
        const harFile = path.join(harSearchPath, harFiles[0]);
        const harDest = path.join(binDir, `${projectNameUpper}_OHOS_SDK-${fullVersion}.har`);
        fs.copyFileSync(harFile, harDest);
        console.log(`Copied HAR: ${harDest}`);
    }

    // Create archive directory structure
    const archiveName = `(ARCHIVE)_${projectNameUpper}_OHOS_SDK-${fullVersion}`;
    const archiveDir = path.join(binDir, archiveName);

    // Remove existing archive directory if exists
    if (fs.existsSync(archiveDir)) {
        fs.rmSync(archiveDir, { recursive: true, force: true });
    }
    fs.mkdirSync(archiveDir, { recursive: true });

    // Copy symbol libraries (obj/local with debug info)
    const objLocalSrc = path.join(ohosMainSdk, 'obj', 'local');
    if (fs.existsSync(objLocalSrc)) {
        const objLocalDest = path.join(archiveDir, 'obj', 'local');
        copyDirRecursive(objLocalSrc, objLocalDest);
        console.log('Copied symbol libraries: obj/local');
    }

    // Copy libs (stripped release libraries)
    const libsSrc = path.join(ohosMainSdk, 'libs');
    if (fs.existsSync(libsSrc)) {
        const libsDest = path.join(archiveDir, 'libs');
        copyDirRecursive(libsSrc, libsDest);
        console.log('Copied libs: libs');
    }

    // Copy TypeScript/ArkTS source files
    const etsSrc = path.join(ohosMainSdk, 'src', 'main', 'ets');
    if (fs.existsSync(etsSrc)) {
        const etsDest = path.join(archiveDir, 'ets');
        copyDirRecursive(etsSrc, etsDest);
        console.log('Copied ArkTS source: ets');
    }

    // Create ZIP archive
    const zipPath = path.join(binDir, `${archiveName}.zip`);
    createZipArchive(archiveDir, zipPath);

    // Remove temporary archive directory
    fs.rmSync(archiveDir, { recursive: true, force: true });

    console.log('==================Archive Complete========================');
    console.log(`HAR file: ${binDir}/${projectNameUpper}_OHOS_SDK-${fullVersion}.har`);
    console.log(`Archive ZIP: ${zipPath}`);

    // List bin directory contents
    console.log('\nContents of bin directory:');
    const binContents = fs.readdirSync(binDir);
    for (const item of binContents.sort()) {
        const itemPath = path.join(binDir, item);
        const stats = fs.statSync(itemPath);
        if (stats.isFile()) {
            const sizeMB = (stats.size / 1024 / 1024).toFixed(2);
            console.log(`  ${item} (${sizeMB} MB)`);
        }
    }
}

/**
 * Archive plugin that adds the archiveProject task
 */
export function archivePlugin(): HvigorPlugin {
    return {
        pluginId: 'archivePlugin',
        apply(node: HvigorNode) {
            // Register the archiveProject task
            node.registerTask({
                name: 'archiveProject',
                dependencies: [], // No dependencies - we manually execute all steps
                run: async (taskContext) => {
                    console.log('==================Archive OHOS Project========================');

                    // Get project root directory (two levels up from module: ohos/main_ohos_sdk -> ohos -> root)
                    const moduleDir = node.getNodePath();
                    const ohosDir = path.dirname(moduleDir);
                    const rootDir = path.dirname(ohosDir);

                    try {
                        console.log(`Module directory: ${moduleDir}`);
                        console.log(`Working directory: ${rootDir}`);

                        // Step 1: Build native libraries
                        console.log('\n--- Step 1: Building native libraries ---');
                        const nativeCmd = `ccgo build ohos --native-only`;
                        console.log(`Executing: ${nativeCmd} (from ${rootDir})`);

                        execSync(nativeCmd, {
                            cwd: rootDir,
                            stdio: 'inherit',
                        });

                        // Step 2: Package HAR
                        console.log('\n--- Step 2: Packaging HAR ---');
                        const harCmd = `hvigorw assembleHar --mode module -p product=default --no-daemon --info`;
                        console.log(`Executing: ${harCmd} (from ${ohosDir})`);

                        execSync(harCmd, {
                            cwd: ohosDir,
                            stdio: 'inherit',
                        });

                        // Step 3: Archive artifacts (implemented in TypeScript)
                        console.log('\n--- Step 3: Archiving artifacts ---');
                        archiveOhosArtifacts(rootDir);

                        console.log('==================Archive Complete========================');
                    } catch (error) {
                        console.error('Archive failed:', error);
                        throw error;
                    }
                }
            });
        }
    };
}
