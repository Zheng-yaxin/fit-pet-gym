import request from '@/utils/request'

export function generateTrainingPlan(data: { goal: string; weeklyFrequency: number }) {
  return request({
    url: '/training/plans/generate',
    method: 'post',
    data
  })
}

export function getCurrentTrainingPlan() {
  return request({
    url: '/training/plans/current',
    method: 'get'
  })
}

export function startTrainingCheckin(planId?: number) {
  return request({
    url: '/training/checkin/start',
    method: 'post',
    params: { planId }
  })
}

export function endTrainingCheckin(checkinId: number) {
  return request({
    url: '/training/checkin/end',
    method: 'post',
    params: { checkinId }
  })
}

export function addTrainingLog(data: any) {
  return request({
    url: '/training/logs',
    method: 'post',
    data
  })
}

export function getMyTrainingLogs() {
  return request({
    url: '/training/logs',
    method: 'get'
  })
}

export function getAdminTrainingLogs() {
  return request({
    url: '/admin/training/logs',
    method: 'get'
  })
}

export function getAdminTrainingReviews() {
  return request({
    url: '/admin/training/reviews',
    method: 'get'
  })
}

export function getAdminActiveTrainingCheckins() {
  return request({
    url: '/admin/training/checkins/active',
    method: 'get'
  })
}
