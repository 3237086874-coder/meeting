# 企业会议任务派发与执行跟踪 App — 完整架构设计方案

> 基于 PRD 文档 (PRD.md) 设计
> 技术栈: Spring Boot (Kotlin) + PostgreSQL + Android (Jetpack Compose) + 阿里云 AI

---

## 1. 总体系统架构

```
+-------------------------------------------------------------------+
|                        ANDROID CLIENT                              |
|  +---------------------+  +------------------+  +---------------+  |
|  |   Presentation      |  |    Domain        |  |    Data       |  |
|  |   (Jetpack Compose) |  |    (Use Cases)   |  |  (Repository) |  |
|  +---------+-----------+  +--------+---------+  +-------+-------+  |
|            |                        |                     |         |
|  +---------+------------------------+---------------------+-------+ |
|  |                 NETWORK LAYER (Retrofit + OkHttp)               | |
|  +----------------------------------------------------------------+ |
|  |                 LOCAL STORAGE (Room DB + DataStore)             | |
|  +----------------------------------------------------------------+ |
|  |                 SYNC ENGINE (WorkManager)                       | |
|  +----------------------------------------------------------------+ |
+-------------------------------------------------------------------+
          |  HTTPS/TLS  |                    |  HTTPS/TLS
          v             v                    v
+-------------------------------------------------------------------+
|                    ALIYUN CDN / SLB (Layer 7)                      |
+-------------------------------------------------------------------+
          |
          v
+-------------------------------------------------------------------+
|                   API GATEWAY (Spring Cloud Gateway)               |
|  +-------------+  +--------------+  +---------------------------+  |
|  | Rate Limit  |  | Auth (JWT)   |  | Request/Response Logging  |  |
|  +-------------+  +--------------+  +---------------------------+  |
+-------------------------------------------------------------------+
          |
          v
+-------------------------------------------------------------------+
|                    MICROSERVICE LAYER (Spring Boot / Kotlin)       |
|  +------------------+  +-----------------+  +------------------+  |
|  | Meeting Service  |  | Task Service    |  | User Service     |  |
|  +------------------+  +-----------------+  +------------------+  |
|  +------------------+  +-----------------+  +------------------+  |
|  | AI Orchestrator  |  | Notification    |  | Memo Service     |  |
|  | Service          |  | Service         |  |                  |  |
|  +------------------+  +-----------------+  +------------------+  |
|  +------------------+  +-----------------+  +------------------+  |
|  | File Service     |  | Audit Service   |  |                  |  |
|  +------------------+  +-----------------+  +------------------+  |
+-------------------------------------------------------------------+
          |                    |                    |
          v                    v                    v
+----------------+  +-------------------+  +-------------------+
|   PostgreSQL   |  |  Aliyun OSS      |  |  Aliyun RocketMQ  |
|   (Primary DB) |  |  (File Storage)  |  |  (Message Queue)  |
+----------------+  +-------------------+  +-------------------+
                                               |
                                               v
                            +----------------------------+
                            |  Aliyun AI Services        |
                            |  - Speech-to-Text (ASR)    |
                            |  - NLP (Summarization)     |
                            |  - Custom Task Extraction  |
                            +----------------------------+
```

---

## 2. 技术栈决策

### 后端

| 组件 | 选择 | 理由 |
|------|------|------|
| 框架 | Spring Boot + Kotlin | 声明式事务管理对状态机至关重要；Spring Cloud Alibaba 原生支持阿里云；Kotlin 比 Java 少 40% 样板代码 |
| 数据库 | PostgreSQL | JSONB 存储 AI 元数据；CTE 递归查询部门层级；pgcrypto 列级加密；GIN 索引加速 JSON 查询 |
| 文件存储 | 阿里云 OSS | 分片上传（200MB）；服务端 AES-256 加密；CDN 流式音频播放；Spring Cloud Alibaba 原生集成 |
| 消息队列 | 阿里云 RocketMQ | 事件驱动 AI 管道；精确一次投递；Spring Cloud Stream 集成 |
| 推送通知 | 阿里云移动推送 | 统一华为/小米/OPPO/VIVO 厂商通道；避免分别维护 5 个 SDK |
| 部署 | Docker + Kubernetes | 容器化部署，弹性伸缩 |

### Android

