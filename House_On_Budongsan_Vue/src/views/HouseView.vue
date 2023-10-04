<template>
  <div class="top">
    <div class="topLeft">
      <search-bar
        :click="searchEvent"
        @onChange="onChangeSearch"
      ></search-bar>
    </div>
    <div class="topCenter">
      <h3>{{ year }}.{{ month }}</h3>
      <v-pagination
        v-model="currPage"
        :length="maxPage"
        :total-visible="Math.min(maxPage, 6)"
        rounded="circle"
        @update:modelValue="pageUpdated"
      >
      </v-pagination>
    </div>
    <div class="topRight">
      <div class="likeFirst">
        <v-checkbox
          label="좋아요부터"
          v-model="likeFirst"
          density="compact"
        />
      </div>
      <div class="sortBy">
        <v-select
          v-model="sortBy"
          :items="[
            { title: '최저가', value: 'minAmount' },
            { title: '최고가', value: 'maxAmount' },
            { title: '최저면적', value: 'minArea' },
            { title: '최고면적', value: 'maxArea' }
          ]"
          density="compact"
        />
      </div>

      <search-filter
        :="{
          year,
          month,
          lowerAmount,
          upperAmount,
          lowerArea,
          upperArea
        }"
        @onChange="changeFilter"
      ></search-filter>
    </div>
  </div>
  <div class="bottom">
    <div class="bottomLeft">
      <kakao-map
        :="{ ...mapState }"
        @setMap="setMap"
        @reload="reload"
      ></kakao-map>
    </div>

    <div class="bottomRight">
      <div
        v-if="loading"
        class="resultLoading"
      >
        <h2>검색 결과</h2>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
        <v-skeleton-loader
          :elevation="1"
          type="list-item-avatar"
          min-height="100px"
        ></v-skeleton-loader>
      </div>

      <result-table
        v-else
        :="{ map, houses }"
        @clickEvent="clickEvent"
        @addSave="addSave"
        @removeSave="removeSave"
        @addHeart="addHeart"
        @removeHeart="removeHeart"
      ></result-table>
      <save-table
        :="{ map, saves }"
        @clickEvent="clickEvent"
        @removeSave="removeSave"
        @addHeart="addHeart"
        @removeHeart="removeHeart"
      ></save-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  getDealList,
  getLikeList,
  addLike,
  removeLike,
  getDealListByLatLng
} from '@/api/house.js'
import { useMemberStore } from '@/stores'

import { KakaoMap } from '@/components/map'
import { SearchBar, SearchFilter } from '@/components/search'
import { ResultTable, SaveTable } from '@/components/house'

import { VSkeletonLoader } from 'vuetify/labs/VSkeletonLoader'

import { debounce } from 'lodash'

const memberStore = useMemberStore()
const { isLogin, memberInfo } = memberStore

const map = ref(null)

const mapState = ref({
  lat: 33.450701,
  lng: 126.570667,
  ranges: [
    ['0', '0'],
    ['0', '0'],
    ['0', '0']
  ]
})

const originHouses = ref([]) //API로 받아온 집과 그에 대한 거래 정보들
const likes = ref(new Set()) //좋아요한 집 목록들
const prevWindow = ref(null) //이전에 열린 인포윈도우 저장
const loading = ref(false) //API콜로 인한 로딩중인지 확인용
const originSaves = ref([]) //사용자가 임시 저장한것
const likeFirst = ref(isLogin) //좋아요부터 보여줄건지

//로그인 되어있을 경우 내가 좋아요 누른 건물들의 리스트를 받아오자
;(async () => {
  if (!isLogin) {
    return
  }
  try {
    const { data } = await getLikeList(memberInfo.memberCode)
    data.forEach((houseInfoCode) => {
      likes.value.add(houseInfoCode) //좋아요 누른 건물들을 넣어주자
    })
  } catch (error) {
    return
  }
})()

