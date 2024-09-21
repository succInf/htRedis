<script setup>
import {ref} from "vue"
import asiderInfo from "../../../components/lights/asider.vue"
import contentInf from "../../../components/lights/content.vue"
const activeConnectInf = ref({});
const upData=(info)=>{
	activeConnectInf.value = info;
}


</script>

<template>
<div class="light-content">
	<n-layout has-sider  content-class="height100vh" >
		<n-layout-sider content-style="padding-top:1rem;" :collapse-mode="'width'" :width="'55px'" class="backgroundF2 height100vh border_right">
			<div class="flex_column">
       <asiderInfo @upData="upData"></asiderInfo>
			</div>
		</n-layout-sider>

		<n-layout content-class="height100vh">
			<n-layout-header class="height5vh border_bottom backgroundF2">
				<div class="ht-title">htRedis</div>
			</n-layout-header>



			<n-layout-content content-style="padding: 24px;" class="height90vh backgroundF2">
       <contentInf :activeConnectInf="activeConnectInf"></contentInf>
			</n-layout-content>



			<n-layout-footer class="height5vh border_top backgroundF2 footer ">周口智汇企业咨询有限公司/专业软件著作权代理申请/商标申请/软件开发定制/详情添加微信worker_680进行咨询</n-layout-footer>
		</n-layout>
	</n-layout>
</div>
</template>

<style >
@import url("../../../../static/css/light.css");
</style>
