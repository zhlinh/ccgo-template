# Tasks: migrate-docs-mkdoxy

## Phase 1: 更新 ccgo-template 文档结构 ✅

- [x] **1.1** 添加 MkDocs 配置模板 `mkdocs.yml.jinja` (项目根目录)
- [x] **1.2** 添加 Read the Docs 配置 `.readthedocs.yaml.jinja`
- [x] **1.3** 添加 `docs/requirements.txt`
- [x] **1.4** 创建 `docs/index.md.jinja` 首页模板
- [x] **1.5** 创建 `docs/getting-started.md.jinja` 快速开始模板
- [x] **1.6** 保留现有 Doxyfile 配置 (MkDoxy 会使用)

## Phase 2: 在 ccgonow 项目测试 ✅

- [x] **2.1** 在 ccgonow 添加 MkDocs 配置
- [x] **2.2** 添加 `.readthedocs.yaml`
- [x] **2.3** 本地测试: `mkdocs build` 成功
- [x] **2.4** 验证 API 文档正确生成

## Phase 3: 部署到 Read the Docs (待完成)

- [ ] **3.1** 注册 Read the Docs 账号
- [ ] **3.2** 导入 ccgonow 项目
- [ ] **3.3** 配置构建设置
- [ ] **3.4** 验证部署: `ccgonow.readthedocs.io`
- [ ] **3.5** 测试自动重建 (push 触发)

## Phase 4: 应用到其他项目 (待完成)

- [ ] **4.1** 为 ccgonowdep 添加相同配置
- [ ] **4.2** 部署 ccgonowdep 到 Read the Docs

## 文件结构 (每个项目)

```
project/
├── .readthedocs.yaml          # Read the Docs 配置
├── mkdocs.yml                 # MkDocs 配置 (项目根目录)
├── docs/
│   ├── requirements.txt       # Python 依赖
│   ├── index.md               # 首页
│   ├── getting-started.md     # 快速开始
│   └── api/                   # MkDoxy 自动生成
└── include/                   # C++ 头文件 (Doxygen 注释)
```

## 已创建的模板文件

### ccgo-template
- `template/{{cpy_project_relative_path}}/mkdocs.yml.jinja`
- `template/{{cpy_project_relative_path}}/.readthedocs.yaml.jinja`
- `template/{{cpy_project_relative_path}}/docs/requirements.txt`
- `template/{{cpy_project_relative_path}}/docs/index.md.jinja`
- `template/{{cpy_project_relative_path}}/docs/getting-started.md.jinja`

### ccgonow (测试项目)
- `ccgonow/mkdocs.yml`
- `ccgonow/.readthedocs.yaml`
- `ccgonow/docs/requirements.txt`
- `ccgonow/docs/index.md`
- `ccgonow/docs/getting-started.md`