//사용자가 좋아요 누른거 인지 확인하기
const saves = computed(() => {
  return originSaves.value.map((house) => {
    if (likes.value.has(house.houseInfoCode)) {
      //만약 내가 좋아요 누른 거 이면
      return { ...house, isLiked: true }
    } else {
      return { ...house, isLiked: false }
    }
  })
})

//성능 개선을 위해
const saveSet = computed(() => {
  const set = new Set()
  originSaves.value.forEach((house) => {
    house.deals.forEach((deal) => {
      set.add(deal.houseDealCode)
    })
  })
  return set
})

//검색에 필요한 년도 및 월 정의
const year = ref(2022)
const month = ref(4)

//검색 키워드들 정의
const searchBy = ref('location')
const keyword = ref(0)

//실제로 필터링에 사용되어 사용자가 변경할 데이터 정의
const lowerAmount = ref(0)
const upperAmount = ref(1200000)
const lowerArea = ref(5)
const upperArea = ref(425)

//사용자가 정렬 조건을 줄 수 있도록 하기
const sortBy = ref('minAmount')

//사용자가 좋아요 누른거 인지 확인하기
const housedWithLike = computed(() => {
  return originHouses.value.map((house) => {
    if (likes.value.has(house.houseInfoCode)) {
      //만약 내가 좋아요 누른 거 이면
      return { ...house, isLiked: true }
    } else {
      return { ...house, isLiked: false }
    }
  })
})

//필터링 등에 의해 걸러진 정보들
const filteredHouses = computed(() => {
  const tempHouses = []

  let sortFunction = null

  //정렬 기준에 따라 정렬함수를 정해주자
  if (sortBy.value === 'minAmount') {
    sortFunction = (dealA, dealB) => {
      if (dealA.dealAmount > dealB.dealAmount) return 1
      if (dealA.dealAmount === dealB.dealAmount) return 0
      if (dealA.dealAmount < dealB.dealAmount) return -1
    }
  } else if (sortBy.value === 'maxAmount') {
    sortFunction = (dealA, dealB) => {
      if (dealA.dealAmount < dealB.dealAmount) return 1
      if (dealA.dealAmount === dealB.dealAmount) return 0
      if (dealA.dealAmount > dealB.dealAmount) return -1
    }
  } else if (sortBy.value === 'minArea') {
    sortFunction = (dealA, dealB) => {
      if (dealA.area > dealB.area) return 1
      if (dealA.area === dealB.area) return 0
      if (dealA.area < dealB.area) return -1
    }
  } else if (sortBy.value === 'maxArea') {
    sortFunction = (dealA, dealB) => {
      if (dealA.area < dealB.area) return 1
      if (dealA.area === dealB.area) return 0
      if (dealA.area > dealB.area) return -1
    }
  }

  for (let i = 0; i < housedWithLike.value.length; i++) {
    const house = { ...housedWithLike.value[i] }
    house.deals = [] //일단 거래정보 비우고

    //거래가격 및 면적을 기준으로 필터링
    const deals = housedWithLike.value[i].deals.filter((deal) => {
      if (
        deal.dealAmount >= lowerAmount.value &&
        deal.dealAmount <= upperAmount.value &&
        deal.area >= lowerArea.value &&
        deal.area <= upperArea.value
      ) {
        return true
      } else {
        return false
      }
    })

    //필터링 된 후에도 거래정보가 남아있을 때만
    if (deals.length !== 0) {
      //해당 집의 최소 최대의 가격과 면적들을 구해주자
      let minAmount = 120000000
      let maxAmount = 0
      let minArea = 500
      let maxArea = 0

      deals.forEach((deal) => {
        if (minAmount > deal.dealAmount) minAmount = deal.dealAmount
        if (maxAmount < deal.dealAmount) maxAmount = deal.dealAmount
        if (minArea > deal.area) minArea = deal.area
        if (maxArea < deal.area) maxArea = deal.area
      })

      //집 객체에 최소 최대의 값들의 정보를 넣어주자
      house.minAmount = minAmount
      house.maxAmount = maxAmount
      house.minArea = minArea
      house.maxArea = maxArea

      //각 집의 거래정보들을 정렬된 것을 넣어주자
      house.deals = deals.sort(sortFunction)

      //사용자가 저장한 집일 경우 resulttable에서는 마커를 찍지 않아야 한다
      let withMarker = true
      for (let i = 0; i < deals.length; i++) {
        const deal = deals[i]
        if (saveSet.value.has(deal.houseDealCode)) withMarker = false
      }
      house.withMarker = withMarker
      tempHouses.push(house)
    }
  }

  return tempHouses
})

