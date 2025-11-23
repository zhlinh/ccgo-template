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

### [package]

SDK packaging configuration.

```toml
[package]
platforms = ["android", "ios", "macos", "windows", "linux", "ohos"]
archive_format = "zip"
include_docs = false
include_samples = false
include_kmp = false
```

**Fields:**
- `platforms`: List of platforms to include in the SDK package
- `archive_format`: Archive format (`zip`, `tar.gz`, or `both`)
- `include_docs`: Whether to include documentation
- `include_samples`: Whether to include sample code
- `include_kmp`: Whether to include Kotlin Multiplatform artifacts

**Supported Platforms:**
- `android`: Android AAR packages
- `ios`: iOS frameworks/xcframeworks
- `macos`: macOS frameworks
- `windows`: Windows static libraries (.lib)
- `linux`: Linux static libraries (.a)
- `ohos`: OpenHarmony HAR packages
- `kmp`: Kotlin Multiplatform packages

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

[package]
platforms = ["android", "ios", "macos", "windows", "linux"]
archive_format = "zip"
include_docs = true
include_samples = true
include_kmp = false
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

## Future Enhancements

Planned features for `CCGO.toml`:

- Dependency management
- Custom build scripts
- Platform-specific compile flags
- Test configuration
- Benchmark configuration
- Plugin system configuration
