# Design: migrate-docs-mkdoxy

## 独立发布模式

每个项目自己维护文档配置，独立部署到 Read the Docs。

```
┌──────────────────────────────────────────────────────────────────────┐
│                         Documentation Pipeline                        │
└──────────────────────────────────────────────────────────────────────┘

┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   C++ Code  │    │   Doxygen   │    │   MkDoxy    │    │  MkDocs     │
│  /** ... */ │───▶│  (XML out)  │───▶│  (Parser)   │───▶│  Material   │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
                                                                │
                                                                ▼
                                                         ┌─────────────┐
                                                         │ Read the    │
                                                         │ Docs        │
                                                         └─────────────┘
```

## 项目文件结构

```
project/
├── .readthedocs.yaml              # Read the Docs 配置
├── requirements-docs.txt          # Python 依赖
├── docs/
│   ├── mkdocs.yml                 # MkDocs 配置
│   ├── index.md                   # 首页
│   ├── getting-started.md         # 快速开始
│   ├── api/                       # MkDoxy 自动生成 (gitignore)
│   └── assets/
│       └── images/
├── include/                       # C++ 头文件
│   └── project/
│       └── api.h                  # Doxygen 注释
└── src/
```

## 配置文件

### .readthedocs.yaml

```yaml
version: 2

build:
  os: ubuntu-22.04
  tools:
    python: "3.11"
  apt_packages:
    - doxygen
  commands:
    - pip install -r requirements-docs.txt
    - cd docs && mkdocs build --site-dir $READTHEDOCS_OUTPUT/html

mkdocs:
  configuration: docs/mkdocs.yml
```

### requirements-docs.txt

```txt
mkdocs>=1.5.0
mkdocs-material>=9.0.0
mkdoxy>=1.2.0
```

### docs/mkdocs.yml

```yaml
site_name: Project Name
site_url: https://project.readthedocs.io
repo_url: https://github.com/ccgo-group/project

theme:
  name: material
  palette:
    - scheme: default
      primary: indigo
      accent: indigo
      toggle:
        icon: material/brightness-7
        name: Switch to dark mode
    - scheme: slate
      primary: indigo
      accent: indigo
      toggle:
        icon: material/brightness-4
        name: Switch to light mode
  features:
    - navigation.tabs
    - navigation.sections
    - search.suggest
    - search.highlight
    - content.code.copy

plugins:
  - search
  - mkdoxy:
      projects:
        api:
          src-dirs: ../include
          full-doc: True
          doxy-cfg:
            FILE_PATTERNS: "*.h *.hpp"
            RECURSIVE: True
            EXTRACT_ALL: True
            GENERATE_XML: True

markdown_extensions:
  - pymdownx.highlight:
      anchor_linenums: true
  - pymdownx.superfences
  - pymdownx.tabbed:
      alternate_style: true
  - admonition
  - toc:
      permalink: true

nav:
  - Home: index.md
  - Getting Started: getting-started.md
  - API Reference: api/
```

### docs/index.md

```markdown
# Project Name

Brief description of the project.

## Features

- Feature 1
- Feature 2

## Quick Start

\`\`\`cpp
#include <project/api.h>

int main() {
    // Example code
    return 0;
}
\`\`\`

## Installation

See [Getting Started](getting-started.md) for installation instructions.

## API Reference

See [API Reference](api/) for detailed API documentation.
```

## ccgo-template 模板文件

### template/.readthedocs.yaml.jinja

```yaml
version: 2

build:
  os: ubuntu-22.04
  tools:
    python: "3.11"
  apt_packages:
    - doxygen

mkdocs:
  configuration: docs/mkdocs.yml

python:
  install:
    - requirements: requirements-docs.txt
```

### template/docs/mkdocs.yml.jinja

```yaml
site_name: {{ cpy_project_name }}
site_url: https://{{ cpy_project_name | lower }}.readthedocs.io
repo_url: {{ cpy_git_repo_url | git_to_https }}

theme:
  name: material
  palette:
    - scheme: default
      primary: indigo
      toggle:
        icon: material/brightness-7
        name: Switch to dark mode
    - scheme: slate
      primary: indigo
      toggle:
        icon: material/brightness-4
        name: Switch to light mode
  features:
    - navigation.tabs
    - search.suggest
    - content.code.copy

plugins:
  - search
  - mkdoxy:
      projects:
        api:
          src-dirs: ../include
          full-doc: True
          doxy-cfg:
            FILE_PATTERNS: "*.h *.hpp"
            RECURSIVE: True

nav:
  - Home: index.md
  - API Reference: api/
```

## 部署流程

1. 开发者推送代码到 GitHub
2. Read the Docs webhook 触发构建
3. Read the Docs 执行:
   - 安装 Python 依赖
   - MkDoxy 调用 Doxygen 生成 XML
   - MkDoxy 解析 XML 生成 Markdown
   - MkDocs 渲染 HTML
4. 文档发布到 `project.readthedocs.io`

## 优势

| 特性 | 说明 |
|------|------|
| **独立维护** | 每个项目自己管理文档 |
| **自动同步** | 代码变更自动触发文档更新 |
| **无额外仓库** | 不需要中央 ccgo-docs 仓库 |
| **简单 CI** | Read the Docs 自动处理构建 |
