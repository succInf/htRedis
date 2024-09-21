<script setup>
import { onMounted,ref,h ,watch,defineEmits } from 'vue'

const emit = defineEmits([]);
const props = defineProps({
	infs:{type:Array,default:null},
});
const infs = ref([]);
const imptInfs = ref([]);
const noImptInfs = ref([]);
const dbStrs = ref([]);

watch(() => props.infs, (newValue, oldValue) => {
	if (newValue !== oldValue&&newValue.length>0) {
		// 这里可以执行你想要的操作，比如更新 userGroup
		infs.value = newValue;
		infs.value.map((item)=>{
			if(item.level===1){
				imptInfs.value.push(item);
			}else{
				noImptInfs.value.push(item);
			}
			if(item.key.indexOf('db')>-1){
				dbStrs.value.push(item);
			}

		});
		//把级别为1的和0的区分开
	}
}, { immediate: true });

</script>

<template>
<div class="wi100">
	<n-split direction="horizontal" style="height: 100%" :max="0.6" :min="0.4" :resize-trigger-size="0">
		<template #1>
			<div style="margin-right: 1rem;">
				<n-descriptions label-placement="left" title="重要信息" bordered :column="2" label-align="left" size="small" >
					<n-descriptions-item v-for="(item,index) in imptInfs" :key="index"><template #label><span style="font-size: 10px;font-weight: bold;">{{item.name}}[{{item.key}}]</span></template><span style="font-size: 10px">{{item.value}}</span></n-descriptions-item>
				</n-descriptions>
			</div>
		</template>
		<template #2>
			<n-descriptions label-placement="left" title="其他信息" bordered :column="2" label-align="left" size="small">
				<n-descriptions-item v-for="(item,index) in noImptInfs" :key="index"><template #label><span style="font-size: 10px;">{{item.key}}</span></template><span style="font-size: 10px">{{item.value}}</span></n-descriptions-item>
			</n-descriptions>
		</template>
	</n-split>


</div>
</template>

<style scoped>

</style>
