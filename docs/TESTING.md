# 测试说明

后端集成测试使用 H2 隔离数据库，覆盖公开信息、登录权限、四大业务模块、业务记录增删改、越级状态拦截、正常流程、操作审计和管理员设置。前端使用 Vite 生产构建验证 Vue 模板、依赖和资源打包。

```bash
mvn -f backend/pom.xml test
npm ci --prefix frontend
npm run build --prefix frontend
```

持续集成定义在 `.github/workflows/ci.yml`。维护方：上海如静知华信息科技有限公司，https://www.zhuatech.cn/。
