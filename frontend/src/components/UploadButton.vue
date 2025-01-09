<template>
  <div class="upload-container">
    <input
      type="file"
      ref="fileInput"
      @change="handleFileChange"
      accept="image/*"
      class="hidden"
    />
    <button @click="triggerFileInput" class="upload-btn">
      上传图片
    </button>
    <div v-if="preview" class="preview">
      <img :src="preview" alt="预览图" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'UploadButton',
  data() {
    return {
      preview: null
    }
  },
  methods: {
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        const reader = new FileReader()
        reader.onload = (e) => {
          this.preview = e.target.result
          this.$emit('upload-complete', {
            file: file,
            base64: e.target.result
          })
        }
        reader.readAsDataURL(file)
      }
    }
  }
}
</script>

<style scoped>
.upload-container {
  margin: 20px 0;
}

.hidden {
  display: none;
}

.upload-btn {
  padding: 10px 20px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.preview {
  margin-top: 10px;
  max-width: 300px;
  margin-left: auto;
  margin-right: auto;
}

.preview img {
  width: 100%;
  border-radius: 4px;
}
</style> 