<template>
  <div
    class="d-flex align-center justify-center"
    style="height: 100vh"
  >
    <v-sheet
      width="450"
      class="mx-auto"
    >
      <div :class="`img-box`">
        <v-img
          class="mx-auto"
          :width="250"
          aspect-ratio="16/9"
          :src="`/logo.png`"
          alt="로고"
          lazy-src
        ></v-img>
      </div>
      <v-form
        v-model="form"
        fast-fail
        @submit.prevent="join"
      >
        <div :class="'inputBox'">
          <v-text-field
            :id="`id`"
            @update:modelValue="idCheck"
            variant="outlined"
            v-model="memberId"
            label="아이디"
            hint="아이디를 입력하세요"
            :rules="idRules"
            :error-messages="idError"
            clearable
          >
          </v-text-field>
        </div>
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
            :type="show2 ? 'text' : 'password'"
            :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="show2 = !show2"
            clearable
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
          회원가입
        </v-btn>
      </v-form>
    </v-sheet>
  </div>
  <!-- <v-form v-model="valid">
    <v-container>
      <v-row>
        <v-col cols="12" md="4">
          <v-text-field v-model="firstname" :rules="idRules" :counter="10" label="First name" required></v-text-field>
        </v-col>

        <v-col cols="12" md="4">
          <v-text-field v-model="lastname" :rules="nameRules" :counter="10" label="Last name" required></v-text-field>
        </v-col>

        <v-col cols="12" md="4">
          <v-text-field v-model="email" :rules="emailRules" label="E-mail" required></v-text-field>
        </v-col>
      </v-row>
    </v-container>
  </v-form> -->
</template>

<script setup>
import { ref } from 'vue'
import http from '@/api/instance'
import { debounce } from 'lodash'
import router from '@/router'

const form = ref(false)
const memberId = ref('')
const password = ref('')
const passwordValidate = ref('')
const email = ref('')
const nickname = ref('')
const birthday = ref(null)

const show = ref(false)
const show2 = ref(false)

const idError = ref('')
const nicknameError = ref('')

// let idMessage;
// idMessage = document.querySelector("#id-messages .v-messages__message");

const idRules = [
  (value) => {
    if (value?.length <= 20) return true

    return '아이디는 20글자 이하여야합니다.'
  }
]

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

const idCheck = () => {
  debounceIdCheck()
}

const debounceIdCheck = debounce(() => {
  http()
    .post(`http://localhost:8080/member/idCheck`, {
      memberId: memberId.value
    })
    .then(({ data }) => {
      if (data.code === 'SUCCESS') {
        idError.value = ''
      } else if (data.code === 'FAIL') {
        idError.value = '사용불가능한 아이디입니다.'
      }
    })
    .catch(() => {})
}, 250)

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

function join() {
  http()
    .post('http://localhost:8080/member', {
      memberId: memberId.value,
      password: password.value,
      email: email.value,
      nickname: nickname.value,
      birthday: birthday.value
    })
    .then(({ data }) => {
      if (data.code === 'SUCCESS') {
        window.alert('회원가입 성공')
        router.push({ name: 'home' })
      } else if (data.code === 'FAIL') {
        window.alert('회원가입 실패')
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

<style>
/* :root{
  --default: #54636b;
  --success: #2196F3;
  --error: #b00020;
} */

/* .v-messages__message{
} */
</style>
