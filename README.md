# ccgo-template

Official [Copier](https://github.com/copier-org/copier) template for creating cross-platform C++ projects with [ccgo](https://github.com/zhlinh/ccgo).

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/license/MIT)

## Overview

This template generates a complete cross-platform C++ library project with:

- 🏗️ **Multi-platform support**: Android, iOS, macOS, Windows, Linux, OpenHarmony (OHOS)
- 📦 **Kotlin Multiplatform (KMP)**: Ready-to-use KMP wrapper for native libraries
- 🧪 **Testing framework**: GoogleTest integration with `ccgo test`
- 📊 **Benchmarking**: Google Benchmark support with `ccgo bench`
- 📚 **Documentation**: Doxygen configuration with `ccgo doc`
- 🔧 **Build automation**: Platform-specific build scripts and CI/CD orchestration
- 🎨 **Code formatting**: Pre-configured with `.clang-format` and lint tools
- 🚀 **Publishing**: Maven and OHPM publishing support

## Quick Start

### Prerequisites

Ensure ccgo is installed:

```bash
pip3 install ccgo
```

### Create a New Project

```bash
# Create a new project with interactive prompts
ccgo new my-awesome-lib

# Create with default values
ccgo new my-awesome-lib --defaults

# Create with custom template variables
ccgo new my-awesome-lib --data cpy_project_version=2.0.0
```

### Initialize in Existing Directory

```bash
# Initialize in current directory
ccgo init

# Initialize with defaults, skip confirmation
ccgo init --defaults --force
```

### Use Custom Template

```bash
# From GitHub repository
ccgo new my-project --template-url=https://github.com/user/custom-template.git

# From local directory
ccgo new my-project --template-url=/path/to/local/template
```

## Template Variables

The template uses the following configurable variables (defined in `copier.yml`):

### Project Information

| Variable | Description | Example |
|----------|-------------|---------|
| `cpy_project_name` | Project name (letters, digits, dashes) | `CCGO`, `MyLib` |
| `cpy_project_relative_path` | Project subdirectory name | `mylib`, `ccgo` |
| `cpy_project_version` | Initial version (semantic versioning) | `1.0.0` |
| `cpy_project_group_id` | Maven-style group ID | `com.company.project` |
| `cpy_project_dependencies` | Comma-separated dependencies | `com.ccgo.sample:sample:1.0.0` |

### User & Git Information

| Variable | Description | Example |
|----------|-------------|---------|
| `cpy_user_name` | Git user name (auto-detected) | `zhlinh` |
| `cpy_user_email` | Git user email (auto-detected) | `user@example.com` |
| `cpy_git_base_host` | Git hosting domain | `github.com`, `gitlab.com` |
| `cpy_git_repo_url` | Full repository URL | `git@github.com:user/repo.git` |
| `cpy_document_url` | Documentation URL | `https://user-project.pages.github.io` |

### Project Options

| Variable | Description | Default |
|----------|-------------|---------|
| `cpy_with_include_src_tests` | Include `src/`, `include/`, `tests/` directories | `True` |


## Template Features

### 1. Kotlin Multiplatform Support

The template includes a complete KMP module with:
- **Multi-target support**: Android, iOS, macOS, Linux, Windows
- **C interop**: Pre-configured `.def` files for native bindings
- **Platform-specific implementations**: Separate source sets for each platform
- **Migration guides**: Documentation for transitioning to native libraries

### 2. Testing & Benchmarking

- **GoogleTest**: Pre-configured test infrastructure
- **Google Benchmark**: Performance testing framework
- **Platform coverage**: Tests run on native platforms (macOS, Linux, Windows)

### 3. Documentation Generation

- **Doxygen**: Multi-language documentation support (English, Chinese)
- **GitHub Pages**: Ready-to-publish with `build_pages.py`
- **Custom domains**: CNAME configuration

### 4. Code Quality Tools

- **Clang-Format**: Consistent C++ code formatting
- **CPPLINT**: Google C++ style guide enforcement
- **Git hooks**: Automatic lint checking on commit (optional)

## Template Customization

### Jinja2 Extensions

The template uses custom Jinja2 extensions (defined in `copier_extensions.py`):