//필터링된 house에 대해 정렬을 수행해주자
const sortedHouses = computed(() => {
  if (filteredHouses.value.length === 0) {
    //아무것도 없으면 그냥 반환
    return []
  }

  let sortFunction = null

  //정렬 기준에 따라 정렬함수를 정해주자
  if (sortBy.value === 'minAmount') {
    sortFunction = (houseA, houseB) => {
      if (houseA.minAmount > houseB.minAmount) return 1
      if (houseA.minAmount === houseB.minAmount) return 0
      if (houseA.minAmount < houseB.minAmount) return -1
    }
  } else if (sortBy.value === 'maxAmount') {
    sortFunction = (houseA, houseB) => {
      if (houseA.maxAmount < houseB.maxAmount) return 1
      if (houseA.maxAmount === houseB.maxAmount) return 0
      if (houseA.maxAmount > houseB.maxAmount) return -1
    }
  } else if (sortBy.value === 'minArea') {
    sortFunction = (houseA, houseB) => {
      if (houseA.minArea > houseB.minArea) return 1
      if (houseA.minArea === houseB.minArea) return 0
      if (houseA.minArea < houseB.minArea) return -1
    }
  } else if (sortBy.value === 'maxArea') {
    sortFunction = (houseA, houseB) => {
      if (houseA.maxArea < houseB.maxArea) return 1
      if (houseA.maxArea === houseB.maxArea) return 0
      if (houseA.maxArea > houseB.maxArea) return -1
    }
  }

  //실제로 집의 정보들을 정렬해주자

  let sortHouses = filteredHouses.value.sort(sortFunction)

  //정렬된 집들에 대해
  //3개의 범위로 나누어서 3등분 해주자
  const range1 = Math.floor(sortHouses.length / 3)
  const range2 = Math.floor((sortHouses.length / 3) * 2)

  //범위에 따라 rank를 정해주자
  for (let i = 0; i < range1; i++) {
    sortHouses[i].rank = 1
  }
  for (let i = range1; i < range2; i++) {
    sortHouses[i].rank = 2
  }
  for (let i = range2; i < sortHouses.length; i++) {
    sortHouses[i].rank = 3
  }

  if (sortHouses.length !== 0) {
    //3개의 범위로 나누어서 3등분 해주자
    const range1 = Math.floor(sortHouses.length / 3)
    const range2 = Math.floor((sortHouses.length / 3) * 2)
    //3등분된 범위를 맵의 legend에서 사용할 수 있도록 해주자

    let range0Value = 0
    let range1Value = 0
    let range2Value = 0
    let range3Value = 0

    if (sortBy.value === 'minAmount' || sortBy.value === 'maxAmount') {
      //가격에 따른 정렬이면
      range0Value = sortHouses[0].deals[0].dealAmount
      range1Value = sortHouses[range1].deals[0].dealAmount
      range2Value = sortHouses[range2].deals[0].dealAmount
      range3Value =
        sortHouses[sortHouses.length - 1].deals[
          sortHouses[sortHouses.length - 1].deals.length - 1
        ].dealAmount
    } else {
      //면적에 따른 정렬이면
      range0Value = sortHouses[0].deals[0].area
      range1Value = sortHouses[range1].deals[0].area
      range2Value = sortHouses[range2].deals[0].area
      range3Value =
        sortHouses[sortHouses.length - 1].deals[
          sortHouses[sortHouses.length - 1].deals.length - 1
        ].area
    }

    //오름차순 내림차순에 따라 범위의 순서를 바꿔줘야 한다
    if (sortBy.value === 'minAmount' || sortBy.value === 'minArea') {
      mapState.value.ranges[0] = [range0Value, range1Value]
      mapState.value.ranges[1] = [range1Value, range2Value]
      mapState.value.ranges[2] = [range2Value, range3Value]
    } else {
      mapState.value.ranges[0] = [range1Value, range0Value]
      mapState.value.ranges[1] = [range2Value, range1Value]
      mapState.value.ranges[2] = [range3Value, range2Value]
    }

    //가격인지 면적인지에 따라 끝 문자열을 변경시켜주자
    if (sortBy.value === 'minAmount' || sortBy.value === 'maxAmount') {
      mapState.value.ranges = mapState.value.ranges.map((range) => {
        return range.map((value) => {
          return value.toLocaleString() + '만원'
        })
      })
    } else {
      mapState.value.ranges = mapState.value.ranges.map((range) => {
        return range.map((value) => {
          return value + '㎡'
        })
      })
    }
  }

  if (likeFirst.value) {
    //만약 좋아요부터 이면
    sortHouses = sortHouses.sort((houseA, houseB) => {
      const houseALike = likes.value.has(houseA.houseInfoCode)
      const houseBLike = likes.value.has(houseB.houseInfoCode)

      if (houseALike && !houseBLike) return -1
      else if ((houseALike && houseBLike) || (!houseALike && !houseBLike))
        return 0
      else if (!houseALike && houseBLike) return 1
    })
  }

  return sortHouses
})

