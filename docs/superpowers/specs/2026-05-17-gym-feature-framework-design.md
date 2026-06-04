# 健身功能框架第一批改造设计

## 目标

本批改造先修复项目里会影响后续扩展的接口与结构问题，再为以下功能建立可访问、可继续迭代的框架：

- 智能训练计划
- 健身房人流热力图
- 动作教学库
- 训练打卡与训练日志
- 课后反馈
- 饮食缺口提醒

## 第一批范围

第一批不做复杂算法和完整业务闭环，重点是把页面、路由、接口、后端模块、数据库增量脚本搭好。

## 现有问题修复

- 补充 Dashboard 后端统计接口和前端 API。
- 补充 `/auth/logout`，让前端退出登录有后端接口可调用。
- 补充 `/member/card/list`，匹配前端会员卡列表调用。
- 补充 `/equipment/repair/{id}`，匹配前端报修详情调用。
- 处理空控制器文件，避免后续误判模块入口。
- 保持 `/coach/my/info` 只由一个有效控制器提供。

## 新增后端模块

- `exercise`：动作教学库与动作替代推荐。
- `training`：智能训练计划、训练打卡、训练日志。
- `feedback`：课程课后反馈。
- `traffic`：健身房区域、人流快照与热力统计。

## 新增前端入口

会员端：

- `/training/plan`
- `/gym/traffic`
- `/exercise/library`
- `/training/log`
- `/course/feedback`
- `/health` 内展示饮食缺口提醒

管理端：

- `/admin/exercise/manage`
- `/admin/training/logs`
- `/admin/feedback`
- `/admin/gym/traffic`

## 数据库增量

新增 `sql/2026-05-17-feature-framework.sql`，包含动作、训练计划、训练日志、课后反馈、人流、饮食目标相关表。

## 验证

- 后端至少运行 Maven 测试或编译。
- 前端至少运行 TypeScript/Vite 构建。
- 若环境缺依赖或数据库，记录未验证项。
