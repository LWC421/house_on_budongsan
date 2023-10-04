<template>
  <router-link
    :to="{ name: 'home' }"
    class="logo"
  >
    <v-img
      width="70"
      :src="`/logo.png`"
    ></v-img>
  </router-link>
</template>

<script setup></script>

<style scoped>
.logo {
  font-size: 30px;
  color: black;
  font-weight: bold;

  transition: all 0.05s ease-in-out;
}

.logo:hover {
  color: gray;
  transition: all 0.1s ease-in-out;
}
</style>
