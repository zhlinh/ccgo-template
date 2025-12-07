# Apple IDE Integration

This directory provides Xcode integration for the {{cpy_project_name}} project.

## Quick Start

### Opening in Xcode

1. Open `{{cpy_project_relative_path}}.xcworkspace` in Xcode
2. Select a scheme from the scheme selector:
   - `{{cpy_project_name}}-macOS` - Build for macOS
   - `{{cpy_project_name}}-iOS` - Build for iOS
   - `{{cpy_project_name}}-tvOS` - Build for tvOS
   - `{{cpy_project_name}}-watchOS` - Build for watchOS
   - `{{cpy_project_name}}-Tests` - Run unit tests
3. Press `Cmd+B` to build

### Prerequisites

- Xcode 14.0 or later
- ccgo CLI installed (`pip install ccgo`)
- CMake 3.15 or later

## Directory Structure

```
apple/
├── {{cpy_project_relative_path}}.xcworkspace/  # Xcode workspace (open this)
├── {{cpy_project_relative_path}}.xcodeproj/    # Xcode project configuration
├── apple_native_stub/                          # CMake bridge for IDE features
│   └── CMakeLists.txt
└── README.md                                   # This file
```

## How It Works

This Xcode project uses **Legacy Build System** targets that invoke `ccgo` commands:

| Scheme | Command |
|--------|---------|
| {{cpy_project_name}}-macOS | `ccgo build macos` |
| {{cpy_project_name}}-iOS | `ccgo build ios` |
| {{cpy_project_name}}-tvOS | `ccgo build tvos` |
| {{cpy_project_name}}-watchOS | `ccgo build watchos` |
| {{cpy_project_name}}-Tests | `ccgo test` |

The workspace also references the `src/` and `include/` directories for:
- Code navigation and browsing
- IntelliSense / code completion
- Symbol search

## Code Navigation

Xcode will index source files from:
- `../src/` - Source files
- `../include/` - Header files
- `../tests/` - Test files

You can:
- Jump to definition (`Cmd+Click`)
- Find references (`Ctrl+Cmd+J`)
- Use the symbol navigator (`Cmd+Shift+O`)

## Building

### From Xcode

1. Select the target scheme (e.g., `{{cpy_project_name}}-iOS`)
2. Press `Cmd+B` or select Product → Build

### From Command Line

For production builds, use ccgo directly:

```bash
cd ..
ccgo build ios      # Build iOS XCFramework
ccgo build macos    # Build macOS framework
ccgo build tvos     # Build tvOS XCFramework
ccgo build watchos  # Build watchOS XCFramework
```

## Debugging

To debug C++ code:

1. Set breakpoints in source files
2. Select a test scheme (e.g., `{{cpy_project_name}}-Tests`)
3. Press `Cmd+R` to run with debugger

Note: Debugging requires the test binary to be built first.

## Regenerating IDE Project

If you modify `CMakeLists.txt` or add new source files, you may need to regenerate:

```bash
# From the project root directory
ccgo build macos --ide-project
```

## Troubleshooting

### "ccgo command not found"

Ensure ccgo is installed and in your PATH:

```bash
pip install ccgo
which ccgo  # Should show the path
```

If installed but not found, add to your shell profile:
```bash
export PATH="$HOME/.local/bin:$PATH"
```

### Build fails in Xcode

1. Try building from command line first: `ccgo build macos`
2. Check that all prerequisites are installed
3. Verify `CCGO.toml` configuration is correct

### IntelliSense not working

1. Wait for Xcode to finish indexing (check progress in the activity bar)
2. Clean build folder: Product → Clean Build Folder (`Cmd+Shift+K`)
3. Close and reopen the workspace

## See Also

- [CCGO Documentation](https://github.com/user/ccgo)
- [Root README](../README.md)
- [Android Integration](../android/README.md)
