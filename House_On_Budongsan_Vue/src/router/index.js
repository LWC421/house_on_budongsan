import { createRouter, createWebHistory } from 'vue-router'
import { HomeView, HouseView, MyPageView, JoinView, LoginView } from '@/views'
import { MyPageLikeView, MyPageMemberView } from '@/views/mypage'
import { BoardView, NewsView, BoardWrite, BoardDetail } from '@/views/board'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/board',
      name: 'board',
      component: BoardView
    },
    {
      path: '/write',
      name: 'boardWrite',
      component: BoardWrite
    },
    {
      path: '/detail/:boardCode/:memberCode',
      name: 'boardDetail',
      component: BoardDetail
    },
    {
      path: '/news',
      name: 'news',
      component: NewsView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/join',
      name: 'join',
      component: JoinView
    },
    {
      path: '/house',
      name: 'house',
      component: HouseView
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView,
      redirect: '/mypage/like',
      children: [
        {
          path: 'like',
          name: 'mypage-like',
          component: MyPageLikeView
        },
        {
          path: 'member',
          name: 'mypage-member',
          component: MyPageMemberView
        }
      ]
    }
  ]
})

export default router
