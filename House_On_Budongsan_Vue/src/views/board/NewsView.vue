<template>
  <board-navigator></board-navigator>
  <div id="board-body">
    <div id="header-name">뉴스</div>
    <table>
      <thead>
        <tr>
          <th>No</th>
          <th>제목</th>
          <th>기사게시시간</th>
        </tr>
      </thead>
      <tbody>
        <template
          v-for="(article, index) in articles"
          :key="index"
        >
          <tr
            class="list-item"
            @click="goLink(article.originalLink)"
          >
            <td>{{ 10 * (page - 1) + (index + 1) }}</td>
            <td v-html="article.title"></td>
            <td>{{ article.publishDate }}</td>
          </tr>
        </template>
      </tbody>
    </table>
    <div
      class="text-center"
      style="margin-top: 30px"
    >
      <v-pagination
        v-model="page"
        :length="pageCount"
        :total-visible="5"
        rounded="circle"
      ></v-pagination>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { getPageArticle, getCount } from '@/api/news'
import BoardNavigator from '@/components/board/BoardNavigator.vue'

const page = ref(1)
const totalArticle = ref(1)
const itemsPerPage = ref(10)
const articles = ref([])

// 즉시 실행함수 page를 기본값으로 잡고 articles를 받아온다.
;(async () => {
  const { data } = await getPageArticle(page.value)
  articles.value = data.items
})()

// 즉시 실행함수로 전체 count불러옴
;(async () => {
  const { data } = await getCount()
  totalArticle.value = data.items
})()

const pageCount = computed(() => {
  return Math.ceil(totalArticle.value / itemsPerPage.value)
})

// page의 값이 변함에 따라 데이터베이스에서 게시글 목록을 불러온다.
watch(page, async (page) => {
  const { data } = await getPageArticle(page)
  articles.value = data.items
})

// 게시글을 누르면 상세보기 페이지로 이동한다.
function goLink(link) {
  window.open(link, '_blank')
}
</script>

<style scoped>
#board-body {
  height: 80vh;
  padding-left: 30px;
  padding-right: 30px;
  padding-top: 40px;
}

#header-name {
  font-weight: bold;
  font-size: 25px;
  margin-bottom: 30px;
}
table {
  width: 100%;
  border-top: 2px solid #444444;
  border-collapse: collapse;
  text-align: center;
}
th,
td {
  border-bottom: 1px solid #3e3e3e;
  padding: 10px;
}
.write-container {
  display: flex;
  flex-direction: row-reverse;
}
.list-item:hover {
  cursor: pointer;
  background: rgb(246, 246, 246);
}
</style>
