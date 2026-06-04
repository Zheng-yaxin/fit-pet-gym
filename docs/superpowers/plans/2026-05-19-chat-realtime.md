# Chat Realtime Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current polling-based chat flow with JWT-authenticated WebSocket realtime messaging while preserving REST history, contacts, unread counts, and persistence.

**Architecture:** The backend will authenticate the socket handshake with the existing JWT token, register live sessions by `userId/userRole`, persist every outgoing chat message first, and then push it to online recipients. The frontend will centralize socket lifecycle, connection state, pending messages, and retry logic in a Pinia module and a socket service so both chat entry points render the same realtime behavior. REST endpoints stay in place for history, contacts, and unread counts.

**Tech Stack:** Spring Boot 3.2, Spring Security, spring-websocket, MyBatis-Plus, Redis-ready session registry, Vue 3, Pinia, TypeScript, Element Plus, axios.

---

### Task 1: Add backend WebSocket foundation

**Files:**
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/websocket/ChatWebSocketConfig.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/websocket/ChatHandshakeInterceptor.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/websocket/ChatWebSocketHandler.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/websocket/ChatSessionRegistry.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/dto/ChatSocketEnvelope.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/dto/ChatSocketPayload.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/enums/ChatSocketMessageType.java`
- Modify: `gym-management-system-backend/pom.xml`

- [ ] **Step 1: Add the failing test scaffolding**

Create unit tests under `gym-management-system-backend/src/test/java/com/gym/modules/chat/websocket/` that assert:
- the handshake interceptor rejects missing or invalid JWT
- the handshake interceptor exposes `userId` and `userRole` for a valid token
- the session registry can register, find, and remove sessions by user key

- [ ] **Step 2: Run the focused backend tests and confirm they fail**

Run: `cd gym-management-system-backend && mvn test -Dtest=ChatHandshakeInterceptorTest,ChatSessionRegistryTest`
Expected: fail because the websocket classes do not exist yet.

- [ ] **Step 3: Implement the websocket foundation**

Add `spring-boot-starter-websocket` to `pom.xml`, wire `/ws/chat`, and keep the handler focused on:
- establishing authenticated sessions
- tracking sessions by `userId:userRole`
- parsing and emitting the unified JSON envelope
- routing connect, disconnect, typing, online status, and error events

- [ ] **Step 4: Re-run the focused backend tests**

Run: `cd gym-management-system-backend && mvn test -Dtest=ChatHandshakeInterceptorTest,ChatSessionRegistryTest`
Expected: pass.

### Task 2: Route persisted chat messages through realtime push

**Files:**
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/service/impl/ChatServiceImpl.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/controller/ChatController.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/service/IChatService.java`
- Create: `gym-management-system-backend/src/main/java/com/gym/modules/chat/service/impl/ChatPushService.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/dto/ChatMessageSendDto.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/vo/ChatMessageVO.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/vo/ChatContactVo.java`
- Modify: `gym-management-system-backend/src/main/java/com/gym/modules/chat/domain/entity/ChatMessage.java`

- [ ] **Step 1: Add failing service tests**

Create tests under `gym-management-system-backend/src/test/java/com/gym/modules/chat/service/` that assert:
- `sendMessage` still saves the message
- `sendMessage` rejects users without a valid booking relationship
- `sendMessage` attempts a push after persistence
- unread count and history behavior remain unchanged

- [ ] **Step 2: Run the targeted service test and confirm failure**

Run: `cd gym-management-system-backend && mvn test -Dtest=ChatServiceImplTest`
Expected: fail because the push service and validation path are not implemented yet.

- [ ] **Step 3: Implement persistence-first push flow**

Restore booking validation, keep REST history endpoints, and make `sendMessage`:
- validate sender/receiver roles against the existing booking table
- insert the chat row first
- push the saved message to any online recipient session
- return a clean error envelope when validation fails

- [ ] **Step 4: Re-run the service test**

Run: `cd gym-management-system-backend && mvn test -Dtest=ChatServiceImplTest`
Expected: pass.

### Task 3: Build the frontend socket client and chat store

**Files:**
- Create: `gym-management-system-frontend/src/services/chatSocket.ts`
- Create: `gym-management-system-frontend/src/store/modules/chat.ts`
- Modify: `gym-management-system-frontend/src/store/modules/user.ts`
- Modify: `gym-management-system-frontend/src/api/chat.ts`

- [ ] **Step 1: Add failing TypeScript coverage**

Create a small test harness or compile-targeted module assertions that check:
- the socket service can open, close, reconnect, and emit chat events
- the store exposes connection state, pending queue, unread counts, and retry helpers
- socket auth uses the existing stored token

- [ ] **Step 2: Run the frontend type/build check and confirm it fails**

Run: `cd gym-management-system-frontend && npm.cmd run build`
Expected: fail until the new socket/store modules and imports exist.

- [ ] **Step 3: Implement socket lifecycle and shared state**

Add a socket service that:
- connects to `/ws/chat`
- sends the JWT token during handshake or query auth
- tracks `connected`, `reconnecting`, `lastError`, `pendingMessages`, and `unreadCounts`
- retries with backoff after disconnect
- emits typed events for message, read receipt, typing, and online status

- [ ] **Step 4: Re-run build**

Run: `cd gym-management-system-frontend && npm.cmd run build`
Expected: pass.

### Task 4: Replace polling in both chat pages

**Files:**
- Modify: `gym-management-system-frontend/src/views/chat/index.vue`
- Modify: `gym-management-system-frontend/src/views/course/coach/components/ChatPanel.vue`

- [ ] **Step 1: Add page-level interaction checks**

Use browser/manual checks for:
- opening a conversation
- receiving a new message without waiting 3 seconds
- seeing typing and online states
- retrying a failed send
- marking messages read when the conversation is focused

- [ ] **Step 2: Remove polling and wire socket subscriptions**

Replace `setInterval` with store subscriptions so both pages:
- load history once when a contact is selected
- subscribe to incoming socket events
- show pending/sent/delivered/read states
- keep contact unread badges in sync

- [ ] **Step 3: Re-run build and browser QA**

Run: `cd gym-management-system-frontend && npm.cmd run build`
Expected: pass.

### Task 5: Final verification

**Files:**
- No code changes expected unless verification reveals a regression

- [ ] **Step 1: Run the backend test suite**

Run: `cd gym-management-system-backend && mvn test`
Expected: pass.

- [ ] **Step 2: Run the frontend production build**

Run: `cd gym-management-system-frontend && npm.cmd run build`
Expected: pass.

- [ ] **Step 3: Validate in browser**

Open the local app and verify:
- messages arrive instantly
- online and typing indicators render
- failed messages can be retried
- unread counts update when switching conversations