| 组件 | 选择 | 理由 |
|------|------|------|
| 架构模式 | MVVM + MVI 混合 | 简单页面用 MVVM（登录、表单）；复杂状态页面用 MVI（会议详情、审核） |
| UI | Jetpack Compose | 声明式 UI 匹配复杂状态机；比 XML 少 50% 代码；动态 UI 如录音进度、状态徽章 |
| DI | Hilt | 编译时安全；50+ 依赖项的大项目必需；与 Navigation/WorkManager 原生集成 |
| 网络 | Retrofit + OkHttp | 拦截器链：自动刷新 token、日志、SSL Pinning、重试；Ktor 生态不够成熟 |
| 本地 DB | Room | 编译时 SQL 校验；Kotlin Flow/Coroutines 原生支持 |
| 状态管理 | StateFlow | 生命周期感知；支持 stateIn() 用于 MVI |
| 音频录制 | MediaRecorder | 会议录音足够；比 MediaCodec 开销低 |
| 音频播放 | ExoPlayer (Media3) | 支持 OSS 流式加载；进度通知栏；后台播放 |
| 离线同步 | WorkManager | App 被杀也能保证执行；支持网络/存储约束；链式多步同步 |
| 图片加载 | Coil | Compose 原生；轻量；支持 OSS URL |

---

## 3. 后端项目结构

```
meeting-app-backend/
├── build.gradle.kts
├── settings.gradle.kts
├── docker-compose.yml
├── kubernetes/
└── src/main/kotlin/com/enterprise/meeting/
    ├── MeetingApplication.kt
    ├── common/          # 通用配置、异常、工具
    │   ├── config/      # WebConfig, SecurityConfig, AliyunConfig, RocketMQConfig, AsyncConfig
    │   ├── exception/   # GlobalExceptionHandler, BusinessException, StateTransitionException, ErrorCode
    │   ├── util/        # EncryptionUtil, FileUtil, DateTimeUtil
    │   └── dto/         # ApiResponse 统一响应体
    ├── auth/            # JWT 认证
    │   ├── JwtTokenProvider.kt
    │   ├── JwtAuthenticationFilter.kt
    │   └── annotation/  # @CurrentUser, @RequireRole
    ├── user/            # 用户与角色管理
    │   ├── domain/      # User, Role, UserRole 实体
    │   ├── api/         # UserController, AuthController
    │   ├── service/     # UserService, AuthService
    │   └── repository/
    ├── meeting/         # 会议管理
    │   ├── domain/      # Meeting, MeetingState(enum), MeetingStateMachine
    │   ├── api/         # MeetingController
    │   ├── service/     # MeetingService, MeetingStateMachineService, RecordingService
    │   └── repository/
    ├── task/            # 任务管理
    │   ├── domain/      # Task, TaskState(enum), TaskAttachment, TaskStateMachine
    │   ├── api/         # TaskController
    │   ├── service/     # TaskService, TaskStateMachineService
    │   └── repository/
    ├── ai/              # AI 集成
    │   ├── orchestrator/ # AIOrchestratorService, TranscriptionStep, SummarizationStep, TaskExtractionStep
    │   ├── client/      # AliyunASRClient, AliyunNLPClient
    │   └── dto/         # TranscriptionResult, SummaryResult, ExtractedTask
    ├── file/            # 文件管理
    │   ├── domain/      # FileRecord 实体
    │   ├── api/         # FileController
    │   ├── service/     # FileService, OSSService
    │   └── repository/
    ├── notification/    # 推送通知
    │   ├── api/         # NotificationController
    │   └── service/     # PushNotificationService, AliyunPushService
    ├── memo/            # 个人备忘
    │   ├── domain/      # Memo 实体
    │   ├── api/         # MemoController
    │   ├── service/     # MemoService
    │   └── repository/
    └── audit/           # 审计日志
        ├── domain/      # AuditLog, AuditActionType
        └── service/     # AuditService
```

---

## 4. Android 项目结构

