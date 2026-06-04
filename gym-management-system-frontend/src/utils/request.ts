import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'

interface ApiResponse<T = any> {
  code: number
  msg?: string
  data: T
}

interface RequestInstance extends AxiosInstance {
  <T = any>(config: AxiosRequestConfig): Promise<T>
}

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
}) as RequestInstance

service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, msg, data } = response.data
    if (code === 200) {
      return data as any
    }
    ElMessage.error(msg || 'System error')
    return Promise.reject(new Error(msg || 'Error'))
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      ElMessage.error('Login expired. Please sign in again.')
      const userStore = useUserStore()
      userStore.logout()
    } else {
      ElMessage.error(error.message || 'Network request failed')
    }
    return Promise.reject(error)
  }
)

export default service
