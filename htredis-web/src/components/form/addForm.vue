<script setup>
import {defineEmits, ref, watch} from "vue"
import strForm from "./strForm.vue"
import hashForm from "./hashForm.vue"
import listForm from "./listForm.vue"
import setForm from "./setForm.vue"
import zsetForm from "./zsetForm.vue"


const formInf = ref({
	type:"",
})
const emit = defineEmits(['success']);
const props = defineProps({
	redisInf:{type:Object,default:null},
});
const redisInf = ref({});
watch(() => props.redisInf, (newValue, oldValue) => {
	if (newValue !== oldValue) {
		console.log(newValue)
		// 这里可以执行你想要的操作，比如更新 userGroup
		redisInf.value = newValue;
		formInf.value.id = newValue.id;
		formInf.value.db = newValue.activeDb;
		if(newValue.type){
			formInf.value.type = newValue.type;
		}
		if(newValue.key){
			formInf.value.key = newValue.key;
		}
		if(newValue.field){
			formInf.value.field = newValue.field;
		}
		if(newValue.value){
			formInf.value.value = newValue.value;
		}
		if(newValue.expire){
			formInf.value.expire = newValue.expire;
		}

	}
}, { immediate: true ,deep:true});



const clear=()=>{
	formInf.value.type = "";
}
const success=()=>{
	formInf.value.type = "";
	emit("success");
}
</script>

<template>
<div class="addForm">
<n-form :label-width="'120px'" :label-placement="'left'" :model="formInf">
   <n-form-item label="新增类型">
		 <n-radio-group v-model:value="formInf.type">
			 <n-radio-button :value="'string'">字符串[string]</n-radio-button>
			 <n-radio-button :value="'hash'">哈希[hash]</n-radio-button>
			 <n-radio-button :value="'list'">集合[list]</n-radio-button>
			 <n-radio-button :value="'set'">集合[set]</n-radio-button>
			 <n-radio-button :value="'zset'">集合[zset]</n-radio-button>
		 </n-radio-group>
	 </n-form-item>

	<strForm v-if="formInf.type==='string'" :formInf="formInf" @clear="clear" @success="success"></strForm>
	<hash-form v-if="formInf.type==='hash'" :formInf="formInf" @clear="clear" @success="success"></hash-form>
	<list-form v-if="formInf.type==='list'" :formInf="formInf" @clear="clear" @success="success"></list-form>
	<set-form v-if="formInf.type==='set'" :formInf="formInf" @clear="clear" @success="success"></set-form>
	<zset-form v-if="formInf.type==='zset'" :formInf="formInf" @clear="clear" @success="success"></zset-form>
</n-form>
</div>
</template>

<style >
.addForm{width: 98%}

</style>