```
meeting-app-android/
├── build.gradle.kts (root)
├── settings.gradle.kts
├── gradle/libs.versions.toml
└── app/src/main/java/com/enterprise/meeting/
    ├── MeetingApplication.kt
    ├── MainActivity.kt
    ├── di/              # 依赖注入模块
    │   ├── AppModule.kt, NetworkModule.kt, DatabaseModule.kt
    │   ├── RepositoryModule.kt, UseCaseModule.kt
    ├── core/            # 基础设施层
    │   ├── network/     # ApiClient, AuthInterceptor, TokenRefreshAuthenticator, ApiResponse, ErrorMapper
    │   ├── local/       # AppDatabase, Converters, DataStoreModule
    │   ├── sync/        # SyncWorker, SyncManager, SyncConflictResolver
    │   ├── security/    # EncryptedPrefs, BiometricAuth, CryptoUtil
    │   ├── notification/ # PushNotificationService, NotificationChannelManager, InAppNotificationManager
    │   └── util/        # FileUtil, AudioUtil, NetworkMonitor, DateTimeFormatter
    ├── data/            # 数据层
    │   ├── remote/api/  # AuthApi, MeetingApi, TaskApi, FileApi, NotificationApi, MemoApi
    │   ├── remote/dto/  # MeetingDto, TaskDto, UserDto...
    │   ├── remote/mapper/ # DtoMapper
    │   ├── local/dao/   # MeetingDao, TaskDao, UserDao, MemoDao, PendingActionDao
    │   ├── local/entity/ # MeetingEntity, TaskEntity... PendingActionEntity
    │   ├── local/mapper/ # EntityMapper
    │   ├── repository/  # AuthRepositoryImpl, MeetingRepositoryImpl...
    │   └── datastore/   # UserPreferences, SyncMetadata
    ├── domain/          # 领域层
    │   ├── model/       # User, Meeting, MeetingState, Task, TaskState, Memo...
    │   ├── repository/  # 接口定义: AuthRepository, MeetingRepository...
    │   └── usecase/     # 按模块分组：meeting/, task/, recording/, auth/, memo/
    └── presentation/    # 表现层
        ├── navigation/  # NavGraph, Screen(sealed), NavArgs
        ├── theme/       # Theme, Color, Type, Shape
        ├── components/  # MeetingCard, TaskCard, StateBadge, AudioPlayerBar,
        │                # RecordingButton, UploadProgressIndicator, EmptyStateView,
        │                # ErrorStateView, LoadingStateView, ConfirmDialog, PullToRefresh
        ├── meeting/     # list/, detail/, create/, review/ (各含 Screen + ViewModel + UiState)
        ├── task/        # list/, detail/, attachment/
        ├── auth/        # LoginScreen + ViewModel
        ├── memo/        # MemoListScreen, CreateMemoScreen
        ├── profile/     # ProfileScreen
        └── notification/ # NotificationListScreen
```

---

## 5. 核心数据流

### 5.1 会议录音流程

```
用户 → App 内录音 (MediaRecorder)
  → 创建会议 (POST /meetings)
    → 分片上传 OSS (最多 200MB, 5MB/片, 3次重试)
      → 链接录音到会议 (PATCH meeting)
        → 状态转为 AI_PROCESSING
          → 发布事件到 RocketMQ
```

### 5.2 AI 处理流程

```
RocketMQ 事件 → AI Orchestrator
  Step 1: 语音转文本 (阿里云 ASR, 轮询进度)
  Step 2: 生成摘要 (阿里云 NLP)
  Step 3: 任务抽取 (自定义 Prompt + 解析)
    → 状态转为 PENDING_REVIEW
      → 推送通知给审核人
```

### 5.3 任务生命周期

```
AI 抽取任务 (DRAFT, 对执行人不可见)
  → 审核人审核/修改/驳回
    → 发布 (任务对执行人可见)
      → 执行人确认 → 执行中 → 进度更新 → 附件上传 → 完成
```

---

## 6. 状态机设计

### 6.1 会议状态机

```
PENDING → AI_PROCESSING → PENDING_REVIEW → REJECTED
                                            → PENDING_PUBLISH → PUBLISHED → ARCHIVED
```

10 条合法转换，每个转换有前置条件校验和后置副作用（发送通知、启动管道等）

### 6.2 任务状态机

```
PENDING_CONFIRM → EXECUTING → COMPLETED
                → REJECTED → PENDING_CONFIRM (重新分配)
```

---

## 7. 数据库核心表

| 表名 | 说明 | 关键索引 |
|------|------|----------|
| users | 用户账号 | phone, department |
| roles | 角色定义 | code (唯一) |
| user_roles | 用户-角色关联 | (user_id, role_id) |
| meetings | 会议主表 | state, created_by, assigned_reviewer |
| meeting_recordings | 会议录音文件 | meeting_id |
| meeting_transcripts | 转写文本 | meeting_id (唯一) |
| meeting_summaries | 会议摘要 | meeting_id (唯一) |
| tasks | 任务主表 | assigned_to+state, meeting_id, due_date |
| task_attachments | 任务附件 | task_id |
| memos | 个人备忘 | user_id+is_completed, reminder_at |
| audit_logs | 审计日志 | entity_type+entity_id, actor_id, created_at |
| notifications | 通知记录 | user_id+is_read |

