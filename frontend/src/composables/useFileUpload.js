import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export const useFileUpload = (options = {}) => {
  const {
    maxSize = 5,
    acceptTypes = ['image/*'],
    onSuccess
  } = options

  const uploading = ref(false)
  const previewImage = ref(null)

  const triggerUpload = () => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = acceptTypes.join(',')
    input.onchange = (e) => {
      const file = e.target.files[0]
      if (file) {
        handleFileUpload(file)
      }
    }
    input.click()
  }

  const handleFileUpload = (file) => {
    if (!file) return
    
    const isAcceptedType = acceptTypes.some(type => 
      file.type.match(new RegExp(type.replace('*', '.*')))
    )
    const isLtMaxSize = file.size / 1024 / 1024 < maxSize

    if (!isAcceptedType) {
      ElMessage.error(`只能上传${acceptTypes.join('/')}类型文件!`)
      return
    }
    if (!isLtMaxSize) {
      ElMessage.error(`文件大小不能超过 ${maxSize}MB!`)
      return
    }

    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = (e) => {
      previewImage.value = e.target.result
      onSuccess?.(e.target.result)
    }
  }

  return {
    uploading,
    previewImage,
    triggerUpload,
    handleFileUpload
  }
} 