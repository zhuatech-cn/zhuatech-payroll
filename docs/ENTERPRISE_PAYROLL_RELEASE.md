# 企业级薪资批次发放治理

薪资发放前检查员工覆盖、银行令牌、个税社保、总额波动、职责分离、双负责人审批和付款文件签名。

`POST /api/enterprise/payroll/run-release` 返回 `RELEASE / APPROVAL_REQUIRED / HOLD`，生产使用应关联薪资快照、授权矩阵和银行回执。
