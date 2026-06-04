import request from '@/utils/request'

export function getPendingFeedback() {
  return request({
    url: '/course/feedback/pending',
    method: 'get'
  })
}

export function submitCourseFeedback(data: any) {
  return request({
    url: '/course/feedback',
    method: 'post',
    data
  })
}

export function getMyCourseFeedback() {
  return request({
    url: '/course/feedback/mine',
    method: 'get'
  })
}

export function getAdminFeedbackList() {
  return request({
    url: '/admin/feedback/list',
    method: 'get'
  })
}

export function getAdminFeedbackStats() {
  return request({
    url: '/admin/feedback/stats',
    method: 'get'
  })
}

export function handleAdminFeedback(id: number, data: any) {
  return request({
    url: `/admin/feedback/${id}/handle`,
    method: 'put',
    data
  })
}
