import {createApp} from 'vue'
import App from './App.vue'
import router from "./router/index.js";
import naive from 'naive-ui'
import datav from "./js/datav.min.vue.esm.js"
const app = createApp(App);
app.use(router)
app.use(datav)
app.use(naive)
//自动注入组件
app.mount('#app')
