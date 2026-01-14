<script setup>
import { useDark, useToggle } from "@vueuse/core";

// ✅ 正确：只调用一次 useDark，并用变量接收
const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

// ✅ 正确：使用 useToggle 创建切换函数
const toggleDark = useToggle(isDark)

// 🎯 如果你想跟随系统主题，可以添加这个（可选）
// usePreferredDark() // 监听系统主题变化
</script>

<template>
  <div>
    <!-- 添加一个切换按钮 -->
    <button @click="toggleDark()" style="position: fixed; top: 20px; right: 20px; z-index: 999">
      {{ isDark ? '☀️ 浅色模式' : '🌙 深色模式' }}
    </button>
    <router-view/>
  </div>
</template>