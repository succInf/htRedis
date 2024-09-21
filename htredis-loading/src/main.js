import { createApp } from 'vue'
import App from './App.vue'
import Particles from "particles.vue3"; // 引入
const app = createApp(App);

app.use(Particles);

app.mount('#app')
