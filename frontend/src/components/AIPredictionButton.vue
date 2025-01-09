<template>
  <button 
    @click="$emit('predict')" 
    :disabled="disabled"
    class="predict-btn"
    :class="{ 'disabled': disabled }"
  >
    预测
  </button>
</template>

<script>
export default {
  name: 'AIPredictionButton',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style scoped>
.predict-btn {
  padding: 10px 20px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 10px 0;
}

.predict-btn.disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style> 