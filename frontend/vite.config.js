import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
        resolvers: [ElementPlusResolver({
            icons: true // 关键：开启图标自动导入（导入所有图标）
            // 也可以指定只导入需要的图标，减小体积：icons: ["User", "Lock", "Home"]
        })],
    }),
    Components({
        resolvers: [ElementPlusResolver({
            icons: true // 和上面保持一致
        })],
    }),

],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
