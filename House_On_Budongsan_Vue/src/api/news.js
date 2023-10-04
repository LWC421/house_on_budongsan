import getInstance from './instance.js'

const instance = getInstance()

function getPageArticle(pageNo) {
  return instance.get(`/board/news/getPageNews/${pageNo}`)
}

function getCount() {
  return instance.get(`/board/news/getCount`)
}

function newsRecentFive() {
  return instance.get(`/board/news/recentfive`)
}

export { getPageArticle, getCount, newsRecentFive }