//최종적으로 페이지네이션을 적용해서 사용자에게 보여주자
const houses = computed(() => {
  //페이지네이션을 위한 시작점과 끝점 잡기
  let start = (currPage.value - 1) * 100
  let end = currPage.value * 100

  if (end > sortedHouses.value.length) {
    //만약 끝을 변경해야 되면
    end = sortedHouses.value.length
  }

  return sortedHouses.value.slice(start, end)
})

//필요한 페이지수 계산
const maxPage = computed(() => {
  return Math.ceil(filteredHouses.value.length / 100)
})
const currPage = ref(1)
const pageUpdated = () => {
  //페이지가 변경되면
  if (prevWindow.value) {
    //그전에 켜진 인포윈도우가 있으면
    prevWindow.value.close()
  }
}

//위치기반으로 새로고침을 할 경우
const reload = async (map) => {
  const level = map.getLevel()
  const latlng = map.getCenter()
  const lat = latlng.getLat()
  const lng = latlng.getLng()

  try {
    loading.value = true //일단 로딩중이라고 하기
    const { data } = await getDealListByLatLng({
      level,
      lat,
      lng,
      year: year.value,
      month: month.value
    })
    currPage.value = 1 //페이지네이션을 처음으로 돌리자

    if (prevWindow.value) {
      //그전에 켜진 인포윈도우가 있으면
      prevWindow.value.close()
    }

    //거래에 대해 조회된 정보가 있으면
    originHouses.value = data.map((house) => {
      house.deals = house.deals.map((deal) => {
        let isSaved = false
        if (saveSet.value.has(deal.houseDealCode)) {
          //만약 저장된 거래정보이면 true로 해주자
          isSaved = true
        }
        return { ...deal, isSaved } //저장된 객체인지 아닌지 넣어주자
      })
      return house
    })
  } catch (error) {}

  loading.value = false //로딩이 끝났다고 표시하기
}

//기존에 저장된 값이 있다면 불러오자
const saved = localStorage.getItem('savedHouses')
if (saved) {
  originSaves.value = JSON.parse(saved)
}

const route = useRoute()
const query = route.query

