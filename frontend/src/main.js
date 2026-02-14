import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router"
import { createPinia } from "pinia"
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios"

axios.defaults.baseURL = "http://localhost:8080"

createApp(App)
    .use(createPinia())
    .use(router)
    .use(ElementPlus)
    .mount('#app')