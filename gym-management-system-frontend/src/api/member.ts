import request from '@/utils/request'

export interface Member {
    id?: number
    username: string
    nickname?: string
    name?: string
    phone: string
    gender: number // 1:男, 0:女
    status: string // '0':正常, '1':停用
    password?: string
    balance?: number // 余额
    createTime?: string
}

export interface MemberCard {
    id?: number
    memberId: number
    memberName?: string
    cardNo: string
    cardType: string // '年卡' | '月卡' | '次卡'
    issueDate: string
    expireDate: string
    remainingTimes?: number
    status: string // '0':正常, '1':挂失, '2':过期
    createTime?: string
}

export interface BenefitAsset {
    equipmentId?: number
    name?: string
    categoryName?: string
    location?: string
    status?: number
    statusDesc?: string
    reason?: string
}

export interface BenefitArea {
    areaId?: number
    areaName?: string
    location?: string
    currentCount?: number
    capacity?: number
    occupancyPercent?: number
    statusLabel?: string
    action?: string
}

export interface MemberBenefitSummary {
    memberId?: number
    memberName?: string
    walletBalance?: number
    active?: boolean
    statusLabel?: string
    card?: MemberCard | null
    daysLeft?: number
    groupCourseQuota?: number
    privateTrainingQuota?: number
    unlimitedEntry?: boolean
    lockerAccess?: boolean
    entitlements?: string[]
    actions?: string[]
    availableAssets?: BenefitAsset[]
    recommendedAreas?: BenefitArea[]
}

export interface CardBuyDto {
    memberId: number
    cardType: string
    duration?: number
    times?: number
    amount: number
    remark?: string
}

// ================= 会员核心接口 =================

// 新增：获取当前登录会员的个人信息（无需传ID，依靠Token）
export function getMemberProfile() {
    return request({
        url: '/member/profile', // 对应后端新增的接口
        method: 'get'
    })
}

// 获取会员列表 (管理员用)
export function getMemberList(params: any) {
    return request({
        url: '/member/list',
        method: 'get',
        params
    })
}

// 获取单个会员 (管理员用，需传ID)
export function getMember(id: number) {
    return request({
        url: `/member/${id}`,
        method: 'get'
    })
}

// 新增会员
export function addMember(data: Member) {
    return request({
        url: '/member',
        method: 'post',
        data
    })
}

// 修改会员
export function updateMember(data: Member) {
    return request({
        url: '/member',
        method: 'put',
        data
    })
}

// 删除会员
export function deleteMember(id: number) {
    return request({
        url: `/member/${id}`,
        method: 'delete'
    })
}

// ================= 钱包/充值接口 =================

// 会员充值 (修复：URL路径 和 参数传递方式)
export function rechargeMember(data: { memberId: number, amount: number, remark?: string }) {
    return request({
        url: '/member/wallet/recharge', // 修正路径
        method: 'post',
        // 后端使用 @RequestParam，前端需使用 params 发送 Query String
        // 如果后端改为 @RequestBody，则这里应改回 data
        params: data
    })
}

// ================= 会员卡管理接口 =================

// 获取会员卡列表 (管理员用)
export function getMemberCardList(params: any) {
    return request({
        url: '/member/card/list',
        method: 'get',
        params
    })
}

// 获取会员当前有效的卡片
export function getValidCard(memberId: number) {
    return request({
        url: `/member/card/valid/${memberId}`,
        method: 'get'
    })
}

export function getMemberBenefitSummary(memberId: number) {
    return request({
        url: `/member/card/benefits/${memberId}`,
        method: 'get'
    })
}

// 办理/购买会员卡
export function buyMemberCard(data: CardBuyDto) {
    return request({
        url: '/member/card/buy',
        method: 'post',
        data
    })
}

// 会员卡续费
export function renewMemberCard(data: CardBuyDto) {
    return request({
        url: '/member/card/renew',
        method: 'post',
        data
    })
}

// 会员卡挂失
export function reportLossCard(cardId: number) {
    return request({
        url: `/member/card/loss/${cardId}`,
        method: 'post'
    })
}
