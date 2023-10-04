<template>
  <div
    class="list-container"
    :style="props.style"
  >
    <div class="list-header-container">
      <div class="header">뉴스</div>
      <more-view-button :linkName="props.linkName"></more-view-button>
    </div>
    <div>
      <div
        v-for="(item, index) in items"
        :key="index"
        class="list-item"
        v-html="item.title"
        @click="goLink(item.originalLink)"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import MoreViewButton from '@/components/board/MoreViewButton.vue'
import { newsRecentFive } from '../../api/news'

// props 설정
const props = defineProps({
  linkName: String,
  style: Object
})

const items = ref([])

;(async () => {
  const { data } = await newsRecentFive()
  items.value = data.items
})()

function goLink(link) {
  window.open(link, '_blank')
}
</script>

<style scoped>
.header {
  font-weight: bold;
  font-size: 20px;
}
.list-container {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.list-header-container {
  display: flex;
  border-bottom: 1px solid rgb(225, 225, 225);
  padding-bottom: 11px;
  align-items: center;
  justify-content: space-between;
}

.list-item {
  font-size: 15px;
  padding: 10px 0px 10px 0px;
  white-space: nowrap;
  overflow-x: hidden;
  text-overflow: ellipsis;
}

.list-item:hover {
  cursor: pointer;
  background: rgb(246, 246, 246);
}
</style>
