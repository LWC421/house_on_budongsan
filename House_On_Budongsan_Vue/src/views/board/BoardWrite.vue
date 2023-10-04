<template>
  <div id="board-body">
    <v-form
      fast-fail
      v-model="form"
    >
      <div id="header-name">글쓰기</div>
      <v-text-field
        v-model="title"
        label="제목"
        variant="solo"
        :rules="titleRules"
      >
      </v-text-field>
      <v-textarea
        v-model="content"
        label="내용"
        variant="solo"
        :rules="contentRules"
        rows="15"
      >
      </v-textarea>
      <div class="write-container">
        <v-btn
          :disabled="!form"
          @click="goWriteArticle()"
          width="100"
          color="#E6B17E"
          style="margin-top: 20px"
          >글작성</v-btn
        >
        <v-btn
          @click="moveBoard"
          width="100"
          color="#E6B17E"
          style="margin-top: 20px; margin-right: 20px"
          >글목록</v-btn
        >
      </div>
    </v-form>
  </div>
</template>

<script setup>
import { writeArticle } from '@/api/board'
import { ref } from 'vue'
import { useMemberStore } from '@/stores'
import router from '@/router'

const memberStore = useMemberStore()

const form = ref(false)
const title = ref('')
const content = ref('')

const titleRules = [
  (value) => {
    if (value) return true

    return '제목을 입력해야합니다.'
  }
]

const contentRules = [
  (value) => {
    if (value) return true

    return '내용을 입력해야합니다.'
  }
]

async function goWriteArticle() {
  let board = {
    title: title.value,
    content: content.value,
    memberCode: memberStore.memberInfo.memberCode
  }
  let { data } = await writeArticle(board)
  if (data.code === 'SUCCESS') {
    window.alert('글쓰기에 성공했습니다.')
    router.push({ name: 'board' })
  } else if (data.code === 'FAIL') {
    window.alert('글쓰기에 실패했습니다.')
  }
}

function moveBoard() {
  router.push({ name: 'board' })
}
</script>

<style scoped>
#board-body {
  height: 80vh;
  padding-left: 30px;
  padding-right: 30px;
  padding-top: 40px;
}

#header-name {
  font-weight: bold;
  font-size: 25px;
  margin-bottom: 30px;
}
.write-container {
  display: flex;
  flex-direction: row-reverse;
}
</style>
