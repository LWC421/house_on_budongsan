<template>
  <v-dialog
    v-model="onOff"
    width="1200px"
    height="800px"
    :class="{ resized: resized }"
  >
    <template v-slot:activator="{ props }">
      <div>
        <v-btn
          icon="mdi-chart-bar"
          v-bind="props"
          density="compact"
          variant="tonal"
          color="#C56E33"
        ></v-btn>
      </div>
    </template>

    <template v-if="!house">
      <v-card :loading="!house">
        <v-skeleton-loader></v-skeleton-loader>
      </v-card>
    </template>

    <template v-else>
      <v-card class="scrollBox">
        <v-card-title class="cardTitle"> 면적별 거래금액 </v-card-title>

        <v-card-text>
          <div
            class="cardItem"
            ref="topElement"
          >
            <v-table>
              <thead>
                <tr>
                  <th class="text-left">아파트명</th>
                  <th class="text-left">지번 주소</th>
                  <th class="text-left">건축년도</th>
                  <th class="text-left">면적</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{{ house.apartmentName }}</td>
                  <td>{{ house.fullName }}</td>
                  <td>{{ house.buildYear }}년</td>
                  <td>
                    <v-chip
                      v-for="(area, index) in areas"
                      :key="area"
                      @click="() => chipClick(index)"
                      >{{ area }}㎡</v-chip
                    >
                  </td>
                </tr>
              </tbody>
            </v-table>
          </div>
          <template v-for="chartData in chartDatas">
            <div class="chartItem">
              <div
                class="chartHeader"
                ref="chartRef"
              >
                {{ `${chartData.area}㎡` }}
              </div>
              <Line
                :data="{
                  labels: chartData.labels,
                  datasets: chartData.datasets
                }"
                :options="options"
              />
            </div>
            <v-divider></v-divider>
          </template>
          <div class="buttonContainer">
            <v-btn
              icon="mdi-pan-up"
              @click="clickUp"
            />
          </div>
        </v-card-text>
      </v-card>
    </template>

    <v-card-actions>
      <v-btn-secondary
        block
        @click="onOff = false"
        >Close</v-btn-secondary
      >
    </v-card-actions>
  </v-dialog>
</template>

<script setup>
import { ref, watch, defineProps, computed } from 'vue'
import { getAllDealListByHouseInfoCode } from '@/api/house.js'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

const topElement = ref(null)
const onOff = ref(false)
const house = ref(null)
const chartRef = ref([])
const resized = ref(false)

//차트 애니메이션 정의
const previousY = (ctx) =>
  ctx.index === 0
    ? ctx.chart.scales.y.getPixelForValue(100)
    : ctx.chart
        .getDatasetMeta(ctx.datasetIndex)
        .data[ctx.index - 1].getProps(['y'], true).y
const animation = {
  x: {
    type: 'number',
    easing: 'linear',
    duration: 100,
    from: NaN, // the point is initially skipped
    delay(ctx) {
      if (ctx.type !== 'data' || ctx.xStarted) {
        return 0
      }
      ctx.xStarted = true
      return ctx.index * 10
    }
  },
  y: {
    type: 'number',
    easing: 'linear',
    duration: 100,
    from: previousY,
    delay(ctx) {
      if (ctx.type !== 'data' || ctx.yStarted) {
        return 0
      }
      ctx.yStarted = true
      return ctx.index * 10
    }
  }
}

//차트 옵션 정의
const options = ref({
  animation,
  responsive: true,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    x: {
      display: true,
      title: {
        display: true,
        text: '거래날짜',
        font: {
          size: 15,
          weight: 'bold',
          lineHeight: 1.2
        }
      }
    },
    y: {
      display: true,
      title: {
        display: true,
        text: '거래금액(만 원)',
        font: {
          size: 15,
          weight: 'bold',
          lineHeight: 1.2
        }
      }
    }
  }
})

const props = defineProps({
  houseInfoCode: Number
})

//거래정보로부터 면적들 구성
const areas = computed(() => {
  if (house.value) {
    const set = new Set()
    const deals = house.value.deals

    deals.forEach((deal) => set.add(deal.area))
    const data = []
    set.forEach((s) => {
      data.push(s)
    })

    return data.sort((a, b) => a - b)
  } else {
    return []
  }
})

//거래정보로부터 차트 정보 구성
const chartDatas = computed(() => {
  if (house.value) {
    if (areas.value) {
      chartRef.value = null

      const deals = house.value.deals
      const datas = [] //최종적으로 차트에 사용될 데이터들을 넣자

      areas.value.forEach((area) => {
        //하나의 데이터를 만드는데 필요한 것이다
        const labels = []
        const data = []

        //해당 데이터의 면적이 일치하면
        deals.map((deal) => {
          if (deal.area === area) {
            labels.push(`${deal.dealYear}.${deal.dealMonth}`)
            data.push(deal.dealAmount)
          }
        })

        datas.push({
          area,
          labels,
          datasets: [
            {
              label: `${area}㎡`,
              backgroundColor: `rgb(${Math.floor(
                Math.random() * 255
              )}, ${Math.floor(Math.random() * 255)}, ${Math.floor(
                Math.random() * 255
              )})`,
              data
            }
          ]
        })
      })

      return datas
    }
  } else {
    return []
  }
  return [] //빨간줄 뜨길래 그냥 쓴거임
})

watch(async () => {
  if (onOff.value) {
    //만약 켜졌으면 -> API요청을 하자
    if (house.value) {
      //이미 fetch가 되었으면 넘기자
      return
    } else {
      //사용자가 처음 여는 거일 경우 데이터를 fetch하자
      const { data } = await getAllDealListByHouseInfoCode(props.houseInfoCode)
      house.value = data
    }
  }
})

//면적칩을 클릭하면 해당 chart로 이동시켜주자
const chipClick = (index) => {
  const target = chartRef.value[index]
  target.scrollIntoView({
    behavior: 'smooth'
  })
}

//위로 가기 버튼을 누를 경우 위로 올리기
const clickUp = () => {
  topElement.value.scrollIntoView({ behavior: 'smooth' })
}
</script>

<style scoped>
.cardTitle {
  text-align: center;
  font-size: 40px;
  font-weight: bold;
  padding-top: 20px;
}
.chartHeader {
  font-size: 30px;
  font-weight: bold;
  text-align: center;
}
.chartItem {
  padding-top: 30px;
  padding-bottom: 30px;
}

.scrollBox::-webkit-scrollbar {
  width: 10px;
}
.scrollBox::-webkit-scrollbar-thumb {
  background-color: rgba(128, 128, 128, 0.5);
  border-radius: 4px;
  background-clip: padding-box;
}
.scrollBox::-webkit-scrollbar-track {
  border-radius: 10px;
  box-shadow: inset 0px 0px 5px white;
}

.buttonContainer {
  position: fixed;
  top: 100%;
  left: 100%;
  transform: translate(-60px, -120px);
}

.resized {
  height: 0px !important;
}
</style>
