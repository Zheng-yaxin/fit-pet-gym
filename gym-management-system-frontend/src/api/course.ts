// src/api/course.ts
import request from '@/utils/request'

// ==================== 类型定义 ====================

export interface Course {
    id?: number
    name: string
    description?: string
    duration?: number
    price?: number
    imageUrl?: string
    difficulty?: string
    calories?: number
    maxParticipants?: number
    status?: string // '0':正常, '1':停用
    createTime?: string
}

export interface Coach {
    id?: number
    name: string
    phone: string
    specialty?: string
    description?: string
    avatar?: string
    status?: string
}

// 修改：完善排课接口定义
export interface ClassSchedule {
    id?: number
    courseId: number
    coachId: number
    dayOfWeek: number // 1-7 (周一到周日)
    startTime: string // "09:00"
    endTime: string   // "10:00"
    startDate: string // "2023-01-01"
    endDate: string   // "2023-06-30"
    capacity: number
    enrolledCount?: number
    createTime?: string
    // 关联查询字段
    courseName?: string
    coachName?: string
}

export interface Enrollment {
    id?: number
    memberId?: number
    memberName?: string
    memberPhone?: string
    scheduleId: number
    courseName?: string
    coachName?: string
    classTime?: string
    duration?: number
    price?: number
    status: string // '0':已报名, '1':已取消, '2':已完成
    createTime?: string
}

export interface PageQuery {
    pageNum: number
    pageSize: number
    keyword?: string
    status?: string
    startDate?: string
    endDate?: string
    dayOfWeek?: number
    courseId?: number
    coachId?: number
}

// ==================== 管理员端接口 (/admin/course) ====================

// 1. 课程管理
export function getAdminCourseList(params: PageQuery) {
    return request({
        url: '/admin/course/list',
        method: 'get',
        params
    })
}

export function getAllAdminCourses() {
    return request({
        url: '/admin/course/all',
        method: 'get'
    })
}

export function getAdminCourse(id: number) {
    return request({
        url: `/admin/course/${id}`,
        method: 'get'
    })
}

export function addAdminCourse(data: Course) {
    return request({
        url: '/admin/course',
        method: 'post',
        data
    })
}

export function updateAdminCourse(data: Course) {
    return request({
        url: '/admin/course',
        method: 'put',
        data
    })
}

export function deleteAdminCourse(id: number) {
    return request({
        url: `/admin/course/${id}`,
        method: 'delete'
    })
}

// 2. 教练管理
export function getAdminCoachList(params: PageQuery) {
    return request({
        url: '/admin/course/coach/list',
        method: 'get',
        params
    })
}

export function getAllAdminCoaches() {
    return request({
        url: '/admin/course/coach/all',
        method: 'get'
    })
}

export function addAdminCoach(data: Coach) {
    return request({
        url: '/admin/course/coach',
        method: 'post',
        data
    })
}

export function updateAdminCoach(data: Coach) {
    return request({
        url: '/admin/course/coach',
        method: 'put',
        data
    })
}

export function deleteAdminCoach(id: number) {
    return request({
        url: `/admin/course/coach/${id}`,
        method: 'delete'
    })
}

// 3. 课表管理
export function getAdminScheduleList(params: any) {
    return request({
        url: '/admin/course/schedule/list',
        method: 'get',
        params
    })
}

export function getAdminScheduleDetail(id: number) {
    return request({
        url: `/admin/course/schedule/${id}`,
        method: 'get'
    })
}

export function addAdminSchedule(data: ClassSchedule) {
    return request({
        url: '/admin/course/schedule',
        method: 'post',
        data
    })
}

export function updateAdminSchedule(data: ClassSchedule) {
    return request({
        url: '/admin/course/schedule',
        method: 'put',
        data
    })
}

export function deleteAdminSchedule(id: number) {
    return request({
        url: `/admin/course/schedule/${id}`,
        method: 'delete'
    })
}

// 4. 预约管理
export function getAdminEnrollmentList(params: any) {
    return request({
        url: '/admin/course/enrollment/list',
        method: 'get',
        params
    })
}

