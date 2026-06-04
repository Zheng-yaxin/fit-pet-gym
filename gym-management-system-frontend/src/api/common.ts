import request from '@/utils/request'

// 通用文件上传
export function uploadFile(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/common/upload',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}