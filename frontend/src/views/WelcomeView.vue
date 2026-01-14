<script setup>
import tt from '@/assets/img/1.jpg'
</script>

<template>
  <div style="width:100vw;height:100vh;overflow:hidden;display:flex">
    <div style="flex: 1;background-color: black" >
      <el-image :src="tt" fit="cover" style="width:100%;height:100%;"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 100px;font-weight: bold">欢迎来到牢关的网页</div>
      <div style="margin-top: 10px">私密🈲🈲五杀截图！</div>
      <div style="margin-top: 10px">禁止偷截图</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{Component}">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.welcome-title{
  position:absolute;
  bottom:50px;
  left:40px;
  color:white;
  text-shadow: 0 0 10px black;
  font-size:50px;
}
.right-card{
  width: 600px;
  background-color: var(--el-bg-color);
  z-index: 1;
}
</style>