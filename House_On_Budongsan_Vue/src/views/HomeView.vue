<template>
  <search-container :click="search"></search-container>
  <div class="table__container">
    <news-list-container
      :style="{
        width: '350px',
        marginRight: '80px'
      }"
      linkName="news"
    >
    </news-list-container>
    <board-list-container
      :style="{
        width: '350px'
      }"
      linkName="board"
    >
    </board-list-container>
  </div>

  <div id="rankContainer">
    <trend-ranking
      :="{
        title: '시도 랭킹',
        searchBy: 'location',
        ranks: sidoRanks
      }"
    />
    <trend-ranking
      :="{
        title: '구군 랭킹',
        searchBy: 'location',
        ranks: gugunRanks
      }"
    />
    <trend-ranking
      :="{
        title: '동 랭킹',
        searchBy: 'location',
        ranks: dongRanks
      }"
    />
    <trend-ranking
      :="{
        title: '건물 랭킹',
        searchBy: 'building',
        ranks: buildingRanks
      }"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { SearchContainer } from '@/components/search'
import { BoardListContainer, NewsListContainer } from '@/components/board'
import { getRank } from '@/api/house.js'
import { TrendRanking } from '../components/search'

const router = useRouter()

const search = ({ searchBy, searchKeyword }) => {
  if (!searchKeyword) {
    alert('검색어를 입력해주세요')
  } else {
    router.push({
      name: 'house',
      query: {
        searchBy,
        searchKeyword,
        year: 2022,
        month: 4
      }
    })
  }
}

const sidoRanks = ref([])
const gugunRanks = ref([])
const dongRanks = ref([])
const buildingRanks = ref([])

//각각의 랭킹 정보들을 불러온다
;(async () => {
  const { data } = await getRank('sido')
  sidoRanks.value = data
})()
;(async () => {
  const { data } = await getRank('gugun')
  gugunRanks.value = data
})()
;(async () => {
  const { data } = await getRank('dong')
  dongRanks.value = data
})()
;(async () => {
  const { data } = await getRank('building')
  buildingRanks.value = data
})()
</script>

<style scoped>
.table__container {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 70px;
}
#rankContainer {
  position: relative;
  left: 50%;
  transform: translateX(-50%);

  width: 800px;
  height: 200px;

  display: flex;
  flex-direction: row;
}
</style>
