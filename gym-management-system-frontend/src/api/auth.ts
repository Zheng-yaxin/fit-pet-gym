import request from '@/utils/request'

// 管理员登录
export function loginAdmin(data: any) {
    return request({
        url: '/auth/admin/login',
        method: 'post',
        data
    })
}

// 会员登录
export function loginMember(data: any) {
    return request({
        url: '/auth/member/login',
        method: 'post',
        data
    })
}

// 教练登录 (新增)
export function loginCoach(data: any) {
    return request({
        url: '/auth/coach/login',
        method: 'post',
        data
    })
}

// 注册
export function register(data: any) {
    return request({
        url: '/auth/register',
        method: 'post',
        data
    })
}

// 退出登录
export function logout() {
    return request({
        url: '/auth/logout',
        method: 'post'
    })
}
