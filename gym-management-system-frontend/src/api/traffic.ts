import request from '@/utils/request'

export function getCurrentTraffic() {
  return request({
    url: '/gym/traffic/current',
    method: 'get'
  })
}

export function getTrafficHeatmap() {
  return request({
    url: '/gym/traffic/heatmap',
    method: 'get'
  })
}

export function getTrafficRecommendations(goal = 'general', limit = 5) {
  return request({
    url: '/gym/traffic/recommendations',
    method: 'get',
    params: { goal, limit }
  })
}

export function getGymAreas() {
  return request({
    url: '/admin/gym/traffic/areas',
    method: 'get'
  })
}

export function addGymArea(data: any) {
  return request({
    url: '/admin/gym/traffic/areas',
    method: 'post',
    data
  })
}

export function addTrafficSnapshot(data: any) {
  return request({
    url: '/admin/gym/traffic/snapshot',
    method: 'post',
    data
  })
}
