# 架构说明

```text
Vue 3 管理端 / 响应式业务端
            │ HTTP / JSON
Spring Security → Controller → PayrollService → Spring Data JPA → MySQL 8
                                       ├→ 业务状态机
                                       ├→ 操作审计
                                       └→ 持久化设置
```

领域目录定义四个业务模块、字段语义和允许的状态动作；服务层统一执行模块校验、编号唯一性、状态流转、权限边界和审计记录。生产化可在现有接口后对接企业微信、钉钉、电子发票、CA、银行、ERP 或其他第三方服务，社区源码版不内置外部密钥。

Java 根包：`cn.zhuatech.payroll`。公司官网：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)
