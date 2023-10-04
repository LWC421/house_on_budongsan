<template>
  <v-tabs
    fixed-tabs
    bg-color="white"
  >
    <v-tab
      elevation="2"
      to="/news"
    >
      뉴스
    </v-tab>
    <v-tab
      to="/board"
    >
      자유게시판
    </v-tab>
  </v-tabs>
  <the-board></the-board>
</template>


<script setup>

</script>


<style scoped></style>