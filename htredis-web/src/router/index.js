import { createRouter, createWebHistory } from 'vue-router'
import lightsIndex from "../views/thems/lights/index.vue"
const routerHistory = createWebHistory("/")

const routes = [
	{
		path:"/index",
		name:"htredis",
		component:lightsIndex,
	}
];


const router = createRouter({history: routerHistory, routes: routes})
export default router