- **CurrentYearExtension**: `{{ current_year() }}` - Get current year
- **GitExtension**: `{{ git_user_name() }}`, `{{ git_user_email() }}` - Extract git config
- **SlugifyExtension**: Convert strings to URL-safe slugs

### Custom Variables

Pass custom variables when creating projects:

```bash
ccgo new my-project \
  --data cpy_project_version=2.5.0 \
  --data cpy_project_group_id=io.github.myorg \
  --data cpy_with_include_src_tests=false
```

### Template Inheritance

Create your own template by forking this repository:

1. Clone the template:
   ```bash
   git clone https://github.com/zhlinh/ccgo-template.git my-custom-template
   cd my-custom-template
   ```

2. Modify `copier.yml` to add/remove questions

3. Edit files in `template/` directory

4. Use your custom template:
   ```bash
   ccgo new my-project --template-url=/path/to/my-custom-template
   ```

## Updating Projects

Update existing projects when the template changes:

```bash
cd my-project
copier update
```

This will:
- Pull the latest template changes
- Preserve your local modifications
- Prompt for conflict resolution if needed
- Skip files listed in `_skip_if_exists` (configured in `copier.yml`)

## Post-Generation Setup

After creating a project, follow these steps:

### 1. Navigate to Project

```bash
cd my-project/{project_relative_path}
```

### 2. Initialize Git Submodules (for lint tools)

```bash
git submodule update --init --recursive
lint/install.sh cpp
```

### 3. Build for Your Platform

```bash
# Android
ccgo build android

# iOS
ccgo build ios

# Run tests
ccgo test

# Build documentation
ccgo doc --open
```

## Environment Requirements

### Android
- **Android SDK**: Set `ANDROID_HOME`
- **Android NDK**: Set `ANDROID_NDK_HOME`
- **Java**: Set `JAVA_HOME` (JDK 11+)

### iOS/macOS
- **Xcode**: Install from App Store
- **Command-line tools**: `xcode-select --install`

### OpenHarmony (OHOS)
- **OHOS SDK**: Set `OHOS_SDK_HOME` or `HOS_SDK_HOME`
- **hvigor**: Install OHOS build tools
- **ohpm**: Install OHOS package manager

### Windows
- **Visual Studio**: 2019 or later with C++ workload
- **CMake**: 3.20 or later

### Linux
- **GCC/Clang**: C++17 compatible compiler
- **CMake**: 3.20 or later

## Template Files Reference

### Build Scripts

| File | Purpose |
|------|---------|
| `CCGO.toml` | Project configuration (name, version, dependencies) |

### CMake Files

| File | Purpose |
|------|---------|
| `CMakeLists.txt` | Root CMake configuration |

### Platform-Specific

| Directory | Purpose |
|-----------|---------|
| `android/` | Gradle-based Android library module |
| `ohos/` | hvigor-based OpenHarmony module |
| `kmp/` | Kotlin Multiplatform library wrapper |
| `examples/` | Sample applications for each platform |

## Troubleshooting

### Template Generation Fails

**Error**: `copier: command not found`
```bash
pip3 install copier
```

**Error**: `Jinja2 extension not found`
```bash
# Use --trust flag for custom Jinja extensions
ccgo new my-project --template-url=./ccgo-template --defaults
# Or with copier directly:
copier copy ccgo-template/ my-project/ --trust
```

### Post-Generation Issues

**Git initialization fails**
- Ensure git is installed and configured
- Check `cpy_git_repo_url` is valid

**Submodule update fails**
- Check network connectivity
- Verify submodule URLs in `.gitmodules`

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes to files in `template/`
4. Test the template: `copier copy . test-output/ --trust`
5. Submit a pull request

## License

ccgo-template is available under the [MIT license](https://opensource.org/license/MIT).
See the LICENSE file for the full license text.

## Links

- [ccgo CLI](https://github.com/zhlinh/ccgo) - The build system CLI
- [Copier Documentation](https://copier.readthedocs.io/) - Template engine
- [Issue Tracker](https://github.com/zhlinh/ccgo-template/issues) - Report bugs
- [PyPI Package](https://pypi.org/project/ccgo/) - Install ccgo
