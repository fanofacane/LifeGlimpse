# Repository Guidelines

## 项目概述
LifeGlimpse 后端基于 Spring Boot 3.5 与 Java 21 构建，负责短视频社交场景的 Feed 推送、热度榜、标签画像推荐、聊天记录与流式 AI 客服，同时通过 Netty WebSocket 在 8082 端口承载实时会话。服务依赖 MySQL、Redis、MinIO 以及阿里 DashScope，并内置定时任务保持热点榜单与推荐分数的新鲜度。

## 环境准备与安装
先安装 JDK 21、Maven（可直接使用仓库自带的 `mvnw`）、MySQL 8、Redis 7 与可访问的 MinIO/对象存储。复制 `src/main/resources/application-example.yml` 为 `application.yml`，按环境填写数据库、Redis、MinIO 与 DashScope 信息。推荐通过环境变量或启动参数覆盖以下键：`SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME`、`SPRING_DATASOURCE_PASSWORD`、`SPRING_DATA_REDIS_HOST`、`SPRING_DATA_REDIS_PORT`、`SPRING_AI_DASHSCOPE_API-KEY`、`MINIO_ENDPOINT`、`MINIO_ACCESSKEY`、`MINIO_SECRETKEY`。首次启动前执行 `sky_platform.sql` 初始化数据库，并确保 `log/` 目录对运行用户可写。

## 运行与构建命令
常用命令如下：
```bash
./mvnw.cmd spring-boot:run      # Windows 本地运行
./mvnw spring-boot:run          # macOS/Linux 本地运行
./mvnw.cmd clean package        # 生成可执行 JAR (target/skyBackend-0.0.1-SNAPSHOT.jar)
./mvnw.cmd test                 # 运行 JUnit 5 测试
java -jar target/skyBackend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
Netty WebSocket 服务随应用启动并监听 8082，如需调整请修改 `SkyBackendApplication` 或配置注入端口。

## 目录结构与接口路由
关键目录：
```
src/main/java/com/sky/skybackend/
├─controller        # /user、/video、/chat 控制层
├─service(*)        # 业务接口与实现
├─mapper            # MyBatis-Plus 映射
├─schedul           # 热榜与 TopK 任务
├─ws                # Netty WebSocket 管道
├─utils/filter      # MinIO、JWT、Redis 工具与过滤器
└─domain            # DTO/PO/VO
```
主要 REST 接口：`/user/login|register|getInfo/{id}|updateInfo`，`/video/upload|public|action|comment|getComments|recommend|match|getVideoByActionType|dynamics|getHotVideos|getHotVideo`，`/chat/userList|chatRecord/{targetId}|clearUnread/{id}|AIService|AIService2`。后端以 JSON 返回 `Result` 包装体；上传接口接受 `multipart/form-data`，推荐接口支持游标分页。

## 技术栈与依赖说明
核心框架包括 Spring Boot 3.5、Spring Web、Spring Data Redis、MyBatis-Plus、Spring Scheduler、Spring AI DashScope Starter 以及 Reactor Flux。底层依赖包含 Netty（WebSocket 通道）、MinIO SDK 负责对象存储、Hutool 简化 Bean 拷贝、JJWT 生成/解析令牌、OkHttp 与 commons-codec 提供网络与散列能力。按需引入 `spring-ai-autoconfigure-model-chat-memory` 保留对话记忆，并通过 `application-example.yml` 中的 `chat.memory.repository.jdbc` 选项启用数据库持久化。部署前确认以上依赖在目标环境可用，并在 CI 中运行 `./mvnw test` 验证热点调度与推荐逻辑。

## 总是用中文回答
