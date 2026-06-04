import request from '@/utils/request'

// --- 类型定义 (对应 Java Entity/DTO/VO) ---

export interface HealthData {
    id?: number
    userId?: number
    gender?: number
    birthDate?: string // yyyy-MM-dd
    height?: number
    weight?: number
    bmi?: number
    bodyFatRate?: number
    measureTime?: string // yyyy-MM-dd HH:mm:ss
}

export interface Food {
    id?: number
    name: string
    emoji?: string
    calories: number
    protein: number
    fat: number
    carbohydrate: number
}

export interface DietRecordDTO {
    foodId: number
    amount: number
    mealType: number // 1:早餐, 2:午餐, 3:晚餐, 4:加餐
    eatDate: string // yyyy-MM-dd
}

export interface DietDetailVO {
    id: number
    foodName: string
    amount: number
    calories: number
    mealType: number
}

export interface DietSummaryVO {
    date: string
    totalCalories: number
    totalProtein: number
    totalFat: number
    totalCarb: number
    recommendCalories: number
    recommendProtein: number
    recommendFat: number
    recommendCarb: number
    suggestions: string[]
    details: DietDetailVO[]
}

export interface DietActionItem {
    kind?: string
    priority?: string
    title?: string
    detail?: string
}

export interface DietActionPlanVO {
    date?: string
    status?: string
    headline?: string
    nextMealFocus?: string
    caloriesGap?: number
    proteinGap?: number
    fatGap?: number
    carbohydrateGap?: number
    actions?: DietActionItem[]
    notes?: string[]
}

export interface BodyInsightVO {
    status?: string
    headline?: string
    bmiStatus?: string
    trendLabel?: string
    latestWeight?: number
    latestBmi?: number
    latestBodyFatRate?: number
    weightDelta?: number
    bodyFatDelta?: number
    latestMeasureTime?: string
    explanations?: string[]
    actions?: string[]
}

export interface BodyImage {
    id: number
    imageUrl: string
    recordTime: string
    createTime?: string
}

// --- 接口方法 (对应 HealthController) ---

// 1. 健康数据
export function saveHealthData(data: HealthData) {
    return request({
        url: '/health/data',
        method: 'post',
        data
    })
}

export function getLatestHealthData() {
    return request<HealthData>({
        url: '/health/data/latest',
        method: 'get'
    })
}

export function getHealthDataHistory() {
    return request<HealthData[]>({
        url: '/health/data/history',
        method: 'get'
    })
}

export function getBodyInsight() {
    return request<BodyInsightVO>({
        url: '/health/data/insight',
        method: 'get'
    })
}

// 2. 身材照片
export function uploadBodyImageFile(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request<string>({ // 返回 URL
        url: '/health/image/upload',
        method: 'post',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
    })
}

export function saveBodyImageRecord(data: { imageUrl: string; recordTime: string }) {
    return request({
        url: '/health/image/record',
        method: 'post',
        data
    })
}

export function getBodyImageHistory() {
    return request<BodyImage[]>({
        url: '/health/image/history',
        method: 'get'
    })
}

export function deleteBodyImage(id: number) {
    return request({
        url: `/health/image/${id}`,
        method: 'delete'
    })
}

// 3. 饮食管理
export function getFoodList(keyword: string) {
    return request<Food[]>({
        url: '/health/food/list',
        method: 'get',
        params: { keyword }
    })
}

export function addCustomFood(data: Food) {
    return request({
        url: '/health/food',
        method: 'post',
        data
    })
}

export function recordDiet(data: DietRecordDTO) {
    return request({
        url: '/health/diet',
        method: 'post',
        data
    })
}

export function deleteDietLog(id: number) {
    return request({
        url: `/health/diet/${id}`,
        method: 'delete'
    })
}

export function getDietSummary(date: string) {
    return request<DietSummaryVO>({
        url: '/health/diet/summary',
        method: 'get',
        params: { date }
    })
}

export function getDietActionPlan(date: string) {
    return request<DietActionPlanVO>({
        url: '/health/diet/action-plan',
        method: 'get',
        params: { date }
    })
}
// AI 分析结果类型
export interface FoodAnalysisVO {
    name: string
    estimatedWeight: number
    caloriesPer100g: number
    proteinPer100g: number
    fatPer100g: number
    carbPer100g: number
    analysis: string
}

// AI 识别接口
export function analyzeFoodImage(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request<FoodAnalysisVO>({
        url: '/health/diet/analyze',
        method: 'post',
        headers: { 'Content-Type': 'multipart/form-data' },
        data: formData
    })
}

export function getDietTarget() {
    return request({
        url: '/health/diet/target',
        method: 'get'
    })
}

export function saveDietTarget(data: any) {
    return request({
        url: '/health/diet/target',
        method: 'put',
        data
    })
}

export function getDietGap(date: string) {
    return request({
        url: '/health/diet/gap',
        method: 'get',
        params: { date }
    })
}
