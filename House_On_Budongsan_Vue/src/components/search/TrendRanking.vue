<template>
  <article>
    <h3>{{ title }}</h3>
    <div
      v-for="(rank, index) in props.ranks"
      :key="rank.code"
      class="rankItem"
      @click="() => go(rank.code)"
    >
      <div class="rank">{{ index + 1 }}</div>
      <div class="name">{{ rank.name }}</div>
    </div>
  </article>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  title: String,
  searchBy: String,
  ranks: Array
})

const go = (searchKeyword) => {
  router.push({
    name: 'house',
    query: {
      searchBy: props.searchBy,
      searchKeyword,
      year: 2022,
      month: 4
    }
  })
}
</script>

<style scoped>
article {
  width: 100%;
  max-width: 200px;
  height: 100%;

  display: flex;
  flex-direction: column;
  gap: 10px;
}
.rankItem {
  display: flex;
  align-items: center;
  gap: 5px;
}
.rankItem:hover {
  color: rgb(164, 164, 164);
  transition: all 0.1s ease-out;
}
.rankItem:hover {
  cursor: pointer;
}
.rank {
  width: 16px;
  height: 16px;
  background-color: #e6b17e;

  color: white;

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 10px;
}
.name {
  font-size: 14px;
  overflow-x: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
