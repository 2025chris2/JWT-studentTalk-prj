<script setup>
import {get,logout} from "@/net/index.js";
import router from "@/router/index.js";
import {useStore} from "@/store/index.js";
import {reactive, ref} from "vue";
import {
  Back,
  Bell, ChatDotSquare, Collection, DataLine,
  Document, Files, Location, Lock, Message, Monitor,
  Notification, Operation, Position, School,
  Search, Umbrella, User
} from '@element-plus/icons-vue';

const store = useStore();
const loading = ref(true);
const searchInput = reactive({
  type: '1',
  text: ''
})

get("api/user/info",(data)=>{
  store.user = data;
  loading.value = false;
})

function userLogout(){
  logout(()=>{
    router.push('/')
  })
}
</script>

<template>
  <div>
    <div class="main-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
      <el-container style="height: 100%">
        <!-- Header -->
        <el-header class="main-content-header">
          <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>

          <div style="flex: 1; padding: 20px; text-align: center;">
            <el-input
                v-model="searchInput.text"
                style="width: 100%; max-width: 500px"
                placeholder="搜索论坛相关内容..."
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
              <template #append>
                <el-select v-model="searchInput.type" style="width: 110px">
                  <el-option :key="1" value="1" label="帖子广场"/>
                  <el-option :key="2" value="2" label="校园活动"/>
                  <el-option :key="3" value="3" label="表白墙"/>
                  <el-option :key="4" value="4" label="教务通知"/>
                </el-select>
              </template>
            </el-input>
          </div>

          <div class="user-info">
            <div class="profile">
              <div>{{ store.user.username }}</div>
              <div>{{ store.user.email }}</div>
            </div>
            <el-dropdown>
              <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
              <template #dropdown>
                <el-dropdown-item>
                  <el-icon><Operation/></el-icon>
                  个人设置
                </el-dropdown-item>
                <el-dropdown-item>
                  <el-icon><Message/></el-icon>
                  消息列表
                </el-dropdown-item>
                <el-dropdown-item divided @click="userLogout">
                  <el-icon><Back/></el-icon>
                  退出登录
                </el-dropdown-item>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-container>
          <!-- Aside -->
          <el-aside width="230px">
            <el-scrollbar style="height: calc(100vh - 55px)">
              <el-menu
                  style="min-height: 100vh"
                  :default-active="$route.path"
                  router>
                <!-- 校园论坛 -->
                <el-sub-menu index="1">
                  <template #title>
                    <el-icon><Location /></el-icon>
                    <span>校园论坛</span>
                  </template>
                  <el-menu-item index="">
                    <el-icon><ChatDotSquare /></el-icon>
                    <span>帖子广场</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Bell /></el-icon>
                    <span>失物招领</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Notification /></el-icon>
                    <span>校园活动</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Umbrella /></el-icon>
                    <span>表白墙</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><School /></el-icon>
                    <span>海文考研</span>
                    <el-tag size="small" style="margin-left: 5px">合作</el-tag>
                  </el-menu-item>
                </el-sub-menu>

                <!-- 探索与发现 -->
                <el-sub-menu index="2">
                  <template #title>
                    <el-icon><Position /></el-icon>
                    <span>探索与发现</span>
                  </template>
                  <el-menu-item index="">
                    <el-icon><Document /></el-icon>
                    <span>成绩查询</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Files /></el-icon>
                    <span>班级课程表</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Monitor /></el-icon>
                    <span>教务通知</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Collection /></el-icon>
                    <span>在线图书馆</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><DataLine /></el-icon>
                    <span>预约教室</span>
                  </el-menu-item>
                </el-sub-menu>

                <!-- 个人设置 -->
                <el-sub-menu index="3">
                  <template #title>
                    <el-icon><Operation /></el-icon>
                    <span>个人设置</span>
                  </template>
                  <el-menu-item index="/index/user-setting">
                    <el-icon><User /></el-icon>
                    <span>个人信息设置</span>
                  </el-menu-item>
                  <el-menu-item index="">
                    <el-icon><Lock /></el-icon>
                    <span>账号安全设置</span>
                  </el-menu-item>
                </el-sub-menu>

              </el-menu>
            </el-scrollbar>
          </el-aside>

          <el-main class="main-content-page">
            <el-scrollbar style="height: calc(100vh - 55px)">
              <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in">
                  <component :is="Component" style="height: 100%"/>
                </transition>
              </router-view>
            </el-scrollbar>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>

<style lang="less" scoped>
.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}

.dark .main-content-page{
  background-color: #242628;
}

.main-content {
  height: 100vh;
  width: 100vw;

  :deep(.el-input-group__append) {
    background-color: #fff;
    padding: 0;
  }

  :deep(.el-input-group__append .el-select) {
    margin: 0 -1px;
  }

  :deep(.el-input-group__append .el-select .el-input__wrapper) {
    background-color: transparent;
    box-shadow: none !important;
  }

  :deep(.el-input-group__append .el-select .el-input__inner) {
    color: #606266;
    text-align: center;
  }
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  box-sizing: border-box;
  display: flex;
  align-items: center;

  .logo {
    height: 32px;
    padding: 10px;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .el-avatar:hover{
      cursor: pointer;
    }

    .profile {
      text-align: right;
      margin-right: 20px;

      div:first-child {
        font-size: 18px;
        font-weight: bold;
        line-height: 20px;
      }

      div:last-child {
        color: grey;
        font-size: 10px;
      }
    }
  }
}
</style>