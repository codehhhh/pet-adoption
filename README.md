🐾 宠物领养守护系统
![Java](https://img.shields.io/badge/Java-1.8-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.0-brightgreen)
![Vue](https://img.shields.io/badge/Vue-2.x-success)
![MySQL](https://img.shields.io/badge/MySQL-5.7-blue)

📋 项目简介
基于 Spring Boot + Vue 的宠物领养管理平台，包含用户认证、宠物管理、领养申请、健康档案、预警中心等模块。
🛠️ 技术栈
- **后端**：Java 8 + Spring Boot + MyBatis + JWT + MySQL
- **前端**：Vue 2 + Element UI + Axios + ECharts
- **工具**：Maven + Git + Postman

✨ 核心功能
- 用户认证（JWT权限控制）
- 宠物管理（列表/详情/搜索）
- 领养订单（申请/审核/防重复领养）
- 健康档案（疫苗/体检记录）
- 预警中心（定时问卷+风险评分）

🚀 快速开始
```bash
# 后端
cd JAVA
mvn spring-boot:run

# 前端
cd VUE
npm install
npm run serve
```

## 📁 项目结构
```
pet-adoption/
├── JAVA/          # 后端源码
├── VUE/           # 前端源码
├── SQL/           # 数据库脚本
└── README.md
```