export function updateAdminEnrollmentStatus(id: number, status: string) {
    return request({
        url: `/admin/course/enrollment/${id}/status`,
        method: 'put',
        params: { status }
    })
}

export function getAdminEnrollmentStats() {
    return request({
        url: '/admin/course/enrollment/stats',
        method: 'get'
    })
}

export function deleteAdminEnrollment(id: number) {
    return request({
        url: `/admin/course/enrollment/${id}`,
        method: 'delete'
    })
}


// ==================== 教练端接口 (/coach/my) ====================

export function getCurrentCoachInfo() {
    return request({
        url: '/coach/my/info',
        method: 'get'
    })
}

export function getCoachSchedules() {
    return request({
        url: '/coach/my/schedules',
        method: 'get'
    })
}

export function getCoachHistorySchedules(params: PageQuery) {
    return request({
        url: '/coach/my/history',
        method: 'get',
        params
    })
}

export function getCoachScheduleStudents(scheduleId: number) {
    return request({
        url: `/coach/my/schedule/${scheduleId}/students`,
        method: 'get'
    })
}

export function coachCheckinStudent(enrollmentId: number) {
    return request({
        url: `/coach/my/checkin/${enrollmentId}`,
        method: 'post'
    })
}


// ==================== 会员端接口 (/member/course) ====================

export function getMemberCourseList() {
    return request({
        url: '/member/course/list',
        method: 'get'
    })
}

export function getMemberCourseDetail(id: number) {
    return request({
        url: `/member/course/${id}`,
        method: 'get'
    })
}

export function getMemberCoachList() {
    return request({
        url: '/member/course/coaches',
        method: 'get'
    })
}

export function getMemberAvailableSchedules(params: any) {
    return request({
        url: '/member/course/schedules',
        method: 'get',
        params
    })
}

export function getMemberScheduleDetail(id: number) {
    return request({
        url: `/member/course/schedule/${id}`,
        method: 'get'
    })
}

export function memberEnrollCourse(scheduleId: number) {
    return request({
        url: '/member/course/enroll',
        method: 'post',
        data: { scheduleId }
    })
}

export function memberCancelEnrollment(enrollmentId: number) {
    return request({
        url: `/member/course/cancel/${enrollmentId}`,
        method: 'post'
    })
}

export function getMemberEnrollments(params: PageQuery) {
    return request({
        url: '/member/course/my-enrollments',
        method: 'get',
        params
    })
}

export function checkMemberEnrolled(scheduleId: number) {
    return request({
        url: `/member/course/check-enrolled/${scheduleId}`,
        method: 'get'
    })
}

// ==================== 课程模板管理接口 ====================

export interface CourseTemplate {
    id?: number
    courseId: number
    coachId: number
    weekDays: string // 例如: "1,3,5" 表示周一、三、五
    startTime: string // 例如: "18:00:00"
    capacity: number
    status?: string // '0':启用, '1':停用
}

export function getCourseTemplateList(params: any) {
    return request({
        url: '/admin/course/template/list',
        method: 'get',
        params
    })
}

export function getCourseTemplateDetail(id: number) {
    return request({
        url: `/admin/course/template/${id}`,
        method: 'get'
    })
}

export function addCourseTemplate(data: CourseTemplate) {
    return request({
        url: '/admin/course/template',
        method: 'post',
        data
    })
}

export function updateCourseTemplate(data: CourseTemplate) {
    return request({
        url: '/admin/course/template',
        method: 'put',
        data
    })
}

export function deleteCourseTemplate(id: number) {
    return request({
        url: `/admin/course/template/${id}`,
        method: 'delete'
    })
}

export function updateCourseTemplateStatus(id: number, status: string) {
    return request({
        url: `/admin/course/template/${id}/status`,
        method: 'put',
        params: { status }
    })
}

export function generateSchedulesFromTemplates(weeks: number) {
    return request({
        url: '/admin/course/template/generate',
        method: 'post',
        params: { weeks }
    })
}