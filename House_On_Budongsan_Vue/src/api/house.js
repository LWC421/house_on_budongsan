import axios from 'axios'
import getInstance, { authInstance } from './instance.js'

const instance = getInstance()

const getLocationList = (keyword) => {
  return instance.get(`/house/search/location/${keyword}`)
}

const getBuildingList = (keyword) => {
  return instance.get(`/house/search/building/${keyword}`)
}

const getDealList = (searchCondition) => {
  const accessToken = sessionStorage.getItem('access-token')

  if (accessToken) {
    //만약 로그인이 되어 있으면 -> 토큰을 넣어서 보내자
    return authInstance().get(`/house/deal`, {
      params: searchCondition
    })
  } else {
    //로그인이 안 되어 있으면
    return instance.get(`/house/deal`, {
      params: searchCondition
    })
  }
}

const getAllDealListByHouseInfoCode = (houseInfoCode) => {
  return instance.get(`/house/deal/${houseInfoCode}`)
}

const getLikeList = (memberCode) => {
  const inst = authInstance()
  return inst.get(`/house/like/${memberCode}`)
}

const getLikeListDetail = (memberCode) => {
  const inst = authInstance()
  return inst.get(`/house/like/detail/${memberCode}`)
}

const addLike = ({ memberCode, houseInfoCode }) => {
  const inst = authInstance()
  return inst.post(
    `/house/like/add/${memberCode}`,
    JSON.stringify({ houseInfoCode })
  )
}

const removeLike = ({ memberCode, houseInfoCode }) => {
  const inst = authInstance()
  return inst.post(
    `/house/like/remove/${memberCode}`,
    JSON.stringify({ houseInfoCode })
  )
}

const getDealListByLatLng = (params) => {
  return instance.get(`/house/dealbylatlng`, {
    params: {
      ...params
    }
  })
}

const getRank = (searchBy) => {
  return instance.get(`/house/rank`, {
    params: {
      searchBy
    }
  })
}

const getImageList = (limit) => {
  return axios.get(`https://picsum.photos/v2/list?page=2&limit=${limit}`)
}

export {
  getLocationList,
  getBuildingList,
  getDealList,
  getAllDealListByHouseInfoCode,
  getLikeList,
  getLikeListDetail,
  addLike,
  removeLike,
  getDealListByLatLng,
  getImageList,
  getRank
}
