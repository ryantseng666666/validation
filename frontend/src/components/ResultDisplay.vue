<template>
  <div class="result-container">
    <div v-if="loading" class="loading">
      处理中...
    </div>
    <div v-else-if="results" class="result-display">
      <h3>预测结果</h3>
      <p>下载速度: {{ results.downloadSpeed }} Mbps</p>
      <p>上传速度: {{ results.uploadSpeed }} Mbps</p>
      <p>Ping: {{ results.ping }} ms</p>
      <p>置信度: {{ (results.confidence * 100).toFixed(2) }}%</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ResultDisplay',
  props: {
    results: Object,
    loading: Boolean
  }
}
</script>

<style scoped>
.result-container {
  margin: 20px 0;
}

.loading {
  color: #666;
  font-style: italic;
}

.result-display {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-width: 300px;
  margin: 0 auto;
}

.result-display h3 {
  margin-top: 0;
  color: #42b983;
}
</style> 