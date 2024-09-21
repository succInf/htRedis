<script setup>

import {watch,ref,defineProps} from "vue";
import welcomeInf from "./welcome.vue"
import workerInf from "./worker.vue"
import addForm from "../form/addForm.vue"


import API from "@/js/api.js";
import {NIcon,useDialog,useMessage} from "naive-ui";
const dialog = useDialog();
const message = useMessage();
const addKeyShow=ref(false);
const activeConnectInf = ref({});
const infs = ref({});
const dbs = ref([]);
const props = defineProps({
	activeConnectInf:{type:Object,default:null},
});
const activeIndex = ref(-1);


const getDbs =()=>{
	API.getDbs(activeConnectInf.value.id).then((rr)=>{
		if(rr.code==="000000"){
			let data = [];
			Object.keys(rr.data).forEach((key)=>{
				data.push({key:key,value:rr.data[key],name:"db"+key,});
			});
			dbs.value = data;
		}
	}).catch((err)=>{
		message.error("打开连接失败，请检查主机、端口、账号、密码是否正确");
	})

}

const getRedisInfs=()=>{
	API.getInfs(activeConnectInf.value.id).then((res)=>{
		if(res.code==="000000"){
			let data = res.data;
			infs.value= data;
			console.log(infs.value)
		}else{
			message.error(res.msg);
		}

	}).catch((err)=>{
		message.error("打开连接失败，请检查主机、端口、账号、密码是否正确");
	})

}





const openConnection=()=>{
	API.openConnection(activeConnectInf.value.id).then((res)=>{
		if(res.code==="000000"){
			//代表第一次打开
			getDbs();
			getRedisInfs();
			//打开成功 获取默认的db数量。
		}else{
			if(res.code==="000009"){
				//连接已经被打开 获取db数量即可
				getDbs();
				getRedisInfs();
			}else{
				message.error(res.msg);
			}
		}
	}).catch((err)=>{
		//这里一定是打开连接失败。
		message.error("打开连接失败，请检查主机、端口、账号、密码是否正确");
	})
}



watch(() => props.activeConnectInf, (newValue, oldValue) => {
	if (newValue !== oldValue&&newValue.id!==undefined) {
		activeConnectInf.value = newValue;
		//获取db
		openConnection();
	}
}, { immediate: true });
const selectChange=(value)=>{
	activeIndex.value= value;//切换db
	activeConnectInf.value = JSON.parse(JSON.stringify(activeConnectInf.value));
	activeConnectInf.value.activeDb = value;
}
const selectClear=()=>{
	activeIndex.value = -1;

}
const addKeyClick=()=>{
	//先判断db数量是否大于1
	if(dbs.value.length>1){
		if(activeConnectInf.value.activeDb===undefined||activeConnectInf.value.activeDb===null||activeConnectInf.value.activeDb===''){
			message.error("请先选择一个db");
			return;
		}
	}
	addKeyShow.value = true;
}
const addSuccess=()=>{
	addKeyShow.value = false;
	//更新数据。
	activeConnectInf.value.timestamps = new Date().getTime();
	selectChange(activeConnectInf.value.activeDb);
}

</script>

<template>
<div class="wi100 ">
<n-modal v-model:show="addKeyShow">
<n-card style="width: 80%;">
<addForm :redisInf="activeConnectInf" @success="addSuccess"></addForm>
</n-card>
</n-modal>


<div>
	<n-grid x-gap="12" y-gap="8" cols="1">
    <n-grid-item>
			<n-card v-if="activeConnectInf.name!==undefined">
			<n-space >
				<n-breadcrumb>
					<n-breadcrumb-item>
						<i class="iconfont eqadmin-folder-empty "></i>{{activeConnectInf.name}}
					</n-breadcrumb-item>
					<n-breadcrumb-item>
						<i class="iconfont eqadmin-a-zhujifuwuqi"></i>{{activeConnectInf.host}}
					</n-breadcrumb-item>
					<n-breadcrumb-item>
						<i class="iconfont eqadmin-bianji2"></i>db（{{dbs.length}}）
					</n-breadcrumb-item>
					<n-breadcrumb-item>
						<a href="javascript:;" @click="addKeyClick"><span>	<i class="iconfont eqadmin-jia4"></i>新建key</span></a>
					</n-breadcrumb-item>
				</n-breadcrumb>
				<n-select style="width: 300px;" v-model:value="activeConnectInf.activeDb" placeholder="请选择一个db" :options="dbs" clearable :label-field="'name'" :value-field="'key'" @change="selectChange" @clear="selectClear"></n-select>
			</n-space>



			</n-card>
			<n-card v-else>
				<n-result status="418" title="暂无可用连接" description="一切尽在不言中">
				</n-result>
			</n-card>
		</n-grid-item>
	</n-grid>

	<!--工作台-->
	<n-grid x-gap="12" y-gap="8" cols="1">
   <n-grid-item>
		 <n-card>
      <welcomeInf v-if="activeIndex===-1" :infs="infs"></welcomeInf>
			<workerInf v-else  :activeConnectInf="activeConnectInf"></workerInf>
		 </n-card>
	 </n-grid-item>
	</n-grid>
</div>


</div>


</template>

<style >

</style>
