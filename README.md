# 企业会议管理系统

基于 **Android (Jetpack Compose) + Spring Boot + MySQL** 的企业会议管理解决方案。

## 项目结构

```
meeting/
├── android/          # Android 前端 (Jetpack Compose)
│   └── app/
└── backend/          # 后端 API (Spring Boot + Java 17)
    └── src/
```

## 后端技术栈

- **语言**: Java 17
- **框架**: Spring Boot 3.2.5
- **数据库**: MySQL 8.0 (JPA + Hibernate)
- **安全**: Spring Security + JWT (双令牌)
- **构建**: Maven + Maven Wrapper
- **文档**: SpringDoc OpenAPI (Swagger)

## 快速启动

### 前置要求
- JDK 17+
- MySQL 8.0
- Maven (或使用项目自带的 `mvnw`)

### 1. 创建数据库

```sql
CREATE DATABASE meeting CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接

编辑 `backend/src/main/resources/application.yml`，修改数据库用户名和密码。

### 3. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

启动后自动建表并初始化种子数据：
- **角色**: 超级管理员、高管、部门主管、执行人、普通用户
- **部门**: 研发部、产品部、市场部、运营部、运维部、人事部
- **用户**: admin/zhangsan/lisi/wangwu/zhaoliu (默认密码: `123456`)

### 4. 访问 API

- **API 前缀**: `http://localhost:8080/api/v1`
- **健康检查**: `GET /api/v1/health`
- **登录**: `POST /api/v1/auth/login`
- **Swagger 文档**: `http://localhost:8080/swagger-ui.html`

### 5. Docker 部署

```bash
# 在项目根目录
docker-compose up -d
```

## API 概览

### 认证 `/api/v1/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/login` | 登录获取令牌 |
| POST | `/refresh` | 刷新令牌 |

### 用户 `/api/v1/users`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 用户列表 (分页) |
| GET | `/current` | 当前用户信息 |
| GET | `/{id}` | 用户详情 |
| PUT | `/{id}` | 更新用户信息 |

### 备忘录 `/api/v1/memos`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 备忘录列表 (分页) |
| GET | `/{id}` | 备忘录详情 |
| POST | `/` | 创建备忘录 |
| PUT | `/{id}` | 更新备忘录 |
| DELETE | `/{id}` | 删除备忘录 |

### 任务 `/api/v1/tasks`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 任务列表 (分页) |
| GET | `/{id}` | 任务详情 |
| POST | `/{id}/confirm` | 确认任务 |
| POST | `/{id}/progress` | 更新进度 |
| POST | `/{id}/complete` | 完成任务 |
| POST | `/{id}/reject` | 驳回任务 |

### 会议 `/api/v1/meetings`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 会议列表 (分页) |
| GET | `/{id}` | 会议详情 |
| POST | `/` | 创建会议 |
| POST | `/{id}/review` | 审核会议 |
| POST | `/{id}/publish` | 发布会议 |
| POST | `/{id}/archive` | 归档会议 |

### 通知 `/api/v1/notifications`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 通知列表 (分页) |
| GET | `/unread-count` | 未读通知数 |
| PATCH | `/{id}/read` | 标记已读 |
| POST | `/read-all` | 全部标记已读 |

### 文件 `/api/v1/files`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/upload` | 上传文件 |
| GET | `/meeting/{meetingId}` | 会议文件列表 |
| GET | `/task/{taskId}` | 任务文件列表 |

### 辅助接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/roles` | 角色列表 |
| GET | `/api/v1/departments` | 部门列表 |
| GET | `/api/v1/health` | 健康检查 |
