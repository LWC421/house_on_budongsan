<template>
  <div class="container">
    <div
      :class="{ left: true, leftOn: onOff }"
      @mouseenter="enterLeft"
      @mouseleave="leaveLeft"
    >
      <router-link
        :to="{ name: 'mypage-like' }"
        class="navItem"
        @mouseenter="likeHover = true"
        @mouseleave="likeHover = false"
      >
        <v-icon
          icon="mdi-heart"
          size="x-large"
          :color="likeHover ? 'rgb(210, 0, 0)': 'red'"
        />
        <template v-if="onOff">좋아요</template>
      </router-link>
      <router-link
        :to="{ name: 'mypage-member' }"
        class="navItem"
      >
        <v-icon
          icon="mdi-account-circle-outline"
          size="x-large"
        />
        <template v-if="onOff">나의 정보</template>
      </router-link>
    </div>
    <div class="right">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const onOff = ref(false)
const likeHover = ref(false);

//왼쪽 영역에 마우스 커서가 들어오면
const enterLeft = () => {
  onOff.value = true
}

const leaveLeft = () => {
  onOff.value = false
}
</script>

<style scoped>
.container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: row;
}

.left {
  display: flex;
  flex-direction: column;
  justify-content: start;
  gap: 10px;

  width: 50px;
  transition: all 0.1s ease-out;

  padding-top: 20px;
  padding-left: 7px;

  border-right: 1px solid rgb(214, 214, 214);
}
.leftOn {
  width: 200px;
  transition: all 0.2s ease-out;
}
.navItem {
  display: flex;
  flex-direction: row;
  align-items: center;

  gap: 10px;

  overflow-x: hidden;
  white-space: nowrap;

  font-weight: bold;
}
.navItem:hover {
  cursor: pointer;
  color: rgb(124, 124, 124);
}
.right {
  width: 100%;
  height: 100%;
  padding: 20px;
}
</style>
