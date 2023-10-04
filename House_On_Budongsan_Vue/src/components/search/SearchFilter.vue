<template>
  <v-dialog
    v-model="onOff"
    width="600px"
  >
    <template v-slot:activator="{ props }">
      <div>
        <v-btn-secondary v-bind="props">필터링</v-btn-secondary>
      </div>
    </template>

    <v-card>
      <v-card-text>
        <v-slider
          v-model="myYear"
          :max="2022"
          :min="2015"
          :step="1"
          label="Y e a r"
          @update:modelValue="onChange"
        >
          <template v-slot:append>
            <v-text-field
              readonly
              v-model="myYear"
              style="width: 70px"
              density="compact"
              hide-details
              variant="outlined"
            >
            </v-text-field>
          </template>
        </v-slider>

        <v-slider
          v-model="myMonth"
          :max="12"
          :min="1"
          :step="1"
          label="Month"
          @update:modelValue="onChange"
        >
          <template v-slot:append>
            <v-text-field
              readonly
              v-model="myMonth"
              style="width: 70px"
              density="compact"
              hide-details
              variant="outlined"
            >
            </v-text-field>
          </template>
        </v-slider>

        <div class="filterItem">
          가격
          <v-range-slider
            v-model="amountRange"
            :max="1200000"
            :min="400"
            :step="100"
            hide-details
          >
            <template v-slot:prepend>
              <v-text-field
                :model-value="amountRange[0]"
                hide-details
                single-line
                type="number"
                variant="outlined"
                density="compact"
                style="width: 120px"
                @update:modelValue="(value) => amountUpdate(0, value)"
              >
              </v-text-field>
            </template>
            <template v-slot:append>
              <v-text-field
                :model-value="amountRange[1]"
                hide-details
                single-line
                type="number"
                variant="outlined"
                density="compact"
                style="width: 120px"
                @update:modelValue="(value) => amountUpdate(1, value)"
              >
              </v-text-field>
            </template>
          </v-range-slider>
        </div>

        <div class="filterItem">
          면적
          <v-range-slider
          v-model="areaRange"
          :max="425"
          :min="5"
          :step="5"
          hide-details
          >
          <template v-slot:prepend>
            <v-text-field
            :model-value="areaRange[0]"
            hide-details
            single-line
              type="number"
              variant="outlined"
              density="compact"
              style="width: 120px"
              @update:modelValue="(value) => areaUpdate(0, value)"
              >
            </v-text-field>
          </template>
          <template v-slot:append>
            <v-text-field
            :model-value="areaRange[1]"
            hide-details
            single-line
            type="number"
            variant="outlined"
            density="compact"
            style="width: 120px"
            @update:modelValue="(value) => areaUpdate(1, value)"
            >
            </v-text-field>
          </template>
        </v-range-slider>
      </div>
    </v-card-text>
    
    
      <v-card-actions>
        <v-btn-secondary
        block
        @click="onOff = false"
        >Close</v-btn-secondary
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue'

const emit = defineEmits(['onChange'])

const onOff = ref(false)
const props = defineProps({
  year: Number,
  month: Number,
})

//실제로 조작될 데이터를 설정해주자
// 초기값은 이전에 저장된 값들로부터 설정한다
const myYear = ref(props.year)
const myMonth = ref(props.month)

//filter창에서 업데이트 될 값들을 정의
const amountRange = ref([0, 1200000])
const areaRange = ref([5, 425])

//가격필터가 변경 될때에 대해
const amountUpdate = (index, value) => {
  amountRange.value[index] = value
  onChange()
}

//면적필터가 변경 될때에 대해
const areaUpdate = (index, value) => {
  areaRange.value[index] = value
  onChange()
}


//각각의 필터 값들이 바뀔때 적용
const onChange = () => {
  emit('onChange', {
    year: myYear.value,
    month: myMonth.value,
    lowerAmount: amountRange.value[0],
    upperAmount: amountRange.value[1],
    lowerArea: areaRange.value[0],
    upperArea: areaRange.value[1]
  })
}
watch(() => {
  if (amountRange.value) {
    onChange()
  }
})
</script>

<style scoped>
#dialog{
  z-index: 999999;
}
.filterItem{
  display: flex;
  align-items: center;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>
