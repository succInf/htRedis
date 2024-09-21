<script setup>
import {defineEmits, ref, watch} from "vue"
import {useMessage} from "naive-ui"
import API from "../../js/api.js"
const props = defineProps({
	formInf:{type:Object,default:null},
});
const formInf = ref({});
const message = useMessage();
const emit = defineEmits(['success']);

watch(() => props.formInf, (newValue, oldValue) => {
	if (newValue !== oldValue) {
		// 这里可以执行你想要的操作，比如更新 userGroup
		formInf.value = newValue;
		formInf.value.expire = -1;
	}
}, { immediate: true });


const save=()=>{
	if(formInf.value.key===undefined||formInf.value.key===null||formInf.value.key===''){
		message.error("请先输入key");
		return;
	}

	if(formInf.value.field===undefined||formInf.value.field===null||formInf.value.field===''){
		message.error("请先输入field");
		return;
	}

	if(formInf.value.value===undefined||formInf.value.value===null||formInf.value.value===''){
		message.error("请先输入value");
		return;
	}
	API.addOrUpdateHashValue({
		key:formInf.value.key,
		field:formInf.value.field,
		value:formInf.value.value,
		id:formInf.value.id,
		db:formInf.value.db,
		expire:formInf.value.expire
	}).then((res)=>{
				if(res.code==="000000"){
					message.success("保存成功");
					formInf.value={};
					emit('success');
				}else{
					message.error("保存失败");
				}
			}).catch((err)=>{
				message.error("系统异常");
			})

	// API.addOrUpateStrValue(formInf.value.id,formInf.value.db,formInf.value.key,formInf.value.value,formInf.value.expire).then((res)=>{
	// 	if(res.code==="000000"){
	// 		message.success("保存成功");
	// 		formInf.value={};
	// 		emit('success');
	// 	}else{
	// 		message.error("保存失败");
	// 	}
	// }).catch((err)=>{
	// 	message.error("系统异常");
	// })

}
</script>

<template>
	<div>
		<n-form-item label="key">
			<n-input placeholder="请输入key" v-model:value="formInf.key"></n-input>
		</n-form-item>

		<n-form-item label="field">
			<n-input placeholder="请输入field" v-model:value="formInf.field"></n-input>
		</n-form-item>

		<n-form-item label="value">
			<n-input placeholder="请输入value" type="textarea" v-model:value="formInf.value"></n-input>
		</n-form-item>


		<n-form-item label="ttl">
			<n-input-number style="width: 100%;" placeholder="请输入过期时间，默认为-1 不过期"  v-model:value="formInf.expire"></n-input-number>
		</n-form-item>


		<n-form-item>
			<n-button @click="save" type="warning" size="small">保存</n-button>
		</n-form-item>
	</div>
</template>

<style scoped>

</style>
