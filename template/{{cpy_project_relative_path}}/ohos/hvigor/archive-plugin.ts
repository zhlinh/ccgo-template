/**
 * Custom Hvigor plugin for building OHOS HAR package.
 *
 * This plugin provides a 'buildHAR' task that builds native libraries and
 * packages the HAR file. ZIP archive creation is handled by Python's
 * archive_ohos_project() for unified structure across all platforms.
 *
 * Usage:
 *   hvigorw buildHAR
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
 * Copy HAR file to target/ohos/ directory
 * ZIP archive creation is handled by Python's archive_ohos_project()
 */
function copyHarToTarget(rootDir: string): void {
    console.log('==================Copy HAR to Target========================');

    const versionName = getVersionName(rootDir);
    const versionSuffix = getVersionSuffix();
    const projectName = getProjectName(rootDir);
    const projectNameUpper = projectName.toUpperCase();
    const fullVersion = `${versionName}-${versionSuffix}`;

    console.log(`Project: ${projectName}`);
    console.log(`Version: ${fullVersion}`);

    // Output to target/ohos/ directory
    const targetDir = path.join(rootDir, 'target', 'ohos');
    const ohosMainSdk = path.join(rootDir, 'ohos', 'main_ohos_sdk');

    // Create target/ohos directory if not exists
    fs.mkdirSync(targetDir, { recursive: true });

    // Find and copy HAR file to targetDir
    const harSearchPath = path.join(ohosMainSdk, 'build', 'default', 'outputs', 'default');
    const harFilename = `${projectNameUpper}_OHOS_SDK-${fullVersion}.har`;
    let harFiles: string[] = [];
    if (fs.existsSync(harSearchPath)) {
        harFiles = fs.readdirSync(harSearchPath).filter(f => f.endsWith('.har'));
    }

    if (harFiles.length === 0) {
        console.warn(`WARNING: No HAR file found in ${harSearchPath}`);
    } else {
        const harFile = path.join(harSearchPath, harFiles[0]);
        // Copy to targetDir (Python will move to haars/ in unified archive)
        const harDest = path.join(targetDir, harFilename);
        fs.copyFileSync(harFile, harDest);
        console.log(`Copied HAR: ${harDest}`);
    }

    console.log('==================HAR Build Complete========================');
    console.log(`HAR file: ${targetDir}/${harFilename}`);
    console.log('Note: Use Python\'s archive_ohos_project() to create unified ZIP archive');
}

/**
 * Archive plugin that adds the buildHAR task
 * Note: Renamed from archiveProject to buildHAR
 * ZIP archive creation is now handled by Python's archive_ohos_project()
 */
export function archivePlugin(): HvigorPlugin {
    return {
        pluginId: 'archivePlugin',
        apply(node: HvigorNode) {
            // Register the buildHAR task (renamed from archiveProject)
            node.registerTask({
                name: 'buildHAR',
                dependencies: [], // No dependencies - we manually execute all steps
                run: async (taskContext) => {
                    console.log('==================Build OHOS HAR========================');

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

                        // Step 3: Copy HAR to target/ohos/
                        console.log('\n--- Step 3: Copying HAR to target ---');
                        copyHarToTarget(rootDir);

                        console.log('==================Build HAR Complete========================');
                    } catch (error) {
                        console.error('Build HAR failed:', error);
                        throw error;
                    }
                }
            });
        }
    };
}
