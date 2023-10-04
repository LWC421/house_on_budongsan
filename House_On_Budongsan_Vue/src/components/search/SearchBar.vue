<template>
  <div>
    <v-tabs
      v-model="searchBy"
      bg-color="#E6B17E"
      color="gray"
      class="select"
      :style="{
        width: '180px',
        display: 'flex',
        alignItems: 'center',
        opacity: 0.9
      }"
    >
      <v-tab value="location">지역명</v-tab>
      <v-tab value="building">건물명</v-tab>
    </v-tabs>
    <div class="bottom">
      <v-autocomplete
        variant="outlined"
        density="compact"
        clearable
        no-data-text="검색어와 일치하는 항목이 없습니다"
        v-model="selected"
        @update:menu="ableAPICall"
        @update:modelValue="selectCompleted"
        @update:search="searching"
        :items="searchItems"
        item-title="title"
        item-value="value"
      ></v-autocomplete>
      <v-btn-primary
        height="100%"
        @click="clickEvent"
        >검색</v-btn-primary
      >
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref, watch, defineEmits } from 'vue'
import { getLocationList, getBuildingList } from '@/api/house.js'
import { debounce } from 'lodash'

const emit = defineEmits(['onChange'])
const { click } = defineProps({
  click: Function
})

const searchItems = ref([])
const needAPICall = ref(true)
const searchBy = ref('area') //검색 기준
const keyword = ref(0) //검색 키워드(code)
const selected = ref()

//autocomplete의 메뉴 중 하나를 골랐으면
const selectCompleted = (value) => {
  keyword.value = value //선택된 value로 변경
  needAPICall.value = false //API콜이 필요없다고 표시해놓기
}

//디바운스를 적용하여 API콜을 제한하자
const APICall = debounce(async (value) => {
  if (needAPICall.value) {
    let promise = null
    if (searchBy.value === 'location') {
      promise = () => getLocationList(value)
    } else if (searchBy.value === 'building') {
      promise = () => getBuildingList(value)
    }
    try {
      const { data } = await promise()
      searchItems.value = data
    } catch (error) {}
  }
}, 500)

//사용자의 입력에 따라 발생한다
const searching = async (value) => {
  if (needAPICall.value) {
    //만약 API콜을 해야하면
    if (value) {
      //공백이 아니면
      APICall(value)
    }
  }
}

//사용자가 무언가를 선택 했으면
// 다시 API콜을 할 수 있도록 돌려놓자
const ableAPICall = () => {
  needAPICall.value = true //다시 API콜이 필요하다고 표시
  emit('onChange', {
    //상위 컴포넌트에서 받을 수 있도록 emit해주기
    searchBy: searchBy.value,
    searchKeyword: keyword.value
  })
}

//검색 종류를 바꾸면 이에 따라 비워주자
watch(() => {
  if (searchBy.value) {
    searchItems.value = []
    keyword.value = null
    selected.value = null
  }
})

//검색을 누르면 그에 해당하는 이벤트를 발생 시켜주자
const clickEvent = () => {
  click({
    searchBy: searchBy.value,
    searchKeyword: keyword.value
  })
}
</script>

<style scoped>
.select {
  width: 200px;
  margin-bottom: 5px;
}
div.bottom {
  display: flex;
  flex-direction: row;
  height: 40px;
  background-color: white;
  border-radius: 5px;
}
</style>