//쿼리로부터 데이터를 가져온다
const dealListFetch = async (query) => {
  loading.value = true //일단 로딩중이라고 하기
  const { data } = await getDealList({ ...query })
  changeScale() //지도 스케일 변경

  loading.value = false //로딩이 끝났다고 표시하기

  currPage.value = 1 //페이지네이션을 처음으로 돌리자
  if (prevWindow.value) {
    //그전에 켜진 인포윈도우가 있으면
    prevWindow.value.close()
  }
  if (data.length === 0) {
    //만약 거래에 대한 것이 없으면
    originHouses.value = []
  } else {
    //거래에 대해 조회된 정보가 있으면
    originHouses.value = data.map((house) => {
      house.deals = house.deals.map((deal) => {
        let isSaved = false
        if (saveSet.value.has(deal.houseDealCode)) {
          //만약 저장된 거래정보이면 true로 해주자
          isSaved = true
        }
        return { ...deal, isSaved } //저장된 객체인지 아닌지 넣어주자
      })
      return house
    })

    //중심 좌표를 찍어주자
    let lat = 0
    let lng = 0
    data.forEach((d) => {
      lat += d.lat
      lng += d.lng
    })

    lat /= data.length
    lng /= data.length

    mapState.value.lat = lat
    mapState.value.lng = lng
  }
}

//스케일을 변경해주자
const changeScale = () => {
  mapState.value.scale = 1
  //지역검색일 경우
  if (searchBy.value === 'location') {
    if (keyword.value >= 10 && keyword.value <= 99) {
      //`시`기준 검색일 경우
      if (map.value) {
        map.value.setLevel(9)
      }
    } else if (keyword.value >= 10000 && keyword.value <= 99999) {
      //`구군`기준 검색일 경우
      if (map.value) {
        map.value.setLevel(7)
      }
    } else {
      //동 기준 검색일 경우
      if (map.value) {
        map.value.setLevel(5)
      }
    }
  } else if (searchBy.value === 'building') {
    //건물 기준 검색일 경우
    if (map.value) {
      map.value.setLevel(3)
    }
  }
}

//home에서 올 때 쿼리를 가져오므로 그것을 처리해주자
onMounted(() => {
  if (Object.keys(query).length !== 0) {
    //쿼리가 있을 경우
    if (
      !(query.searchBy === 'location' || query.searchBy === 'building') ||
      !query.searchKeyword
    ) {
      alert('잘못된 요청입니다!')
    } else {
      //제대로 쿼리를 날릴 준비가 되었으면
      searchBy.value = query.searchBy
      keyword.value = query.searchKeyword
      changeScale()

      dealListFetch(query)
    }
  } else {
    //쿼리가 없을 경우이다
  }
})

//debounce적용
const APICall = debounce((value) => {
  if (!value.searchKeyword) {
    alert('검색어를 입력해주세요')
  } else {
    dealListFetch(value)
  }
}, 500)

//검색을 하였을 때
const searchEvent = ({ searchBy, searchKeyword }) => {
  APICall({
    searchBy,
    searchKeyword,
    year: year.value,
    month: month.value
  })
}

const onChangeSearch = (params) => {
  searchBy.value = params.searchBy
  keyword.value = params.searchKeyword
}

const filterDebounce = debounce((filters) => {
  lowerAmount.value = filters.lowerAmount
  upperAmount.value = filters.upperAmount
  lowerArea.value = filters.lowerArea
  upperArea.value = filters.upperArea
  currPage.value = 1 //다시 1페이지로 넘기기
}, 100)

//필터링 값이 변경될 때에 대해 정의
const changeFilter = (filters) => {
  filterDebounce(filters)
  if (year.value !== filters.year || month.value !== filters.month) {
    //만약 년도나 월이 바뀌는 거일 경우
    year.value = filters.year
    month.value = filters.month
    APICall({
      searchBy: searchBy.value,
      searchKeyword: keyword.value,
      year: year.value,
      month: month.value
    })
  }
}

//맵 객체를 넣어주는 부분
const setMap = (initMap) => {
  map.value = initMap
}

//마커 또는 옆 리절트 창의 아이템을 클릭했을때 이벤트 정의
const clickEvent = ({ house, item, window, marker }) => {
  mapState.value.lat = house.lat
  mapState.value.lng = house.lng
  if (map.value) {
    map.value.setLevel(3)
  }

  if (prevWindow.value) {
    //그전에 열린 인포윈도우가 있으면
    prevWindow.value.close() //닫아주기
  }

  window.open(map.value, marker) //인포윈도우 열어주기
  prevWindow.value = window //현재 객체를 미리 저장

  item.scrollIntoView({
    behavior: 'smooth'
  })
}

