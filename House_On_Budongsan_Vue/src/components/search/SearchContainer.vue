<template>
  <div class="background">
    <div class="searchContainer">
      <search-bar :click="click"></search-bar>
    </div>
  </div>
</template>


<script setup>
import { defineProps } from "vue";
import SearchBar from "./searchBar.vue";

const { click } = defineProps({
  click: Function,
})

</script>


<style scoped>
.background {
  background-image: url("@/assets/img/search_background.jpg");
  background-size: 100% 300px;
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-left: 200px;
  padding-right: 200px;
}

</style>