
import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios";

axios.defaults.baseURL = "http://localhost:8080"
// 挂载路由到Vue实例
createApp(App)
    .use(router) // 必须加这行，路由才会生效
    .use(ElementPlus)
    .mount('#app')
