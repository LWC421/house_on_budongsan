<template>
  <router-link :to="{ name: linkName }">
    <div id="more-button">+ 더보기</div>
  </router-link>
</template>


<script setup>
const props = defineProps({
  linkName: String,
})
</script>


<style scoped>
#more-button{
  font-size: 13px;
}

#more-button:hover{
  color:#2F73C0;
}

</style>