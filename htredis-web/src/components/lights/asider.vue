<script setup>
import {ref,onMounted,defineEmits} from "vue"
import API from "../../js/api.js"
import {useMessage} from "naive-ui";
const message = useMessage();
const emit = defineEmits(['upData']);
const btns = ref([
	{id:1,icon:"../../../static/images/database.png",iconSelect:"../../../static/images/database-red.png",alt:"数据库",active:false,},

])
const selectShow = ref(false);
const addConcentShow=ref(false);
const formData = ref({});
const connectList=ref([]);
const selectInf=ref(null);
const btnsActiveItemFunc=(item)=>{
	btns.value.forEach((it,index)=>{
		if(it.id ===item.id){
			item.active=true;
		}else{
			item.active=false;
		}
	})

}




const removeActiveFunc=(item)=>{
	item.active=false;
}

const btnClick=(item)=>{
	btns.value.forEach((it,index)=>{
		if(it.id ===item.id){
			item.active=true;
		}else{
			item.active=false;
		}
	})
	selectShow.value = true;
}
const addConnectClick=()=>{
	addConcentShow.value = true;
}

const getConnectionList =()=>{
	API.getConnectionList().then((res)=>{
		if(res.code==="000000"){
			let da = res.data;
			da.forEach((item,index)=>{
				item.label = item.name + item.host;
			})
			connectList.value = da;

		}
	})
}


onMounted(()=>{
	getConnectionList();
})





const saveInf=()=>{
	if(formData.value.type===undefined||formData.value.type===null||formData.value.type===''){
		message.error("请先选择连接方式");
		return;
	}
	if(formData.value.name===undefined||formData.value.name===null||formData.value.name===''){
		message.error("请先填写连接名称");
		return;
	}
	if(formData.value.host===undefined||formData.value.host===null||formData.value.host===''){
			message.error("请先填写主机信息");
			return;
	}
	if(formData.value.port===undefined||formData.value.port===null||formData.value.port===''){
			message.error("请先填写端口信息");
			return;
	}
	API.addConnection(formData.value).then((res)=>{
		if(res.code==="000000"){
			message.success("添加成功");
			addConcentShow.value=false;
			getConnectionList();
		}else{
			message.error(res.msg);
		}
	}).catch((err)=>{
		message.error("接口异常");
	})



}
const confirmClick=()=>{
if(selectInf.value===null){
	message.error("请选择一个需要激活的连接");
	return;
}
let id = selectInf.value;
let info = connectList.value.find((value,index,arr)=>{
return value.id === id;
});
//向上传递参数。

emit("upData",info);
selectShow.value = false;
//localStorage.setItem("activeConnection",JSON.stringify(info));


}

</script>

<template>
<div class="wi100 flex_column">

	<div
		@mouseenter="btnsActiveItemFunc(item)"
		@click="btnClick(item)"
		class="courser"
		:class="item.active?'activeInf':''"
		v-for="(item,index) in btns">
		<img
			:src="item.active?item.iconSelect:item.icon"
			:alt="item.alt"
			style="width: 48px;height: 48px;">
	</div>

	<n-modal v-model:show="selectShow">
		<n-card style="width: 80%;background: #f2f2f2;" title="连接">
			<template #header-extra>
				<n-button-group><n-button type="warning" size="small" @click="addConnectClick">新建连接</n-button></n-button-group>
			</template>

			<n-card>
				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="11">
						<n-select placeholder="请选择一个连接" size="small" v-model:value="selectInf" :options="connectList" :label-field="'label'" :value-field="'id'" ></n-select>
					</n-grid-item>
					<n-grid-item>
						<n-button-group><n-button type="info" size="small" @click="confirmClick">确认</n-button></n-button-group>
					</n-grid-item>
				</n-grid>
			</n-card>

			<n-card v-if="addConcentShow">

				<n-divider dashed :title-placement="'center'">新建连接</n-divider>

				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-radio-group size="small" v-model:value="formData.type">
							<n-radio-button :value="'alone'">单机模式</n-radio-button>
							<n-radio-button :value="'cluster'">集群模式</n-radio-button>
							<n-radio-button :value="'sentinel'">哨兵模式</n-radio-button>
						</n-radio-group>
					</n-grid-item>
				</n-grid>

				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-input placeholder="请输入连接名称" size="small"  v-model:value="formData.name" >
							<template #prefix>
								<i class="iconfont eqadmin-gerenzhongxin-zhihui icon-color"></i>
							</template>
						</n-input>
					</n-grid-item>
				</n-grid>


				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-input size="small" placeholder="请输入redis主机信息，默认localhost,如果是集群，则格式应该为127.0.0.1:6379,192.168.1.1:6379..." v-model:value="formData.host">
							<template #prefix>
								<i class="iconfont eqadmin-xingzhuang icon-color"></i>
							</template>
						</n-input>
					</n-grid-item>
				</n-grid>

				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-input size="small"  placeholder="请输入redis主机端口默认6379" v-model:value="formData.port">
							<template #prefix>
								<i class="iconfont eqadmin-port icon-color"></i>
							</template>
						</n-input>
					</n-grid-item>
				</n-grid>


				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-input size="small"  placeholder="请输入redis用户名redis>6.0" v-model:value="formData.userName">
							<template #prefix>
								<i class="iconfont eqadmin-port icon-color"></i>
							</template>
						</n-input>
					</n-grid-item>
				</n-grid>


				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-input placeholder="请输入redis密码" size="small" v-model:value="formData.password">
							<template #prefix>
								<i class="iconfont eqadmin-icon-test35 icon-color"></i>
							</template>
						</n-input>
					</n-grid-item>
				</n-grid>

				<n-grid y-gap="8" x-gap="12" cols="12" style="margin: 1rem">
					<n-grid-item span="12">
						<n-button-group>
							<n-button type="primary" size="small" @click="saveInf">保存</n-button>
						</n-button-group>
					</n-grid-item>
				</n-grid>
			</n-card>


		</n-card>

	</n-modal>

</div>
</template>

<style scoped>

</style>
