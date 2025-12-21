# Proposal: Migrate Documentation to MkDoxy + MkDocs-Material + Read the Docs

## Change ID
`migrate-docs-mkdoxy`

## Status
`draft`

## Summary
Migrate ccgo project documentation from Doxygen-only HTML output to a modern pipeline:
**Doxygen → MkDoxy → MkDocs-Material → Read the Docs**

This enables:
- Modern, responsive UI with dark mode support
- Centralized documentation for all ccgo-group projects
- Multi-project support without GitHub Pages per-project limitation
- Better search and navigation

## Motivation

### Current Problems
1. Each project requires its own GitHub Pages deployment
2. Doxygen HTML output looks dated
3. No unified documentation portal for ccgo-group projects
4. Limited search and cross-project navigation

### Goals
1. Create a centralized `ccgo-docs` repository hosting all project documentation
2. Replace Doxygen HTML with MkDocs-Material UI
3. Deploy to Read the Docs with subproject support
4. Maintain existing Doxygen comment syntax (no migration required)

## Scope

### In Scope
- New `ccgo-docs` repository setup
- MkDoxy + MkDocs-Material configuration
- Read the Docs integration
- Update `ccgo-template` docs structure
- Update `ccgo build docs` command

### Out of Scope
- Changing Doxygen comment syntax in existing projects
- Migrating existing GitHub Pages deployments (can coexist)

## Architecture

**独立发布模式**: 每个项目自己生成文档，独立部署到 Read the Docs。

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    ccgonow/     │     │  ccgonowdep/    │     │  ccgo-template/ │
│  docs/          │     │  docs/          │     │  docs/          │
│  ├── mkdocs.yml │     │  ├── mkdocs.yml │     │  ├── mkdocs.yml │
│  └── ...        │     │  └── ...        │     │  └── ...        │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Read the Docs                            │
├─────────────────────────────────────────────────────────────────┤
│  ccgonow.readthedocs.io/                                         │
│  ccgonowdep.readthedocs.io/                                      │
│  ccgo-template.readthedocs.io/                                   │
│                                                                  │
│  或使用子项目模式:                                                │
│  ccgo.readthedocs.io/projects/ccgonow/                           │
│  ccgo.readthedocs.io/projects/ccgonowdep/                        │
└─────────────────────────────────────────────────────────────────┘
```

**优势**:
- 无需中央仓库，各项目独立维护
- CI/CD 更简单，在各项目内配置
- 文档与代码同步更新

## Alternatives Considered

| Alternative | Pros | Cons | Decision |
|-------------|------|------|----------|
| Doxygen + GitHub Pages | Simple, existing | Per-project Pages, dated UI | Reject |
| Doxygen Awesome theme | Minimal change | Still per-project, no portal | Reject |
| Doxide (replace Doxygen) | Modern, native MkDocs | Less mature, migration effort | Reject |
| **MkDoxy (bridge)** | Keep Doxygen, modern UI, portal | Additional tooling | **Accept** |

## Dependencies

### External
- `mkdocs` - Static site generator
- `mkdocs-material` - Material theme
- `mkdoxy` - Doxygen to MkDocs bridge
- `doxygen` - C++ documentation generator (existing)

### Internal
- `ccgo-template` - Update docs template structure
- `ccgo` CLI - Update `build docs` command

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| MkDoxy compatibility issues | Medium | Medium | Test with ccgonow first |
| Read the Docs build limits | Low | Low | Incremental builds |
| Learning curve | Low | Low | Existing Doxygen knowledge applies |

## Success Criteria

1. [ ] `ccgo-docs` repository created and deployed to Read the Docs
2. [ ] At least 2 projects (ccgonow, ccgonowdep) documentation visible
3. [ ] Search works across all projects
4. [ ] Dark mode and responsive design functional
5. [ ] CI/CD pipeline for automatic doc updates
