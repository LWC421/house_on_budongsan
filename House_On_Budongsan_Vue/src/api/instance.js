import axios from 'axios'

export default () => {
  return axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const authInstance = () => {
  const accessToken = sessionStorage.getItem('access-token')
  if (accessToken) {
    return axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL,
      headers: {
        'Content-Type': 'application/json',
        'access-token': accessToken
      }
    })
  } else {
    //session에 accessToken이 없으면
    throw new Error('NoToken')
  }
}
