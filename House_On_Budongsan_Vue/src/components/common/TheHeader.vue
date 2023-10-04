<template>
  <header>
    <div><the-logo></the-logo></div>
    <div v-if="!isLogin">
      <router-link
        :to="{ name: 'login' }"
        class="hover"
        style="margin-right: 20px"
        >로그인</router-link
      >
      <router-link
        :to="{ name: 'join' }"
        class="hover"
        >회원가입</router-link
      >
    </div>
    <div v-else style="margin-right:30px">
      <v-row justify="center">
      <v-menu
        min-width="200px"
        rounded
      >
        <template v-slot:activator="{ props }">
          <v-btn
            icon
            v-bind="props"
          >
            <v-avatar
              color="brown"
              size="large"
            >
              <span class="text-h5">{{ memberInfo.nickname.substr(0, 1) }}</span>
            </v-avatar>
          </v-btn>
        </template>
        <v-card>
          <v-card-text>
            <div class="mx-auto text-center">
              <v-avatar
                color="brown"
              >
                <span class="text-h5">{{ memberInfo.nickname.substr(0, 1) }}</span>
              </v-avatar>
              <h3>{{ memberInfo.nickname }}</h3>
              <p class="text-caption mt-1">
                {{ memberInfo.email }}
              </p>
              <v-divider class="my-3"></v-divider>
              <v-btn
                rounded
                variant="text"
                @click="moveToMypage"
              >
                마이페이지
              </v-btn>
              <v-divider class="my-3"></v-divider>
              <v-btn
                rounded
                variant="text"
                @click="logout"
              >
                로그아웃
              </v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-menu>
    </v-row>
    </div>
  </header>
</template>

<script setup>
import TheLogo from './TheLogo.vue'
import { useMemberStore } from '@/stores'
import { storeToRefs } from 'pinia'
import router from "../../router";

// pinia에서 store 가져옴
const memberStore = useMemberStore();
const { isLogin, memberInfo } = storeToRefs(memberStore);
const logout = memberStore.memberLogout;

function moveToMypage() {
  router.push({name:'mypage'})
}
</script>

<style scoped>
header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;

  width: 100%;
  height: 80px;

  padding-left: 20px;
  padding-right: 20px;

  background-color: rgb(245, 245, 245);
}
.hover:hover {
  cursor: pointer;
  color: rgb(124, 124, 124);
}
</style>
