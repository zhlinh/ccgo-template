# CCGO.toml Configuration Guide

CCGO uses a TOML-based configuration file (`CCGO.toml`) similar to Cargo.toml for Rust projects. This file contains all build and package configuration for your cross-platform C++ library.

## File Location

The `CCGO.toml` file should be placed in your project's root directory (alongside `CMakeLists.txt` and build scripts).

## Configuration Sections

### [project]

Project metadata and basic information.

```toml
[project]
name = "mylib"              # Project name (lowercase recommended)
version = "1.0.0"           # Semantic versioning
description = "My awesome library"
repository = "https://github.com/user/mylib"
authors = ["Name <email@example.com>"]
license = "MIT"
```

**Fields:**
- `name` (required): Project name, used for library naming and package generation
- `version` (required): Project version in semantic versioning format (MAJOR.MINOR.PATCH)
- `description` (optional): Brief description of the project
- `repository` (optional): Git repository URL
- `authors` (optional): List of authors
- `license` (optional): License type (MIT, Apache-2.0, etc.)

### [build]

Build system configuration.

```toml
[build]
output_dir = "cmake_build"                    # CMake build output directory
verinfo_path = "include/mylib/base/"         # Version info header path
```

**Fields:**
- `output_dir`: Directory where CMake generates build artifacts (default: `cmake_build`)
- `verinfo_path`: Path where version information header will be generated

### [android]

Android-specific build configuration.

```toml
[android]
project_path = "android/main_android_sdk"    # Android Gradle project path
merge_libs = []                               # Third-party libs to merge
exclude_libs = []                             # Libs to exclude from merging
default_archs = ["armeabi-v7a", "arm64-v8a", "x86_64"]  # Target architectures
```

**Fields:**
- `project_path`: Path to Android Gradle project
- `merge_libs`: List of third-party libraries to merge into output library
- `exclude_libs`: List of libraries to exclude from merging
- `default_archs`: Default architectures for Android builds

**Supported Architectures:**
- `armeabi-v7a`: 32-bit ARM
- `arm64-v8a`: 64-bit ARM
- `x86`: 32-bit x86 (Intel/AMD)
- `x86_64`: 64-bit x86 (Intel/AMD)

### [ohos]

OpenHarmony (HarmonyOS) specific configuration.

```toml
[ohos]
project_path = "ohos/main_ohos_sdk"
merge_libs = []
exclude_libs = []
default_archs = ["armeabi-v7a", "arm64-v8a", "x86_64"]
```

Configuration is similar to Android.

### [ios]

iOS-specific configuration.

```toml
[ios]
export_headers = [
    { src = "include/mylib/base/version.h", dest = "base" },
    { src = "include/mylib/api/ios/MyLib.h", dest = "api" },
]
```

**Fields:**
- `export_headers`: List of header files to export in the framework
  - `src`: Source path of the header file
  - `dest`: Destination directory in the framework

### [macos]

macOS-specific configuration (similar to iOS).

```toml
[macos]
export_headers = [
    { src = "include/mylib/base/version.h", dest = "base" },
    { src = "include/mylib/api/macos/MyLib.h", dest = "api" },
]
```

### [windows]

Windows-specific configuration.

```toml
[windows]
export_headers = [
    { src = "include/mylib/base/version.h", dest = "base" },
    { src = "include/mylib/api/native/mylib.h", dest = "api" },
]
```

### [linux]

Linux-specific configuration.

```toml
[linux]
export_headers = [
    { src = "include/mylib/base/version.h", dest = "base" },
    { src = "include/mylib/api/native/mylib.h", dest = "api" },
]
```

### [include]

Configuration for SDK packaging include directory.

```toml
[include]
export_headers = [
    { src = "include/", dest = "." },
]
```

This section defines which headers to include when packaging the SDK.

## Example Complete Configuration