---

## 8. API 端点概览

| 模块 | 端点数 | 说明 |
|------|--------|------|
| 认证 | 4 | login, refresh, logout, me |
| 用户管理 | 7 | CRUD + 角色分配 + 任务查询 + 通知 |
| 会议 | 9 | CRUD + 审核 + 发布 + 归档 |
| 录音 | 6 | 分片上传(3) + 列表 + 播放URL + 删除 |
| AI | 4 | 重试 + 状态 + 转写 + 摘要 |
| 任务 | 6 | 列表 + 详情 + 确认 + 进度 + 完成 + 拒绝 |
| 附件 | 4 | 上传 + 列表 + 下载 + 删除 |
| 通知 | 5 | 列表 + 已读 + 全部已读 + 未读数 + Token注册 |
| 备忘 | 6 | CRUD + 提醒设置 |
| 审计 | 1 | 日志查询 |

统一响应格式: `{code, message, data, timestamp, requestId, pagination?}`

---

## 9. 错误处理策略

### 后端

分层异常体系：
- `BusinessException(ErrorCode)` — 业务逻辑异常（状态机冲突、权限不足）
- `ValidationException` — 参数校验失败（422）
- `SystemException` — 系统异常（DB、网络、AI 超时）

全局 `@ControllerAdvice` 统一捕获，返回标准化错误响应。

幂等性：关键操作（创建会议、确认任务）通过 `Idempotency-Key` 请求头防重复。

### Android

密封类三层映射：
```
Throwable → AppException(sealed) → UI Error State(sealed)
```

显示策略：
| 场景 | 显示方式 |
|------|----------|
| 网络错误(临时) | Snackbar + 重试按钮 |
| 网络错误(持续) | 全屏 ErrorStateView |
| 服务器 4xx | 对话框/内联提示 |
| 服务器 5xx | Snackbar + 自动日志 |
| 离线 | 顶部 Banner + 缓存显示 |
| 上传失败 | 进度条变错误态 + 恢复按钮 |

---

## 10. MVP 分 6 阶段实施（17 周）

| 阶段 | 时间 | 后端 | Android | 可交付物 |
|------|------|------|---------|----------|
| **P1: 基础** | 第 1-3 周 | 项目脚手架 + JWT 认证 + DB + Docker | 脚手架 + 登录 + 导航壳 | 后端部署 + App 可登录 |
| **P2: 会议流程** | 第 4-6 周 | 会议 CRUD + OSS 分片上传 + 列表/详情 API | 会议列表 + 创建 + 录音/播放 | 完整录音上传播放闭环 |
| **P3: AI 集成** | 第 7-9 周 | 阿里 ASR/NLP 集成 + AI 编排 + 转写/摘要 API | 状态卡片 + 转写/摘要展示 + 推送接收 | AI 全流程：录音→转写→摘要→任务 |
| **P4: 审核与任务** | 第 10-12 周 | 审核/发布 API + 任务生命周期 + 附件 + 推送 | 审核页面 + 任务列表/详情 + 附件上传 | 完整审核→发布→执行闭环 |
| **P5: 完善** | 第 13-15 周 | 备忘 API + 审计查询 + 归档定时任务 | 备忘 + 离线同步 + 错误状态 + 个人设置 | 完整功能 App |
| **P6: 加固** | 第 16-17 周 | 安全审计 + 压力测试 + 性能优化 | 厂商推送适配 + Compose 性能 + 混淆 | 生产就绪 |

---

## 11. 安全设计

| 层级 | 措施 |
|------|------|
| 传输层 | TLS 1.3 + Android CertificatePinner |
| 认证 | JWT (RS256, 私钥存阿里云 KMS) + Refresh Token |
| 数据库 | pgcrypto 列级加密 (PII 字段) |
| 文件存储 | OSS 服务端 AES-256 (KMS 自动轮转) |
| Android 本地 | Room + SQLCipher (密钥由生物识别 + KeyStore 派生) |
| 审计 | 所有变更操作记录 WHO/WHAT/WHEN/WHERE/WHY |

---

## 12. 性能目标

| 场景 | 目标 |
|------|------|
| 会议列表加载 (20 条) | < 500ms P95 |
| 会议详情加载 | < 800ms P95 |
| AI 转写 (30 分钟音频) | < 10 分钟 |
| 推送通知送达 | < 5s P95 |
| 文件上传 (200MB, 4G) | < 2 分钟 |
| 并发用户 | 200 持续 |
| 日活用户 | 2000 |
