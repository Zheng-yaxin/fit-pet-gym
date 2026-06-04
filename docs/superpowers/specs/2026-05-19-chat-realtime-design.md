# 聊天实时化设计

## 背景

当前聊天链路仍以轮询为主。前端 `gym-management-system-frontend/src/views/chat/index.vue` 和 `gym-management-system-frontend/src/views/course/coach/components/ChatPanel.vue` 每 3 秒刷新一次，后端 `gym-management-system-backend/src/main/java/com/gym/modules/chat/service/impl/ChatServiceImpl.java` 只提供 REST 历史记录与发送接口。

这套做法能跑，但实时性、在线状态、已读回执、失败重发和多实例扩展都不够。

## 目标

- 改成 WebSocket 实时收发
- 保留 REST 做历史记录、联系人、未读数查询
- 消息入库后再推送，保证持久化
- 支持在线状态、正在输入、送达、已读
- 支持断线重连和失败消息重发
- 为后续 Redis 扩展预留多实例方案

## 范围

### 本期要做

- 新增 `ChatWebSocketConfig`，连接路径 `/ws/chat`
- 握手时解析 JWT，注入 `userId`、`userRole`
- 定义统一消息信封
- 服务端推送在线接收方
- 前端新增 `chatSocket.ts` 和 Pinia 聊天状态
- 两个聊天页面移除轮询
- 恢复预约关系校验

### 本期不做

- 群聊
- 文件/图片/语音消息
- AI 自动回复
- 消息搜索

## 架构

### 后端

- `ChatWebSocketConfig` 负责注册 WebSocket 入口
- `ChatHandshakeInterceptor` 负责 JWT 校验与会话身份写入
- `ChatWebSocketHandler` 负责连接、消息分发、关闭、错误处理
- `ChatSessionRegistry` 维护 `userId -> sessions`
- `ChatPushService` 负责落库后推送
- `ChatServiceImpl` 继续处理历史记录、联系人、未读数

### 消息信封

统一使用 JSON。

```json
{
  "type": "CHAT_MESSAGE",
  "requestId": "uuid",
  "senderId": 1,
  "senderRole": "MEMBER",
  "receiverId": 3,
  "receiverRole": "COACH",
  "payload": {},
  "timestamp": 1710000000000
}
```

支持的 `type`：

- `CHAT_MESSAGE`
- `READ_RECEIPT`
- `TYPING`
- `ONLINE_STATUS`
- `ERROR`

### 消息状态

消息流转建议统一成：

- `PENDING`
- `SENT`
- `DELIVERED`
- `READ`
- `FAILED`

说明：

- `PENDING` 为客户端本地待确认
- `SENT` 表示服务端已接收并入库
- `DELIVERED` 表示目标在线且已推送到 socket
- `READ` 表示对方已进入会话或显式回执
- `FAILED` 表示服务端拒绝或网络重试后仍失败

## 关键流程

### 发送消息

1. 客户端先生成临时 `requestId`
2. WebSocket 发送 `CHAT_MESSAGE`
3. 服务端校验身份与预约关系
4. 写入 `gym_chat_message`
5. 返回发送确认给发送方
6. 若接收方在线，立即推送消息
7. 若接收方离线，仅保留未读状态

### 已读回执

1. 客户端打开会话后上报 `READ_RECEIPT`
2. 服务端批量更新对方发来的未读消息
3. 若对方在线，回推已读状态

### 正在输入与在线状态

- `TYPING` 只做短时透传，不落库
- `ONLINE_STATUS` 只做在线/离线广播，不落库
- 这两类事件允许节流，避免抖动

## 数据与校验

- 继续使用现有 `gym_chat_message`
- 恢复预约关系校验，避免任意用户互发
- 会员只能联系自己的教练
- 教练只能联系自己的会员
- 未通过校验时返回 `ERROR`

## 前端改动

- 新增 `src/services/chatSocket.ts`
- 新增 `src/store/modules/chat.ts`
- 两个聊天页面改成订阅式渲染
- 本地维护消息队列、连接状态、重连状态
- 发送失败可重试
- 页面聚焦会话时自动补发未读回执

## 扩展方案

多实例部署时：

- 优先加 Redis Pub/Sub 做跨实例广播
- 如果后续需要更强顺序性与可追溯性，再升级到 Redis Stream

## 验收标准

- 不再依赖 3 秒轮询刷新消息
- 在线用户能即时收到新消息
- 离线用户重新上线后能正确看到未读数
- 已读、正在输入、在线状态能正确展示
- 断线后能自动重连并恢复会话
- `mvn test` 通过，前端 `npm.cmd run build` 通过

