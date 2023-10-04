<template>
  <div
    class="d-flex align-center justify-center"
    style="height: 80vh"
  >
    <v-sheet
      width="450"
      class="mx-auto"
    >
      <v-form
        v-model="form"
        fast-fail
        @submit.prevent="modify"
      >
        <div :class="'inputBox password'">
          <v-text-field
            variant="outlined"
            v-model="password"
            :rules="passwordRules"
            label="비밀번호"
            hint="비밀번호를 입력하세요"
            clearable
            :type="show ? 'text' : 'password'"
            :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="show = !show"
          >
            <template v-slot:append-inner>
              <v-tooltip location="bottom">
                <template v-slot:activator="{ props }">
                  <v-icon
                    v-bind="props"
                    icon="mdi-help-circle-outline"
                  ></v-icon>
                </template>
                영문, 숫자, 특수기호 조합으로 8-20자리 이상 입력해주세요.
              </v-tooltip>
            </template>
          </v-text-field>
        </div>
        <div :class="'inputBox password'">
          <v-text-field
            variant="outlined"
            v-model="passwordValidate"
            label="비밀번호 확인"
            hint="비밀번호를 다시 입력해주세요"
            :rules="passwordCheckRules"
            clearable
            :type="show2 ? 'text' : 'password'"
            :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="show2 = !show2"
          >
          </v-text-field>
        </div>
        <div :class="'inputBox'">
          <v-text-field
            @update:modelValue="nicknameCheck"
            variant="outlined"
            v-model="nickname"
            label="닉네임"
            hint="닉네임을 입력하세요"
            :rules="nicknameRules"
            :error-messages="nicknameError"
            clearable
          >
          </v-text-field>
        </div>
        <div :class="'inputBox'">
          <v-text-field
            variant="outlined"
            v-model="email"
            label="email"
            hint="이메일을 입력하세요"
            :rules="emailRules"
            clearable
          >
          </v-text-field>
        </div>
        <div :class="'inputBox'"></div>
        <v-btn
          :disabled="!form"
          type="submit"
          color="primary"
          block
          class="mt-2"
        >
          수정하기
        </v-btn>
      </v-form>
      <v-btn
        color="primary"
        block
        class="mt-2"
        @click="deleteMember"
      >
        탈퇴하기
      </v-btn>
    </v-sheet>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import http from '@/api/instance'
import { debounce } from 'lodash'
import router from '@/router'
import { useMemberStore } from '@/stores'

const memberStore = useMemberStore()

const password = ref('')
const passwordValidate = ref('')
const email = ref('')
const nickname = ref('')
const birthday = ref(null)

const show = ref(false)
const show2 = ref(false)

const nicknameError = ref('')
const logout = memberStore.memberLogout

const emailRules = [
  (value) => {
    if (value) return true

    return '이메일을 입력해야합니다.'
  },
  (value) => {
    if (/.+@.+\..+/.test(value)) return true

    return '이메일 형식이 아닙니다.'
  }
]

const passwordRules = [
  (value) => {
    if (value) return true

    return '비밀번호를 입력해야합니다.'
  },
  (value) => {
    if (/^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$/.test(value))
      return true

    return '영문, 숫자, 특수기호 조합으로 8-20자리 이상 입력해주세요.'
  }
]

const passwordCheckRules = [
  (value) => {
    if (value) return true

    return '비밀번호를 다시 한번 입력해주세요'
  },
  (value) => {
    if (value === password.value) {
      return true
    }

    return '비밀번호랑 다릅니다.'
  }
]

const nicknameRules = [
  (value) => {
    if (value) return true

    return '닉네임을 입력해야합니다.'
  }
]

function nicknameCheck() {
  debounceNicknameCheck()
}

const debounceNicknameCheck = debounce(() => {
  http()
    .post(`http://localhost:8080/member/nicknameCheck`, {
      nickname: nickname.value
    })
    .then(({ data }) => {
      if (data.code === 'SUCCESS') {
        nicknameError.value = ''
      } else if (data.code === 'FAIL') {
        nicknameError.value = '사용불가능한 닉네임입니다.'
      }
    })
    .catch(() => {})
}, 250)

function modify() {
  http()
    .post('http://localhost:8080/member/modify', {
      memberCode: memberStore.memberInfo.memberCode,
      password: password.value,
      email: email.value,
      nickname: nickname.value,
      birthday: birthday.value
    })
    .then(({ data }) => {
      if (data.code === 'SUCCESS') {
        window.alert('정보수정 성공')
        logout()
        window.alert('다시 로그인하세요!')
        router.push({ name: 'login' })
      } else if (data.code === 'FAIL') {
        window.alert('정보수정 실패')
      }
    })
}
function deleteMember() {
  http()
    .delete(`http://localhost:8080/member/${memberStore.memberInfo.memberId}`)
    .then(({ data }) => {
      if (data.code === 'SUCCESS') {
        window.alert('회원탈퇴 성공')
        logout()
        router.push({ name: 'home' })
      } else if (data.code === 'FAIL') {
        window.alert('회원탈퇴 실패')
      }
    })
}
</script>

<style scoped>
.img-box {
  margin-bottom: 50px;
}
.inputBox {
  margin-bottom: 15px;
}
.password {
  width: 490px;
}
</style>
