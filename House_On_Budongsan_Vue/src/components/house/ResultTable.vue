<template>
  <div class="tableContainer">
    <h2>검색 결과</h2>
    <v-expansion-panels multiple>
      <TransitionGroup name="trans" tag="div" class="itemContainer">
        <div 
          v-if="props.houses.length !== 0"
          v-for="house in props.houses" 
          :key="house.houseInfoCode">
          <table-item 
            :key="house.houseInfoCode" 
            :="{map:props.map, 
              house, 
              markerKey: 'result', 
              withMarker:house.withMarker
              }" 
            @clickEvent="clickEvent"
            @addSave="addSave"
            @removeSave="removeSave"
            @addHeart="addHeart"
            @removeHeart="removeHeart"
            >
          </table-item>
        </div>
        <div v-else  class="noItem">
          <v-icon icon="mdi-database-off" size="100"/>
          <div>
            결과가 없습니다
          </div>
        </div>
      </TransitionGroup>
    </v-expansion-panels>
  </div>
</template>


<script setup>
import { defineProps, defineEmits, computed } from "vue";
import TableItem from "./TableItem.vue";

const emit = defineEmits(["clickEvent", "addSave", "removeSave"])

const props = defineProps({
  map: Object,
  houses: Array,
})


//테이블 아이템들의 click event들을 위로 다시 올려줌
const clickEvent = (params) => {
  emit("clickEvent", params);
}

//addSave에 대해 위로 다시 올려줌
const addSave = (params) => {
  emit("addSave", params);
}

//removeSave에 대해 위로 다시 올려줌
const removeSave = (params) => {
  emit("removeSave", params);
}

//addHeart에 대해 위로 다시 올려줌
const addHeart = (params) => {
  emit("addHeart", params);
}

//removeSave에 대해 위로 다시 올려줌
const removeHeart = (params) => {
  emit("removeHeart", params);
}


</script>


<style scoped>
h2{
  text-align: center;
  padding-bottom: 10px;
}

.tableContainer{
  width: 300px;
  overflow-y: auto;
  overflow-x:hidden;
}
.tableContainer::-webkit-scrollbar {
  width: 10px;
}
.tableContainer::-webkit-scrollbar-thumb {
  background-color: rgba(128, 128, 128, 0.5);
  border-radius: 4px;
  background-clip: padding-box;
}
.tableContainer::-webkit-scrollbar-track {
  border-radius: 10px;
  box-shadow: inset 0px 0px 5px white;
}
/* 움직이는 엘리먼트에 트랜지션 적용 */
.trans-move, 
.trans-enter-active,
.trans-leave-active {
  transition: all 0.5s ease;
}
.trans-enter-from{
  opacity: 0;
  transform: translateX(-50px);
}
.trans-leave-to {
  opacity: 0;
  transform: translateX(50px);
}
.trans-leave-active {
  position: absolute;
}
.itemContainer{
  width: 100%;
}

.noItem{
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
}

</style>