//거래정보에서 save를 눌렀을 때
const addSave = ({ house, deal }) => {
  const targetHouse = originSaves.value.find(
    (save) => save.houseInfoCode === house.houseInfoCode
  )

  deal.isSaved = true //저장되었다고 표시하고

  if (!targetHouse) {
    //저장된 집 객체가 없으면
    const newHouse = { ...house }
    newHouse.deals = []
    newHouse.deals.push(deal) //deal객체를 추가해주자
    originSaves.value.push(newHouse) //새롭게 생긴 집 정보를 넣어주자
  } else {
    //저장된 집 객체가 있으면
    targetHouse.deals.push(deal) //거래정보만 추가로 넣어주자
  }
  localStorage.setItem('savedHouses', JSON.stringify(originSaves.value)) //로컬 스토리지에도 저장한다
}

//거래정보또는 저장된 정보에서 remove를 눌렀을 때
const removeSave = ({ house, deal }) => {
  deal.isSaved = false //저장되지 않았다고 표시하기
  const targetHouse = originSaves.value.find(
    (save) => save.houseInfoCode === house.houseInfoCode
  )

  targetHouse.deals = targetHouse.deals.filter(
    (d) => !(d.houseDealCode === deal.houseDealCode)
  )
  if (targetHouse.deals.length === 0) {
    //만약 삭제했는데 하위 deal들이 모두 없으면
    originSaves.value = originSaves.value.filter(
      (save) => !(save.houseInfoCode === house.houseInfoCode)
    )
  }
  localStorage.setItem('savedHouses', JSON.stringify(originSaves.value)) //로컬 스토리지에도 저장한다
}

//거래정보또는 저장된 정보에서 좋아요를 눌렀을 때
const addHeart = async (houseInfoCode) => {
  try {
    await addLike({ memberCode: memberInfo.memberCode, houseInfoCode }) //추가성공
    likes.value.add(houseInfoCode)
  } catch (error) {
    //추가실패
    alert('서버 에러...')
  }
}

//거래정보또는 저장된 정보에서 좋아요 취소를 눌렀을 때
const removeHeart = async (houseInfoCode) => {
  try {
    await removeLike({ memberCode: memberInfo.memberCode, houseInfoCode }) //추가성공
    likes.value.delete(houseInfoCode)
  } catch (error) {
    //추가실패
    alert('서버 에러...')
  }
}

//좋아요부터를 눌렀을때 로그인 되었는지 아닌지 확인하자
watch(() => {
  if (likeFirst.value && !isLogin) {
    alert('로그인 후 사용가능합니다')
    likeFirst.value = false
  }
})
</script>

<style scoped>
.top {
  display: flex;
  flex-direction: row;
  justify-content: space-around;

  height: 100px;
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 5px;
}
.topLeft {
  width: 33%;
}
.topCenter {
  width: 33%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
.topRight {
  width: 33%;
  display: flex;
  justify-content: end;
  align-items: center;
  gap: 20px;
}
.likeFirst {
  width: 110px;
  height: 100%;

  padding-top: 28px;
}
.sortBy {
  padding-top: 20px;
  display: flex;
  align-items: center;
}
.bottom {
  width: 100%;
  height: 675px;
  display: flex;
  flex-direction: row;
}

.bottomLeft {
  width: 100%;
  height: 100%;
}

.resultLoading {
  width: 300px;
  padding-left: 10px;
  padding-right: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

h2 {
  text-align: center;
  padding-bottom: 10px;
}

.bottomRight {
  min-width: 560px;
  height: 100%;
  display: flex;
  flex-direction: row;
  gap: 5px;
}
.resultTable {
  height: 100%;
  width: 50%;
}
.saveTable {
  height: 100%;
  width: 50%;
}
</style>
