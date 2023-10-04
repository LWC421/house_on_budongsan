<template>
  <div id="map">
    <div 
      id="reload"
      >
      <v-icon 
        icon="mdi-reload"
        size="50"
        @click="clickReload"
        @mouseenter="isHover = true"
        @mouseleave="isHover = false"
        />
        <Transition name="hover">
          <div id="hoverItem" v-if="isHover">
            현재 지도에서 검색
          </div>
        </Transition>
    </div>
    <div id="legend">
      <div class="legendItem">
        <img
          src="/marker_black.png"
          :width="20"
          :height="30"
        />저장
      </div>
      <div class="legendItem">
        <img
          src="/marker_green.png"
          :width="20"
          :height="30"
        />{{ props.ranges[0][0] }} ~ {{ props.ranges[0][1] }}
      </div>
      <div class="legendItem">
        <img
          src="/marker_blue.png"
          :width="20"
          :height="30"
        />{{ props.ranges[1][0] }} ~ {{ props.ranges[1][1] }}
      </div>
      <div class="legendItem">
        <img
          src="/marker_red.png"
          :width="20"
          :height="30"
        />{{ props.ranges[2][0] }} ~ {{ props.ranges[2][1] }}
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  defineProps,
  watch,
  onMounted,
  defineEmits,
  ref,
  onUnmounted
} from 'vue'
import { useMapStore } from '@/stores'
import { storeToRefs } from 'pinia'

const emit = defineEmits(['setMap', "reload"])

const props = defineProps({
  lat: Number,
  lng: Number,
  ranges: Array
})

const mapStore = useMapStore()
const map = ref(null)
const isHover = ref(false);

const { scriptLoaded } = storeToRefs(mapStore)

onMounted(() => {
  watch(() => {
    if (scriptLoaded.value) {
      //스크립트가 로드 되었을 때만
      window.kakao.maps.load(() => {
        const container = document.getElementById('map')
        const center = new window.kakao.maps.LatLng(props.lat, props.lng)
        const options = {
          center: center,
          level: 5
        }

        // emit("clickEvent");
        const initMap = new window.kakao.maps.Map(container, options)
        map.value = initMap //생성된 맵 객체 넣기
        emit('setMap', initMap) //생성한 map객체를 넣어주자
      })
    }
  })
})

onMounted(() => {
  watch(() => {
    if (map.value) {
      map.value.setCenter(new kakao.maps.LatLng(props.lat, props.lng))
    }
  })
})

onUnmounted(() => {
  if (map.value) {
    try {
      map.value.destroy()
    }
    catch (error) {
      return;
    }
  }
})

//새로고침을 누르면
const clickReload = () => {
  emit("reload", map.value);
}

</script>

<style scoped>
#map {
  width: 100%;
  height: 100%;
}
#reload{
  position: absolute;
  z-index: 100;
  top: 10px;
  width: 90px;
  height: 60px;
  left: 50%;
  transform: translateX(-50%);

  display: flex;
  flex-direction: column;
  align-items: center;
}
#reload:hover{
  cursor:pointer;
}
#hoverItem{
  font-size: 5px;
  font-weight: bold;
  background-color: rgba(255, 255, 255, 0.5);
}

.hover-enter-active,
.hover-leave-active{
  transition: all 0.1s ease;
}
.hover-enter-from,
.hover-leave-to{
  transform:translateY(-20px);
  opacity: 0;
}

#legend {
  position: absolute;
  z-index: 100;
  width: 160px;
  height: 170px;
  top: 10px;
  right: 10px;
  background-color: rgba(255, 255, 255, 0.75);

  padding-top: 10px;
  padding-left: 7px;

  display: flex;
  flex-direction: column;
  gap: 10px;
}
.legendItem {
  display: flex;
  gap: 10px;
  align-items: center;
  font-weight: bold;
  font-size: 5px;
}
</style>
