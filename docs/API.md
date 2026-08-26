# 薪酬管理系统 API

Base URL：`http://localhost:8080/api`。公开接口无需认证，其余接口使用 HTTP Basic；管理员接口要求 `ADMIN` 权限。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/public/about` | 公司、产品、官网和许可信息 |
| GET | `/catalog` | 模块、字段标签和允许的流程动作 |
| GET | `/dashboard` | 总量、金额、状态与模块分布 |
| GET | `/records?module=CODE` | 查询全部或指定模块业务记录 |
| POST | `/records` | 新建初始状态业务记录 |
| PUT | `/records/{id}` | 修改初始状态记录 |
| DELETE | `/records/{id}` | 删除初始状态记录 |
| POST | `/records/{id}/actions` | 执行业务状态流转并校验前置状态 |
| POST | `/insights/payroll` | 薪资实发计算 |
| GET | `/admin/audit-logs` | 查询最近 100 条操作日志 |
| GET/PUT | `/admin/settings` | 查询或更新持久化系统参数 |

流程动作：`CALCULATE、APPROVE、PAY`。服务端拒绝越级流转并返回 HTTP 409。

上海如静知华信息科技有限公司：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)