```toml
# CCGO Project Configuration

[project]
name = "myawesome"
version = "1.2.3"
description = "My Awesome Cross-Platform Library"
repository = "https://github.com/myuser/myawesome"
authors = ["John Doe <john@example.com>"]
license = "MIT"

[build]
output_dir = "cmake_build"
verinfo_path = "include/myawesome/base/"

[android]
project_path = "android/main_android_sdk"
merge_libs = ["third_party_lib"]
exclude_libs = []
default_archs = ["armeabi-v7a", "arm64-v8a", "x86_64"]

[ohos]
project_path = "ohos/main_ohos_sdk"
merge_libs = []
exclude_libs = []
default_archs = ["armeabi-v7a", "arm64-v8a"]

[ios]
export_headers = [
    { src = "include/myawesome/base/version.h", dest = "base" },
    { src = "include/myawesome/api/apple/MyAwesome.h", dest = "api" },
    { src = "include/myawesome/api/ios/MyAwesome.h", dest = "api" },
]

[macos]
export_headers = [
    { src = "include/myawesome/base/version.h", dest = "base" },
    { src = "include/myawesome/api/apple/MyAwesome.h", dest = "api" },
    { src = "include/myawesome/api/macos/MyAwesome.h", dest = "api" },
]

[windows]
export_headers = [
    { src = "include/myawesome/base/version.h", dest = "base" },
    { src = "include/myawesome/api/native/myawesome.h", dest = "api" },
]

[linux]
export_headers = [
    { src = "include/myawesome/base/version.h", dest = "base" },
    { src = "include/myawesome/api/native/myawesome.h", dest = "api" },
]

[include]
export_headers = [
    { src = "include/", dest = "." },
]
```

## Migration from build_config.py

**IMPORTANT**: CCGO now exclusively uses `CCGO.toml` for configuration. The legacy `build_config.py` format is no longer supported.

### Migration Steps:

1. Create a new `CCGO.toml` file in your project root
2. Copy the example configuration above and adjust values
3. Map Python variables to TOML sections:
   - `PROJECT_NAME` / `PROJECT_NAME_LOWER` → `[project] name`
   - `CONFIG_PROJECT_VERSION` → `[project] version`
   - `ANDROID_PROJECT_PATH` → `[android] project_path`
   - `ANDROID_MERGE_THIRD_PARTY_LIBS` → `[android] merge_libs`
   - `ANDROID_MERGE_EXCLUDE_LIBS` → `[android] exclude_libs`
   - Header file mappings → `export_headers` arrays
4. Test the build and package commands
5. Remove the old `build_config.py` file

### Example Migration:

**Old build_config.py:**
```python
PROJECT_NAME = "MYLIB"
PROJECT_NAME_LOWER = "mylib"
CONFIG_PROJECT_VERSION = "1.0.0"

ANDROID_PROJECT_PATH = "android/main_android_sdk"
ANDROID_MERGE_THIRD_PARTY_LIBS = []
ANDROID_MERGE_EXCLUDE_LIBS = []
```

**New CCGO.toml:**
```toml
[project]
name = "mylib"
version = "1.0.0"

[android]
project_path = "android/main_android_sdk"
merge_libs = []
exclude_libs = []
default_archs = ["armeabi-v7a", "arm64-v8a", "x86_64"]
```

### What if CCGO.toml is missing?

If CCGO cannot find a `CCGO.toml` file, it will:
1. Display a warning message
2. Use default values:
   - Project name: "SDK"
   - Version: Current date (YYYYMMDD format) or git describe output
3. Continue with the build/package operation

This allows you to test CCGO commands, but for production use, **you must create a CCGO.toml file**.

## Usage with CCGO Commands

The `CCGO.toml` configuration is automatically read by all CCGO commands:

```bash
# Build commands use architecture defaults from TOML
ccgo build android

# Package command uses platforms list from TOML
ccgo package

# Override TOML version if needed
ccgo package --version 2.0.0

# Override platforms if needed
ccgo package --platforms android,ios
```

## Validation

CCGO will validate your `CCGO.toml` file when reading it. Common errors:

- **Missing required fields**: `name` and `version` in `[project]` section
- **Invalid version format**: Must be semantic versioning (e.g., "1.0.0")
- **Invalid platform names**: Must be one of the supported platforms
- **Invalid archive format**: Must be "zip", "tar.gz", or "both"

## Best Practices

1. **Version Control**: Always commit `CCGO.toml` to version control
2. **Version Format**: Use semantic versioning (MAJOR.MINOR.PATCH)
3. **Project Name**: Use lowercase names without spaces
4. **Platform Selection**: Only include platforms you actually support
5. **Header Exports**: Keep header exports minimal and organized
6. **Documentation**: Update `description` field when project changes

## Dependencies

CCGO supports dependency management similar to Cargo, allowing you to specify dependencies from git repositories or local paths.

### [dependencies]

Define dependencies that are included in all builds.

```toml
[dependencies]
# Git dependency with default branch
my_lib = { git = "https://github.com/user/my_lib.git" }

# Git dependency with specific branch
feature_lib = { git = "https://github.com/user/feature_lib.git", branch = "develop" }

# Git dependency with specific tag
stable_lib = { git = "https://github.com/user/stable_lib.git", tag = "v1.2.3" }

# Git dependency with specific commit (SHA1)
pinned_lib = { git = "https://github.com/user/pinned_lib.git", rev = "0c0990399270277832fbb5b91a1fa118e6f63dba" }

# Git dependency with PR reference (HEAD commit of PR 493)
pr_lib = { git = "https://github.com/user/pr_lib.git", rev = "refs/pull/493/head" }

# Local path dependency (relative to project root)
local_lib = { path = "vendor/local_lib" }

# Local path dependency with version
versioned_local = { path = "../shared_lib", version = "2.0.0" }
```

