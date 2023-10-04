<template>
  <div id="gridContainer">
    <div 
    v-for="like in likes" 
    :key="like.houseInfoCode"
    class="likeContainer"
    >
      <img 
        :src="like.src" 
        width="300"
        height="350"
        class="backgroundImage"
        loading="lazy"
      />
      <div 
        class="likeItem"
        >
        <div class="apart">{{ like.apartmentName }}</div>
        <div class="name">{{ like.fullName }}</div>
        <div class="eventContainer">
          <chart-modal :houseInfoCode="like.houseInfoCode"></chart-modal>
          <v-btn
            icon="mdi-map-search-outline"
            density="compact"
            variant="tonal"
            color="#C56E33"
            @click="() => clickMap(like.houseInfoCode)"
            />
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router"
import { useMemberStore } from "@/stores"
import { getLikeListDetail, getImageList } from "@/api/house.js"
import {ChartModal} from "@/components/house"

const router = useRouter();
const memberStore = useMemberStore();
const { isLogin, memberInfo } = memberStore;
const likes = ref([]);  //좋아요 된 집 객체들


//만약 로그인 상태가 아니면
if (!isLogin) {
  alert("잘못 된 접근 입니다")
  router.replace({name: "home"})
}

(async () => {
  try {
    const {data} = await getLikeListDetail(memberInfo.memberCode);
    likes.value = data.map((d) => {
      return {...d, src:""}
    });
  }
  catch (error) {
    alert("잘못 된 접근")
  }
})()

watch(async() => {
  if (likes.value.length !== 0) {
    const { data } = await getImageList(likes.value.length);

    data.forEach((d, index) => {
      likes.value[index].src = d.download_url;
    })
  }
})

//지도에서 보기를 클릭한 경우
//지도로 보내자
const clickMap = (houseInfoCode) => {
  router.push({
    name: "house",
    query: {
      searchBy: "building",
      searchKeyword: houseInfoCode,
      year: 2022,
      month: 4,
    }
  })
}

</script>

<style scoped>
#gridContainer{
  display: grid;
  grid-template-columns: repeat(auto-fit, 300px);
  justify-content: center;
  gap: 20px;
}
.likeContainer{
  width: 300px;
  height: 450px;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  
  border: 1px solid rgb(210, 210, 210);
  box-shadow: 2px 2px 2px 0px rgba(180, 180, 180, 0.5);
  border-radius: 5px;
}
.likeItem{
  width: 300px;
  height: 100px;
  
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.apart{
  font-size: 20px;
  font-weight: bold;
}
.name{
  font-size: 12px;
  padding-bottom: 5px;
}

.eventContainer{
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
</style>
