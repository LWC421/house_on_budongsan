<template>
  <div
    ref="item"
    class="itemContainer"
  >
    <v-expansion-panel
      :class="{ highlight: highLight, noHighlight: !highLight }"
      @group:selected="onOffSwitch"
    >
      <v-expansion-panel-title
        class="itemHead"
        @click="clickEvent"
      >
        <!-- 안 펼쳐져 있을때는 아파트 이름만 보여주자 -->
        <h3>{{ house.apartmentName }}</h3>
        <div style="font-size: 12px; padding-top:5px">{{ house.fullName }}</div>
        <br />
        <div class="eventContainer">
          <chart-modal :houseInfoCode="house.houseInfoCode"></chart-modal>
            <v-icon
              v-if="!house.isLiked"
              icon="mdi-heart"
              size="30"
              class="addHeart"
              @click.stop="addHeart"
              
            />
            <v-icon
              v-else
              icon="mdi-heart-off"
              size="30"
              class="removeHeart"
              color="red"
              @click.stop="removeHeart"
            />
        </div>
      </v-expansion-panel-title>
      <v-expansion-panel-text>
        <!-- 펼쳤을때 거래정보에 대해 보여주자 -->
        <template
          v-for="deal in house.deals"
          :key="deal.houseDealCode"
        >
          <div class="dealContainer">
            <div>
              {{
                `거래 날짜 : ${deal.dealYear}.${deal.dealMonth}.${deal.dealDay}`
              }}
            </div>
            <div>
              {{ `거래 금액 : ${deal.dealAmount.toLocaleString()} 만원` }}
            </div>
            <div>{{ `층 수 : ${deal.floor}층` }}</div>
            <div>{{ `면적 : ${deal.area} m` }}<sup>2</sup></div>
            <div>
              <v-icon
                v-if="!deal.isSaved"
                icon="mdi-content-save-plus"
                class="saveIcon"
                color="blue"
                @click="() => addSave(deal)"
              ></v-icon>
              <v-icon
                v-else
                icon="mdi-content-save-off-outline "
                class="saveIcon"
                color="red"
                @click="() => removeSave(deal)"
              >
              </v-icon>
            </div>
          </div>
        </template>
      </v-expansion-panel-text>
    </v-expansion-panel>
  </div>

  <kakao-marker
    :="{
      lat: props.house.lat,
      lng: props.house.lng,
      map: props.map,
      infoWindow,
      src: mySrc,
      withMarker: props.withMarker
    }"
    @clickEvent="markerClick"
    :key="`${props.house.houseInfoCode}_${props.markerKey}`"
    @setMarker="setMarker"
  ></kakao-marker>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed } from 'vue'
import { KakaoMarker } from '@/components/map'
import { ChartModal } from '@/components/house'
import { useMemberStore } from '@/stores'

const memberStore = useMemberStore()
const { isLogin } = memberStore

const emit = defineEmits([
  'clickEvent',
  'addSave',
  'removeSave',
  'addHeart',
  'removeHeart'
])

const props = defineProps({
  map: Object,
  house: Object,
  markerKey: String,
  withMarker: Boolean,
  src: String
})

//src를 다시 정의해주자
const mySrc = computed(() => {
  //따로 지정된 src가 있을 경우 그것을 보여주고
  if (props.src) {
    return props.src
  }

  //지정된 src가 없으면 rank에 따라 지정해 주자
  const rank = props.house.rank
  if (rank === 1) return 'marker_green.png'
  else if (rank === 2) return 'marker_blue.png'
  else if (rank === 3) return 'marker_red.png'
})

const highLight = ref(false)

const onOff = ref(false)
const item = ref(null)
const marker = ref(null)
const window = ref(null)
const infoWindow = ref(document.createElement('div'))

//패널의 상태가 바뀔때마다 교체해주기
const onOffSwitch = (value) => {
  onOff.value = value.value
}

//마커를 클릭 하면
const markerClick = () => {
  const prev = onOff.value
  onOff.value = false //이벤트가 발생할 수 있도록 해놓자
  clickEvent() //이벤트 발생시키기
  setTimeout(() => {
    onOff.value = prev //다시 되돌려 놓기
  }, 100)
}

//테이블 또는 마커를 클릭할 때의 이벤트
const clickEvent = () => {
  if (!onOff.value) {
    //닫혀 있을때는 따로 먼가를 하지 않는다
    setTimeout(() => {
      emit('clickEvent', {
        house: props.house,
        item: item.value,
        window: window.value,
        marker: marker.value
      })
      highLight.value = true
    }, 250)
    setTimeout(() => {
      highLight.value = false
    }, 1500)
  }
}

//마커가 생성되면 달아주기
const setMarker = (params) => {
  marker.value = params.marker
  window.value = params.window
}

// 인포윈도우 dom생성
const createInfoWindow = () => {
  const container = infoWindow.value
  container.style.width = '250px'
  container.style.textAlign = 'left'
  container.style.padding = '6px'

  const house = props.house
  container.innerHTML = `${house.apartmentName} <br/>` + `${house.fullName}`
}

createInfoWindow()

const addSave = (deal) => {
  emit('addSave', { house: props.house, deal: deal })
}

const removeSave = (deal) => {
  emit('removeSave', { house: props.house, deal: deal })
}

const addHeart = () => {
  if (!isLogin) {
    alert("로그인 후 사용가능합니다")
    return;
  }
  emit('addHeart', props.house.houseInfoCode)
}

const removeHeart = () => {
  if (!isLogin) {
    alert("로그인 후 사용가능합니다")
    return;
  }
  emit('removeHeart', props.house.houseInfoCode)
}
</script>

<style scoped>
.itemContainer {
  width: 100%;
  border-bottom: 2px solid black;
}
.itemHead {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  white-space: nowrap;
  align-items: start;
}
.addHeart:hover {
  color: red;
  transition: all 0.15s ease-in-out;
}
.removeHeart:hover {
  color: gray !important;
  transition: all 0.15s ease-in-out;
}
.itemHead:hover {
  cursor: pointer;
}

.eventContainer {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}
.highlight > button {
  background-color: rgb(128, 128, 128) !important;
  transition: all 0.75s ease-in-out;
}
.noHighlight > button {
  background-color: white !important;
  transition: all 0.25s ease-in-out;
}

.dealContainer:nth-child(1n + 2) {
  border-top: 1px solid black;
}

.saveIcon:hover {
  cursor: pointer;
}
</style>
