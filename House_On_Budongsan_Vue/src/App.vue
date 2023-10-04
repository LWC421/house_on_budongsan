<template>
  <v-app>
    <the-header></the-header>
    <v-main>
      <router-view></router-view>
    </v-main>
    <the-footer></the-footer>
  </v-app>
</template>

<script setup>
import { TheFooter, TheHeader } from '@/components/common'
import { useMapStore } from '@/stores'
import { storeToRefs } from 'pinia'

const mapStore = useMapStore()

const { scriptLoaded } = storeToRefs(mapStore)

/* global kakao */
const script = document.createElement('script')
script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${
  import.meta.env.VITE_KAKAO_MAP_API_KEY
}&autoload=false`
script.onload = () => {
  scriptLoaded.value = true
}
document.head.appendChild(script)
</script>

<style>
a {
  text-decoration: none;
  color: inherit;
}
</style>
