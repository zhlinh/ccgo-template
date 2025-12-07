# Windows IDE Integration

This directory provides Visual Studio and VS Code integration for the {{cpy_project_name}} project.

## Quick Start

### Opening in Visual Studio 2019/2022

**Method 1: Open as CMake Project (Recommended)**

1. Open Visual Studio
2. Select "Open a local folder" or File → Open → Folder
3. Navigate to this `windows/` directory and select it
4. Visual Studio will automatically detect CMakeSettings.json
5. Select a configuration from the dropdown (e.g., "x64-Debug")
6. Press `F7` to build

**Method 2: Generate VS Solution**

```cmd
cd windows
cmake -G "Visual Studio 17 2022" -A x64 -B build
start build\{{cpy_project_name}}.sln
```

### Opening in VS Code

1. Install the "CMake Tools" extension
2. Open VS Code in this `windows/` directory
3. CMake Tools will detect CMakePresets.json
4. Select a preset from the status bar (e.g., "x64 Debug")
5. Press `F7` to build or use the CMake Tools sidebar

### Prerequisites

- Visual Studio 2019/2022 with C++ workload
- CMake 3.15 or later
- ccgo CLI installed (`pip install ccgo`)

## Directory Structure

```
windows/
├── CMakeLists.txt          # CMake entry point for IDE
├── CMakePresets.json       # Presets for VS Code / VS 2022
├── CMakeSettings.json      # Settings for Visual Studio
├── windows_native_stub/    # CMake bridge for IDE features
│   └── CMakeLists.txt
└── README.md               # This file
```

## Configurations

### CMake Presets (VS Code / VS 2022)

| Preset | Description |
|--------|-------------|
| x64-debug | 64-bit Debug build |
| x64-release | 64-bit Release build |
| x86-debug | 32-bit Debug build |
| x86-release | 32-bit Release build |

### Visual Studio Configurations (CMakeSettings.json)

| Configuration | Description |
|---------------|-------------|
| x64-Debug | 64-bit Debug with MSVC |
| x64-Release | 64-bit Release with MSVC |
| x86-Debug | 32-bit Debug with MSVC |
| x86-Release | 32-bit Release with MSVC |

## Building

### From Visual Studio

1. Select configuration from dropdown
2. Build → Build All (`Ctrl+Shift+B`)

### From VS Code

1. Select preset from status bar
2. CMake: Build (`F7`)

### From Command Line

For production builds, use ccgo directly:

```cmd
cd ..
ccgo build windows
```

For quick development builds:

```cmd
cd windows
cmake --preset x64-debug
cmake --build --preset x64-debug-build
```

## IntelliSense

Both Visual Studio and VS Code will provide IntelliSense for:
- Code completion
- Go to definition
- Find all references
- Parameter hints

Source files are indexed from:
- `../src/` - Source files
- `../include/` - Header files
- `../tests/` - Test files

## Debugging

### In Visual Studio

1. Set breakpoints in source files
2. Select a Debug configuration
3. Debug → Start Debugging (`F5`)

### In VS Code

1. Install "C/C++" extension
2. Create launch.json for debugging configuration
3. Set breakpoints and press `F5`

## Troubleshooting

### "CMake error: cannot find compiler"

1. Install Visual Studio with "Desktop development with C++" workload
2. Run from "Developer Command Prompt for VS"

### IntelliSense not working

1. Wait for CMake configuration to complete
2. Check Output panel for CMake errors
3. Try: CMake: Delete Cache and Reconfigure

### Build fails

1. Try building from command line: `ccgo build windows`
2. Check that CMake 3.15+ is installed
3. Verify Visual Studio C++ tools are installed

## See Also

- [CCGO Documentation](https://github.com/user/ccgo)
- [Root README](../README.md)
- [Android Integration](../android/README.md)
- [Apple Integration](../apple/README.md)
