<script setup>
import {watch,ref,defineProps} from "vue";
import {NIcon,useDialog,useMessage} from "naive-ui";
import addForm from "../form/addForm.vue"


import API from "../../js/api.js"
const message = useMessage();
const activeConnectInf=ref({});

const cursor = ref("0");
const id = ref(null);
const db = ref(null);
const size = ref(100);
const keySearchValue = ref('');
const keysPage=ref({});
const finished = ref(0);
const redisKeyInfos = ref([]);
const activeKeyInf=ref({});
const valueData = ref({});
const activeKeyIndex = ref(-1);
const addFormShow = ref(false);
const addFormData = ref({});
const props = defineProps({
	activeConnectInf:{type:Object,default:null}
});

//获取db的所有keys
const getKeysByPage = ()=>{
	API.getKeysByPage(id.value,cursor.value,db.value,size.value,keySearchValue.value).then((res)=>{
		if(res.code==="000000"){
			keysPage.value = res.data;
			cursor.value = res.data.cursor;
			finished.value = res.data.finished;
			redisKeyInfos.value = res.data.redisKeyInfos;
			if(redisKeyInfos.value.length>0){
				activeKeyInf.value = redisKeyInfos.value[0];
				activeKeyIndex.value=0;
				getKeyInf(activeKeyInf.value);
			}

		}else{
			message.error("获取key失败");
		}
	})
}
const moreClick=()=>{
	if(finished.value===1){
		message.error("已经加载完毕");
	}else{
		getKeysByPage();
	}
}
const getKeyInf=(item)=>{
	//
	activeKeyInf.value = item;
	API.getValueOb(id.value,db.value,item.type,item.key).then((res)=>{
		if(res.code==="000000"){
			valueData.value = res.data;
		}
	})
}



watch(() => props.activeConnectInf, (newValue, oldValue) => {
	if (newValue !== oldValue&&newValue.id!==undefined) {
		activeConnectInf.value = newValue;
		id.value = newValue.id;
		db.value = newValue.activeDb;
		getKeysByPage();
	}
}, { immediate: true,deep:true });

const clickKey=(item,index)=>{
	activeKeyIndex.value = index;
	getKeyInf(item);
	//获取key的信息。
}

const updateStrValue=()=>{
	API.addOrUpateStrValue(id.value,db.value,valueData.value.key,valueData.value.value,valueData.value.expire).then((res)=>{
		if(res.code==="000000"){
			message.success("更新成功");
			getKeyInf(activeKeyInf.value);
		}else{
			message.error("更新失败");
		}
	})
}

const del=(item)=>{
	API.removeStrAndValue(id.value,db.value,item.key,item.type).then((res)=>{
		if(res.code==="000000"){
			//刷新key
			message.success("删除成功");
			refreshData();
		}else{
			message.error("删除失败");
		}
	})
}

const refreshData=()=>{
	getKeysByPage();
}
const delKInf=(item,index)=>{
	del(item);
}
const editHash=(item,info,value,expire)=>{
	addFormData.value = {
		type:"hash",
		key:info.key,
		field:item,
		value:value,
		expire:expire,
		id:id.value,
		activeDb:	db.value
	}
	addFormShow.value = true;



}

const hashDel=(item,info)=>{
	API.removeHashField(id.value,db.value,info.key,item).then((res)=>{
		if(res.code==="000000"){
			message.success("删除成功");
			refreshData();
		}
	})

}
const updateSuccess=()=>{
	addFormShow.value = false;
	refreshData();
}

</script>

<template>
<div class="wi100 ">
	<n-modal v-model:show="addFormShow">
		<n-card style="width: 80%">
      <addForm :redisInf="addFormData" @success="updateSuccess"></addForm>
		</n-card>

	</n-modal>



	<n-split direction="horizontal" style="height: 100%" :size="0.2" :resize-trigger-size="0">
		<template #1>
				<n-grid x-gap="12"  y-gap="8" cols="1">
					<n-grid-item  v-for="(item,index) in keysPage.redisKeyInfos"  :key="index" class="keyGrid" :class="index===activeKeyIndex?'activeKey':''">
						<a  href="javascript:;" @click="clickKey(item,index)">
							<span style="width: 100%;cursor: pointer;">
								<n-ellipsis style="max-width: 100%;"><i class="iconfont eqadmin-lianjie"></i>&nbsp;{{item.key}}【{{item.type}}】</n-ellipsis>
							</span>
						</a>

						<a href="javascript:;" class="del-a" @click="delKInf(item,index)"><i class="iconfont eqadmin-31shanchu" ></i>删除</a>
					</n-grid-item>
				</n-grid>

			  <n-grid x-gap="12" y-gap="8" cols="1" style="margin-top: 1rem">
					<n-grid-item>
						<span class="more-span"><a href="javascript:;" @click="moreClick">{{finished===1?'已经加载完毕':'...加载更多...'}}</a></span>
					</n-grid-item>
				</n-grid>

		</template>
		<template #2>
			<div v-if="activeKeyInf.type==='string'" style="width: 92%;position: relative;margin: auto;">
				<n-card  style="width: 100%;" :bordered="false" :title="activeKeyInf.key+'值域'">
					<template #header-extra>TTL：<n-input-number v-model:value="valueData.expire"></n-input-number></template>
					<n-input type="textarea" v-model:value="valueData.value" :rows="20"></n-input>
					<n-button style="position: absolute;right: 1.8rem;bottom: 1.5rem;"  size="tiny" @click="updateStrValue"><i class="iconfont eqadmin-a-appround6"></i>更新</n-button>
				</n-card>
			</div>
			<div v-if="activeKeyInf.type==='hash'">

					<n-card :title="activeKeyInf.key">
						<template #header-extra>TTL：<n-input-number v-model:value="valueData.expire"></n-input-number></template>
						<n-descriptions label-placement="left"  bordered column="1">
							<n-descriptions-item v-for="(it,index) in valueData.items">
								<template #label>
									{{Object.keys(it).length>0?Object.keys(it)[0]:""}}
								</template>
								{{Object.keys(it).length>0?it[Object.keys(it)[0]]:""}}

								<span style="position: absolute;right: 3.5rem" class="hashspan">
									<span @click="editHash(Object.keys(it)[0],valueData,it[Object.keys(it)[0]],valueData.expire)"><a href="javascript:;"><i class="iconfont eqadmin-bianji6" ></i>编辑</a></span>
									<n-divider :vertical="true"></n-divider>
									<span @click="hashDel(Object.keys(it)[0],valueData)"><a href="javascript:;"><i class="iconfont eqadmin-31shanchu" ></i>删除</a></span>
								</span>
							</n-descriptions-item>

						</n-descriptions>
					</n-card>
			</div>



		</template>
	</n-split>
</div>
</template>

<style >
.keyGrid a{color: #666666;}
.keyGrid a:hover{color: #000000;}
.activeKey{background: burlywood;}
.activeKey a{color: #000000 ;}
.more-span a{color: #666666;text-decoration: none;}
.del-a{text-decoration: none;}
.hashspan a{text-decoration: none;color: #666666;}
.hashspan a:hover{color: burlywood;}
</style>
