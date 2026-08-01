# IDP API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 文档智能运营数据 |
| GET | `/api/admin/work-orders` | 文档处理批次清单 |
| GET | `/api/shopfloor/dashboard` | 人工复核工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交文档复核结果 |
| POST | `/api/shopfloor/ai-preview` | 调用可替换 AI Provider 生成字段抽取结果 |
