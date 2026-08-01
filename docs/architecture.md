# Idp 架构

版权所有 © 2026 上海如静知华信息科技有限公司。

浏览器通过 Vue 管理端或文档复核端访问 Spring Boot REST API。安全层完成 JWT 与角色鉴权，业务层负责文档批次、模型、字段、评测和复核记录，JPA/Flyway 管理 MySQL 数据。

管理端角色为 `DOMAIN_OPERATOR`、`QUALITY`、`ADMIN`；执行端角色为 `DOMAIN_USER`。正式部署建议将OCR 与模型服务置于独立采集服务，并隔离文档处理网络和办公网络。
