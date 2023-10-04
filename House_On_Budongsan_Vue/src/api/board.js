import getInstance from './instance.js'

const instance = getInstance()

function writeArticle(article) {
  return instance.post(`/board`, JSON.stringify(article))
}

function modifyArticle(article) {
  return instance.put(`/board`, JSON.stringify(article))
}

function getArticle(article) {
  return instance.post(`/board/code`, JSON.stringify(article))
}

function getPageArticle(pageNo) {
  return instance.get(`/board/page/${pageNo}`)
}

function removeArticle(boardCode) {
  return instance.delete(`/board/${boardCode}`)
}

function getCount() {
  return instance.get(`/board/count`)
}

function boardRecentFive() {
  return instance.get(`/board/recentfive`)
}

export {
  writeArticle,
  getPageArticle,
  modifyArticle,
  getArticle,
  removeArticle,
  getCount,
  boardRecentFive
}
