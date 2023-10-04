import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, findById, logout } from '@/api/member'
import jwtDecode from 'jwt-decode'

export default defineStore(
  'member',
  () => {
    let isLogin = ref(false)
    let isLoginError = ref(false)
    let memberInfo = ref({
      memberCode: -1,
      memberId: '',
      nickname: ''
    })
    let isValidToken = ref(false)

    let gIsLogin = computed(() => isLogin.value)
    let gMemberInfo = computed(() => memberInfo.value)

    async function memberConfirm(member) {
      try {
        const { data } = await login(member)
        if (data.code === 'SUCCESS') {
          let accessToken = data.items['access-token']
          let refreshToken = data.items['refresh-token']

          isLogin.value = true
          isLoginError.value = false
          isValidToken.value = true
          sessionStorage.setItem('access-token', accessToken)
          sessionStorage.setItem('refresh-token', refreshToken)

          return true
        } else {
          isLogin.value = false
          isLoginError.value = true
          isValidToken.value = false

          return false
        }
      } catch (error) {
        return false
      }
    }
    async function getMemberInfo(token) {
      let decodeToken = jwtDecode(token)
      try {
        const { data } = await findById(decodeToken.memberId)

        // id에 일치하는 사람을 찾았음
        if (data.code === 'SUCCESS') {
          memberInfo.value = data.items.memberInfo
          return true
        }
        // id에 일치하는 사람을 찾지 못했음
        else if (data.code === 'FAIL') {
          return false
        }
        // 사용불가능 토큰
        else if (data.code === 'TOKEN_FAIL') {
          return false
        }
      } catch (error) {
        return false
      }
    }

    async function memberLogout() {
      let memberId = memberInfo.value.memberId

      try {
        const { data } = await logout(memberId)

        // id에 일치하는 사람을 찾았음
        if (data.code === 'SUCCESS') {
          isLogin.value = false
          memberInfo.value = {
            memberCode: -1,
            memberId: '',
            nickname: ''
          }
          isValidToken.value = false
          return true
        }

        // id에 일치하는 사람을 찾지 못했음
        else if (data.code === 'FAIL') {
          return
        }
      } catch (error) {
        return
      }
    }

    return {
      isLogin,
      isLoginError,
      memberInfo,
      isValidToken,
      gIsLogin,
      gMemberInfo,
      memberConfirm,
      getMemberInfo,
      memberLogout
    }
  },
  {
    persist: true
  }
)
