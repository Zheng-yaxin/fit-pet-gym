import request from '@/utils/request'

// ================= 类型定义 =================

// 器材实体
export interface Equipment {
    id?: number
    name: string
    code: string
    categoryId?: number
    categoryName?: string
    brand?: string
    model?: string
    imageUrl?: string // 新增图片字段
    type?: string
    location: string
    status: number // 0:正常, 1:维护中, 2:损坏, 3:报废
    statusDesc?: string
    buyDate?: string
    price?: number
    description?: string
    remark?: string
    managerName?: string
    currentManagerId?: number
    createTime?: string
}

// 报修记录实体
export interface RepairLog {
    id?: number
    equipmentId: number
    equipmentName?: string
    reporterId?: number
    repairByName?: string
    faultDesc: string
    status?: number // 0:待处理, 1:维修中, 2:已完成, 3:已取消
    statusDesc?: string
    repairTime?: string
    finishTime?: string
    handleBy?: number
    handleByName?: string
    cost?: number
    remark?: string
    createTime?: string
}

// 分类实体
export interface Category {
    id?: number
    name: string
    parentId?: number
    children?: Category[]
}

// 查询参数
export interface PageQuery {
    pageNum: number
    pageSize: number
    keyword?: string
    status?: number | string
    categoryId?: number | string
    equipmentId?: number | string
    reporterId?: number | string
    [key: string]: any
}

// ================= 器材管理接口 =================

// 分页查询器材列表
export function getEquipmentPage(params: PageQuery) {
    return request({
        url: '/equipment/page',
        method: 'get',
        params
    })
}
// 别名兼容
export const getEquipmentList = getEquipmentPage

// 获取器材详情
export function getEquipmentDetail(id: number) {
    return request({
        url: `/equipment/${id}`,
        method: 'get'
    })
}

// 新增器材
export function addEquipment(data: Equipment) {
    return request({
        url: '/equipment',
        method: 'post',
        data
    })
}

// 修改器材
export function updateEquipment(data: Equipment) {
    return request({
        url: `/equipment/${data.id}`,
        method: 'put',
        data
    })
}

// 删除器材
export function deleteEquipment(id: number) {
    return request({
        url: `/equipment/${id}`,
        method: 'delete'
    })
}

// 修改器材状态
export function changeEquipmentStatus(id: number, status: number) {
    return request({
        url: `/equipment/${id}/status`,
        method: 'patch',
        params: { status }
    })
}
// 别名兼容
export const updateEquipmentStatus = changeEquipmentStatus


// ================= 分类管理接口 =================

// 获取分类树
export function getCategoryTree() {
    return request({
        url: '/equipment/category/tree',
        method: 'get'
    })
}
// 别名兼容
export const getCategoryList = getCategoryTree


// ================= 报修管理接口 =================

// 分页查询报修记录
export function getRepairPage(params: PageQuery) {
    return request({
        url: '/equipment/repair/page',
        method: 'get',
        params
    })
}
export function getMyRepairPage(params: PageQuery) {
    return request({
        url: '/equipment/repair/my', // 对应后端 Controller 的 /my 接口
        method: 'get',
        params
    })
}
// 提交报修 (修改为 /create 避免冲突)
export function addRepair(data: RepairLog) {
    return request({
        url: '/equipment/repair/create',
        method: 'post',
        data
    })
}

// 处理报修 (开始维修/完成/取消)
export function handleRepair(data: any) {
    return request({
        url: '/equipment/repair/handle',
        method: 'post',
        data
    })
}

// 获取报修详情
export function getRepairDetail(id: number) {
    return request({
        url: `/equipment/repair/${id}`,
        method: 'get'
    })
}