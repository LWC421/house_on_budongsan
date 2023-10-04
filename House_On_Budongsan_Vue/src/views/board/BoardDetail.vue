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
        :readonly="validation"
      >
      </v-text-field>
      <v-textarea
        v-model="content"
        label="내용"
        variant="solo"
        :rules="contentRules"
        rows="15"
        :readonly="validation"
      >
      </v-textarea>
      <div class="write-container">
        <v-btn
          v-if="!validation"
          @click="goRemoveArticle"
          width="100"
          color="#E6B17E"
          style="margin-top: 20px"
          >삭제하기</v-btn
        >
        <v-btn
          v-if="!validation"
          :disabled="!form"
          @click="goModifyArticle()"
          width="100"
          color="#E6B17E"
          style="margin-top: 20px; margin-right: 20px"
          >수정하기</v-btn
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
import { modifyArticle, getArticle, removeArticle } from '@/api/board'
import { ref } from 'vue'
import { useMemberStore } from '@/stores'
import router from '@/router'
import { useRoute } from 'vue-router'

const memberStore = useMemberStore()

const validation = ref(true)
const route = useRoute()
const form = ref(false)
const title = ref('')
const content = ref('')

if (route.params.memberCode == memberStore.memberInfo.memberCode) {
  validation.value = false
} else {
  validation.value = true
}

;(async () => {
  let sendData = {
    boardCode: route.params.boardCode,
    memberCode: memberStore.memberInfo.memberCode
  }

  const { data } = await getArticle(sendData)
  title.value = data.items.title
  content.value = data.items.content
})()

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

async function goModifyArticle() {
  let board = {
    title: title.value,
    content: content.value,
    boardCode: route.params.boardCode
  }
  let { data } = await modifyArticle(board)
  if (data.code === 'SUCCESS') {
    window.alert('글수정에 성공했습니다.')
    router.push({ name: 'board' })
  } else if (data.code === 'FAIL') {
    window.alert('글수정에 실패했습니다.')
  }
}

async function goRemoveArticle() {
  let { data } = await removeArticle(route.params.boardCode)
  if (data.code === 'SUCCESS') {
    window.alert('글삭제에 성공했습니다.')
    router.push({ name: 'board' })
  } else if (data.code === 'FAIL') {
    window.alert('글삭제에 실패했습니다.')
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
