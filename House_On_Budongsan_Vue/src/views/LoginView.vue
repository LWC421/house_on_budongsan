<template>
  <div
    class="d-flex align-center justify-center"
    style="height: 100vh"
  >
    <v-sheet
      width="400"
      height="700"
      class="mx-auto"
    >
      <h2 class="loginTitle">로그인</h2>
      <v-form
        v-model="form"
        fast-fail
        @submit.prevent="login"
      >
        <div :class="'inputBox'">
          <v-text-field
            variant="outlined"
            v-model="memberId"
            label="id"
            :rules="idRules"
            hint="아이디를 입력하세요"
          >
          </v-text-field>
        </div>
        <div :class="'inputBox'">
          <v-text-field
            variant="outlined"
            v-model="password"
            :rules="passwordRules"
            :type="show ? 'text' : 'password'"
            :append-inner-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append-inner="show = !show"
            label="password"
            hint="비밀번호를 입력하세요"
          >
          </v-text-field>
        </div>

        <v-btn
          :disabled="!form"
          type="submit"
          color="primary"
          block
          class="mt-2"
          >로그인
        </v-btn>
      </v-form>
    </v-sheet>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import router from '@/router'
import { useMemberStore } from '@/stores'

const memberStore = useMemberStore()

const form = ref(false)
const show = ref(false)
const memberId = ref('')
const password = ref('')

const idRules = [
  (value) => {
    if (value) {
      return true
    }

    return '아이디를 입력해주세요'
  },
  (value) => {
    if (value?.length <= 20) return true

    return '아이디는 20글자이하여야 합니다.'
  }
]

const passwordRules = [
  (value) => {
    if (value) {
      return true
    }
    return '비밀번호를 입력해주세요'
  }
]

async function login() {
  let member = {
    memberId: memberId.value,
    password: password.value
  }
  try {
    const result_login = await memberStore.memberConfirm(member)
    // login이 정상적으로 됐다면
    if (result_login) {
      // 토큰이 휴효한 토큰인지 검사를 하자
      let token = sessionStorage.getItem('access-token')
      const result_token = await memberStore.getMemberInfo(token)
      // 유요한 토큰이라면
      if (result_token) {
        // 로그인 성공 alert를 띄어주고
        window.alert('로그인 성공')
        // 메인페이지로 routing해줌
        router.push({ name: 'home' })
      } else {
        // 로그인 실패 alert를 띄어준다.
        window.alert('아이디랑 비밀번호를 확인해주세요')
      }
    } else {
      // 로그인 실패 alert를 띄어준다.
      window.alert('아이디랑 비밀번호를 확인해주세요')
    }
  } catch (error) {
    return
  }
}
</script>

<style scoped>
.inputBox {
  margin-bottom: 10px;
}
.loginTitle {
  text-align: center;
  font-size: 70px;
  margin-bottom: 100px;
  color: rgb(230, 177, 126);
}
</style>

<style>
body {
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
</style>
