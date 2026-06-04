import request from '@/utils/request'

export interface Exercise {
  id?: number
  name: string
  targetMuscle?: string
  equipment?: string
  difficulty?: string
  imageUrl?: string
  videoUrl?: string
  steps?: string
  commonMistakes?: string
  tips?: string
  status?: string
}

export function getExerciseList(params: any) {
  return request({
    url: '/exercise/list',
    method: 'get',
    params
  })
}

export function getExerciseDetail(id: number) {
  return request({
    url: `/exercise/${id}`,
    method: 'get'
  })
}

export function getExerciseAlternatives(id: number) {
  return request({
    url: `/exercise/${id}/alternatives`,
    method: 'get'
  })
}

export function addExercise(data: Exercise) {
  return request({
    url: '/admin/exercise',
    method: 'post',
    data
  })
}

export function updateExercise(id: number, data: Exercise) {
  return request({
    url: `/admin/exercise/${id}`,
    method: 'put',
    data
  })
}

export function deleteExercise(id: number) {
  return request({
    url: `/admin/exercise/${id}`,
    method: 'delete'
  })
}
