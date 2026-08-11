# scripts_dir

根目录统一脚本目录。

## 约定

- 通用脚本统一放这里，不再分散到各子项目根目录。
- 子项目私有脚本仍可保留在各自 `bin/` 下，但推荐逐步迁移到这里。
- Go 服务统一通过 `build-go-service.sh` 构建，可复用 `~/.g/go/bin/go`。
- Python 服务统一使用项目内 `.venv`。

## 当前脚本

- `build-go-service.sh`
- `bootstrap-python-service.sh`
- `start-cpo-api-gateway.sh`
- `start-cpo-ai-productivity-service.sh`
- `stop-cpo-api-gateway.sh`
- `stop-service.sh`
- `stop-python-service.sh`
- `validate-cpo-api-gateway.sh`
