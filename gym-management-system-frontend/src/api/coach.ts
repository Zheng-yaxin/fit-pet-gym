import request from '@/utils/request'

// ==================== 1. 教练端接口 (原有内容) ====================

// 获取教练信息
export function getCoachInfo() {
    return request({
        url: '/coach/profile/info',
        method: 'get'
    })
}

// 更新教练信息
export function updateCoachInfo(data: any) {
    return request({
        url: '/coach/profile/update',
        method: 'put',
        data
    })
}

// 获取教练待上团课列表
export function getCoachGroupSchedules() {
    return request({
        url: '/coach/my/schedules',
        method: 'get'
    })
}

// 2. [修改] 获取教练历史团课
// 原路径: /course/coach/history -> 新路径: /coach/my/history
export function getCoachGroupHistory(params: any) {
    return request({
        url: '/coach/my/history',
        method: 'get',
        params
    })
}

// 3. [修改] 获取某节团课的学员列表
// 原路径: /course/coach/schedule/... -> 新路径: /coach/my/schedule/...
export function getGroupClassStudents(scheduleId: number) {
    return request({
        url: `/coach/my/schedule/${scheduleId}/students`,
        method: 'get'
    })
}

// 4. [修改] 学员签到 (团课)
// 原路径: /course/coach/checkin/... -> 新路径: /coach/my/checkin/...
// 原方法: put -> 新方法: post (后端 Controller 使用的是 @PostMapping)
export function checkInStudent(enrollmentId: number) {
    return request({
        url: `/coach/my/checkin/${enrollmentId}`,
        method: 'post'
    })
}

// 查询自己的时间段 (教练端)
export function getMySlots(params: { startDate?: string; endDate?: string }) {
    return request({
        url: '/coach/personal-training/slots',
        method: 'get',
        params
    })
}

// 添加可预约时间段
export function addSlot(data: { date: string; startTime: string; endTime: string; cost?: number }) {
    return request({
        url: '/coach/personal-training/slots',
        method: 'post',
        data
    })
}

// 删除时间段
export function deleteSlot(slotId: number) {
    return request({
        url: `/coach/personal-training/slots/${slotId}`,
        method: 'delete'
    })
}

// 查询预约记录 (教练端)
export function getMyBookings() {
    return request({
        url: '/coach/personal-training/bookings',
        method: 'get'
    })
}

// 确认预约 (接单)
export function confirmBooking(bookingId: number) {
    return request({
        url: `/coach/personal-training/bookings/${bookingId}/confirm`,
        method: 'put'
    })
}

// 完成预约 (结课)
export function completeBooking(bookingId: number) {
    return request({
        url: `/coach/personal-training/bookings/${bookingId}/complete`,
        method: 'put'
    })
}

// 取消预约 (教练端)
export function cancelBooking(bookingId: number) {
    return request({
        url: `/coach/personal-training/bookings/${bookingId}/cancel`,
        method: 'put'
    })
}


// ==================== 2. 会员端接口 (新增) ====================

// 获取所有教练列表 (会员端)
export function getPublicCoachList() {
    return request({
        url: '/member/coach/list',
        method: 'get'
    })
}

// 获取某位教练的可预约时间段
export function getCoachAvailableSlots(coachId: number, date: string) {
    return request({
        url: `/member/coach/${coachId}/slots`,
        method: 'get',
        params: { date }
    })
}

// 会员预约私教课 (修正：添加 startTime 和 endTime)
export function bookCoachSlot(data: {
    coachId: number;
    slotId?: number;
    date: string;
    startTime: string;
    endTime: string
}) {
    return request({
        url: '/member/personal-training/book',
        method: 'post',
        data
    })
}

// 查询我的私教预约记录 (会员端)
export function getMyPrivateBookings() {
    return request({
        url: '/member/personal-training/bookings',
        method: 'get'
    })
}

// 取消私教预约 (会员端)
export function cancelPrivateBooking(bookingId: number) {
    return request({
        url: `/member/personal-training/bookings/${bookingId}/cancel`,
        method: 'put'
    })
}