**Dependency Types:**

1. **Git Dependencies:**
   - `git`: Repository URL (required)
   - `branch`: Specific branch name (optional)
   - `tag`: Specific tag name (optional)
   - `rev`: Specific commit SHA, tag, or ref like `refs/pull/493/head` (optional)
   - If none specified, uses the repository's default branch

2. **Path Dependencies:**
   - `path`: Relative or absolute path to the dependency (required)
   - `version`: Optional version specification for documentation

### Platform-Specific Dependencies

Use `[target.'cfg(...)'.dependencies]` sections to specify dependencies only for certain platforms.

```toml
[target.'cfg(windows)'.dependencies]
winhttp = { git = "https://github.com/user/winhttp.git", tag = "0.4.0" }

[target.'cfg(unix)'.dependencies]
openssl = { git = "https://github.com/user/openssl.git", tag = "1.0.1" }

[target.'cfg(target_arch = "x86")'.dependencies]
native-i686 = { path = "native/i686" }
```

**Platform Configuration Patterns:**

- `cfg(windows)`: Windows platforms
- `cfg(unix)`: Unix-like platforms (Linux, macOS, iOS)
- `cfg(linux)`: Linux only
- `cfg(macos)`: macOS only
- `cfg(ios)`: iOS only
- `cfg(android)`: Android only
- `cfg(target_arch = "x86")`: x86 architecture (32-bit)
- `cfg(target_arch = "x86_64")`: x86_64 architecture (64-bit)
- `cfg(target_arch = "arm")`: ARM architecture

### Dependency Resolution

When you build your project, CCGO will:

1. Parse dependencies from `CCGO.toml`
2. Download git dependencies to `third_party/.cache/`
3. Checkout the specified branch/tag/commit
4. Create symlinks in `third_party/` directory
5. Generate `CCGO.lock` file with resolved dependency information
6. Pass dependency paths to CMake via `CCGO_DEP_PATHS` and `CCGO_DEP_INCLUDE_DIRS`

**Lock File:**

CCGO creates a `CCGO.lock` file that records:
- Exact commit hashes for git dependencies
- Resolved paths for all dependencies
- This ensures reproducible builds

**Using Dependencies in CMake:**

Dependencies are automatically made available to your CMake build:

```cmake
# Include CCGO dependencies module
include(${CCGO_CMAKE_DIR}/CCGODependencies.cmake)

# Add dependencies to your target
ccgo_add_dependencies(my_target)

# Or add a dependency as subdirectory (if it has CMakeLists.txt)
ccgo_add_subdirectory(my_lib)

# Or link a specific library
ccgo_link_dependency(my_target my_lib my_lib_name)

# Print dependency information for debugging
ccgo_print_dependencies()
```

### Example with Dependencies

```toml
[project]
name = "myapp"
version = "1.0.0"

[dependencies]
# Use a common C++ library
spdlog = { git = "https://github.com/gabime/spdlog.git", tag = "v1.12.0" }
json = { git = "https://github.com/nlohmann/json.git", tag = "v3.11.2" }

# Local vendor library
utils = { path = "vendor/utils" }

[target.'cfg(windows)'.dependencies]
# Windows-specific library
wil = { git = "https://github.com/microsoft/wil.git" }

[target.'cfg(unix)'.dependencies]
# Unix-specific library (for both Linux and macOS)
libcurl = { path = "vendor/curl-unix" }
```

Then in your `CMakeLists.txt`:

```cmake
# Include CCGO dependencies
include(${CCGO_CMAKE_DIR}/CCGODependencies.cmake)

# Create your library
add_library(myapp STATIC
    src/main.cpp
    src/utils.cpp
)

# Add CCGO dependencies (includes spdlog, json, utils, and platform-specific ones)
ccgo_add_dependencies(myapp)

# Or add specific dependencies as subdirectories
ccgo_add_subdirectory(spdlog)
target_link_libraries(myapp PRIVATE spdlog::spdlog)
```

## Future Enhancements

Planned features for `CCGO.toml`:

- Version-based dependencies with a package registry
- Custom build scripts
- Platform-specific compile flags
- Test configuration
- Benchmark configuration
- Plugin system configuration
