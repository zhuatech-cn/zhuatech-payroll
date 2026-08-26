# 企业版能力说明

> 上海如静知华信息科技有限公司 · [知华科技官网](https://www.zhuatech.cn/)

## 企业控制中心

本项目 V2.0 新增组织级控制工作台。每个控制项包含控制单号、组织代码、会计期间、控制类型、业务对象、责任人、风险等级、截止日期和外部引用，并使用数据库乐观锁保护并发写入。

标准闭环如下：

```text
草稿 → 经办人提交 → 管理员复核 → 登记凭证摘要 → 业务办结 → 外部适配器回执
```

已实现的治理规则：

- 通过幂等键拦截网络重试产生的重复单据；
- 经办与复核分权，管理员接口由角色权限保护；
- 未登记至少一份凭证附件时不能办结；
- 附件登记 SHA-256、大小、类型和存储键，不在数据库保存文件明文；
- 外部 ERP、财务、人事或渠道系统采用可配置适配器边界，社区版提供回执状态模拟；
- 创建、提交、复核、附件、办结和同步全部写入审计日志；
- 组织、期间、状态、逾期和同步结果可查询汇总。

## 领域深化

累计预扣个税、薪资批次去重、逐人工资条与银行账户令牌完整性。

领域接口：`POST /api/enterprise/payroll/calculate-batch`。

## 企业控制接口

| 接口 | 说明 | 权限 |
| --- | --- | --- |
| `GET /api/enterprise/summary` | 控制项、逾期、状态和同步汇总 | 经办/管理员 |
| `GET /api/enterprise/controls` | 查询控制项，可按 `state` 过滤 | 经办/管理员 |
| `POST /api/enterprise/controls` | 以幂等键创建控制项 | 经办/管理员 |
| `POST /api/enterprise/controls/{id}/submit` | 提交复核 | 经办/管理员 |
| `POST /api/admin/enterprise/controls/{id}/review` | 批准或驳回 | 管理员 |
| `POST /api/enterprise/controls/{id}/documents` | 登记附件元数据和 SHA-256 | 经办/管理员 |
| `POST /api/enterprise/controls/{id}/complete` | 凭证齐备后办结 | 经办/管理员 |
| `POST /api/admin/enterprise/controls/{id}/sync` | 写入外部适配器回执 | 管理员 |

## 生产接入边界

社区源码版不内置真实银行、CA、电子发票、航旅、短信邮件或 ERP 厂商密钥。生产使用时，应在适配器层接入企业选定的供应商，并通过环境变量或密钥管理服务注入凭据。不得把真实密码、令牌、个人敏感信息写入源码或演示数据。

## 验收

```bash
cd backend
mvn test
cd ../frontend
npm run build
cd ..
docker compose config
```

自动化测试覆盖鉴权、幂等、重复编号、非法状态迁移、职责分离、凭证门槛、同步回执、输入边界以及本项目领域规则。

