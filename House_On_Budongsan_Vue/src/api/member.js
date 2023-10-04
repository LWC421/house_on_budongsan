import getInstance from './instance.js'

const instance = getInstance()

function login(member) {
  return instance.post(`/member/login`, JSON.stringify(member))
}

function findById(memberId) {
  instance.defaults.headers['access-token'] =
    sessionStorage.getItem('access-token')
  return instance.get(`/member/info/${memberId}`)
}

function tokenRegeneration(member) {
  instance.defaults.headers['refresh-token'] =
    sessionStorage.getItem('refresh-token') //axios header에 refresh-token 셋팅
  return instance.post(`/member/refresh`, member)
}

function logout(memberId) {
  return instance.get(`/member/logout/${memberId}`)
}

export { login, findById, tokenRegeneration, logout }
