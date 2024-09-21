<script setup>
import API from "../js/api.js"
import {ref,h,onMounted,} from "vue";
import rightContent from "../components/layui/rightContent.vue"
import {NIcon,useDialog,useMessage} from "naive-ui";
import {
	FlashOutline,DesktopSharp,AddCircleSharp,
	Server,ArrowDownCircle,CaretDownSharp,CaretUp,
	OpenOutline
} from '@vicons/ionicons5'
const dialog = useDialog();
const message = useMessage();
//当前鼠标定位的redis对象
const redisDataInf = ref(null);
const redisDataOo = ref({})
const connectList=ref([])
const leftMenuActiveCurrent=ref(0);
const dbActiveCurrent=ref(-1);

const data=ref({
	welcomeFlag:true,//欢迎页
	editRedisConnectionFlag:false,//编辑页
	addRedisConnectionFlag:false,//新增页
	workerFlag:false,//工作台
})

const renderIcon=(Component)=>{
	return () => {
		return h(NIcon, null, {
			default: () => h(Component)
		})
	}
}

const options=ref([
	{label:"新建连接",key:"addConnection",icon:renderIcon(AddCircleSharp)},
]);
const olOptions=ref([
	{label:"打开连接",key:"openConnection",icon:renderIcon(AddCircleSharp)},
	{label:"关闭连接",key:"addConnection",icon:renderIcon(AddCircleSharp)},
	{label:"删除连接",key:"delConnection",icon:renderIcon(AddCircleSharp)},
	{label:"编辑信息",key:"editInf",icon:renderIcon(OpenOutline)},
	{label:"打开工作台",key:"openWorker",icon:renderIcon(AddCircleSharp)},
])

const renderOptions=(options)=>{
	return options;
}
const switchAl=(item,index)=>{
	leftMenuActiveCurrent.value = index;
	//切换之后 开始打开连接。
}
const switchExtend=(item)=>{
	item.expand = !item.expand;
}

const dbclick=(item,index,redisInf)=>{
	//切换db  并切换到worker工作台 item.value  代表选择是哪个库
	let data = {
		activeDbIndex:parseInt(item.key),
		redisDataInf:redisInf,
	}
	dbActiveCurrent.value = index;
	redisDataOo.value = data;
	showFlag("workerFlag",true);
}

const showFlag = (key,value)=>{
	//先把所有的值设置为false.
	let obj = data.value;
	Object.keys(obj).forEach(k => {
		if(k===key){
			obj[k]=value;
		}else{
			obj[k]=false;
		}
	});
}

//新建连接 打开路由。
const selectOptions=()=>{
	showFlag('addRedisConnectionFlag',true);
}

const handleMouseOver=(item)=>{
	redisDataInf.value = item;

}

const openConnect=()=>{
	API.openConnection(redisDataInf.value.id).then((res)=>{
		if(res.code==="000000"){
			//打开成功 获取默认的db数量。
			getDbs();
			getRedisInfs();
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

const getDbs =()=>{

	API.getDbs(redisDataInf.value.id).then((rr)=>{
		if(rr.code==="000000"){
			let data = [];

		Object.keys(rr.data).forEach((key)=>{
			data.push({key:key,value:rr.data[key]});
		});
			console.log(data)
			let rd = connectList.value.find((item)=>item.id===redisDataInf.value.id);
			rd.dbs = data;
			rd.connectionFlag=true;
			rd.expand=true;
		}
	}).catch((err)=>{
		message.error("打开连接失败，请检查主机、端口、账号、密码是否正确");
	})

}

const getRedisInfs=()=>{

	API.getInfs(redisDataInf.value.id).then((res)=>{
		if(res.code==="000000"){
			let data = res.data;
			redisDataOo.value.infs= data;

		}else{
			message.error(res.msg);
		}

	}).catch((err)=>{
		message.error("打开连接失败，请检查主机、端口、账号、密码是否正确");
	})

}



const selectOlOptions=(index)=>{
	if(index==="delConnection"){
		//弹框提示 确认是否删除。
		dialog.error({
			title: '删除操作',
			content: '你确定需要删除这条【'+redisDataInf.value.name+"】数据吗？",
			positiveText: '确认删除',
			negativeText: '取消',
			onPositiveClick: () => {
				//调用接口删除
				API.connectionDel({name:redisDataInf.value.name}).then((res)=>{
					if(res.code==="000000"){
						message.success("删除成功");
						refreshParentData({});

					}else{
						message.error("删除失败");
					}
				})
			},
			onNegativeClick: () => {

			}
		});
		//option.data.name
	}else if(index==="openConnection"){
		openConnect();
	}else if(index==="editInf"){
		//redisDataInf redisDataInf
		redisDataOo.value.redisDataInf = redisDataInf.value;
		showFlag('editRedisConnectionFlag',true);

	}

}

/***********业务处理**********************/


const getConnectionList =()=>{
	API.getConnectionList().then((res)=>{
		if(res.code==="000000"){
     let da = res.data;
			da.forEach((item,index)=>{
			 item.id = item.id;
			 item.key = item.name;
			 item.connectionFlag = false;
			 item.expand = false;
		 })
			   connectList.value = da;
			if(connectList.value.length>0){
				redisDataInf.value = connectList.value[0];
				openConnect();
			}else{
        //没有获取到对应的信息 因此需要置为空。
				redisDataInf.value = null;
			}

		}
	})
}
//刷新数据。
const refreshParentData=(dta)=>{
	if(dta){
		Object.keys(dta).forEach((key)=>{
			showFlag(key,dta[key]);
		})
	}else{
		showFlag('welcomeFlag',true);
	}
	getConnectionList();
}
const workSuccess=()=>{
	getConnectionList();
}


onMounted(()=>{

	getConnectionList();
	//默认获取第一个连接的信息


})

</script>

<template>
<div class="home-index">
<div class="left-menus">
	<dv-border-box-3>
	 <div class="left-menus-mains">
		 <div style="position: absolute;top:3rem">
			 <dv-decoration-8 style="width:300px;height:50px;" />
		 </div>
		 <div style="position: absolute;left: 5rem">
			 <n-breadcrumb separator=">">
				 <n-breadcrumb-item>
				  <h3><n-icon><Server></Server></n-icon>redis</h3>
				 </n-breadcrumb-item>
				 <n-breadcrumb-item>
					 <n-dropdown
						 placement="bottom-start"
						 trigger="click"
						 size="medium"
						 :options="options"
						 @select="selectOptions"
					 >
						 <h3><a href="javascript:;">
							 <n-icon><DesktopSharp></DesktopSharp></n-icon>
							 文件</a></h3>
					 </n-dropdown>
				 </n-breadcrumb-item>
			 </n-breadcrumb>

		 </div>

		 <div style="position: absolute;top:7rem;width: 85%">
			 <n-infinite-scroll style="height: 70vh" :distance="10" @load="handleLoad">

				 <div class="row_card" v-for="(item,index) in connectList" :key="index" >
					 <n-row  gutter="24" class="row-card-redis" :class="leftMenuActiveCurrent===index?'row_card_active':''">
						 <n-col :span="2" @click="switchExtend(item)">
							 <n-icon v-if="item.expand" >
								 <CaretDownSharp></CaretDownSharp>
							 </n-icon>
							 <n-icon v-else>
								 <CaretUp></CaretUp>
							 </n-icon>
						 </n-col>
						 <n-col :span="16" @click="switchAl(item,index)">
							 <span class="font-inf">
								 <n-icon><Server></Server></n-icon>&nbsp;{{item.name}}[{{item.host}}]</span>
						 </n-col>
						 <n-col :span="6" @mouseover="handleMouseOver(item)">
							 <n-dropdown
								 placement="bottom-start"
								 trigger="click"
								 size="small"
								 :options="renderOptions(olOptions)"
								 :data="item"
                 ref="rowDropdownRef"
								 @select="selectOlOptions"
							 >操作</n-dropdown>
						 </n-col>
					 </n-row>

					 <n-anchor
						 affix
						 :trigger-top="24"
						 :top="88"
						 style="z-index: 1"
						 :bound="24"
						 :show-rail="false"
						 v-if="item.connectionFlag&&item.expand"
					 >
						 <n-anchor-link @click="dbclick(it,ind,item)" :class="dbActiveCurrent===ind?'n-anchor-link-row':''" v-for="(it,ind) in item.dbs" :key="ind">
							 <span class="font-inf"><n-icon><Server></Server></n-icon>&nbsp;db{{it.key}}（{{it.value}}）</span>
						 </n-anchor-link>
					 </n-anchor>
				 </div>
			 </n-infinite-scroll>
		 </div>


	 </div>
	</dv-border-box-3>
</div>
<div class="right-workers">
	<dv-border-box-9>
		<div class="right-workers-mains">
			<rightContent :data="data" @refreshParentData="refreshParentData" :redisDataOo="redisDataOo" @workSuccess="workSuccess"></rightContent>
		</div>
	</dv-border-box-9>
</div>
</div>
</template>

<style  >
.home-index{width: 100%;height:90vh;;display: flex;flex-direction: row;flex-wrap: nowrap;}
.left-menus{width: 30%;height: 100%;}
.right-workers{width: 70%;}
.left-menus-mains{padding-left: 2rem;padding-top: 1.5rem;position: relative}
.right-workers-mains{padding-left: 1rem;padding-top: 0.5rem;height: 100%;width: 98%;margin: auto}
.btn-row{display: flex;flex-wrap: nowrap;flex-direction: row;justify-content: flex-end;width: 98%;padding-top: 1rem;}
.n-input,.n-radio-group,.n-radio-button,
.n-button,.n-Descriptions,.n-card,
.n-textarea
{background: transparent !important;}

input{--n-border-focus: #4DA9DF !important;outline: none;}
.row-card-redis{background-color: rgba(32,35,55,0.5);height: 46px;line-height: 46px;cursor: pointer;margin-bottom: 1rem}
.row_card{background-color: rgba(32,35,55,0.5);margin: .5rem;padding: 1rem;border: 1px solid #4DA9DF;cursor: pointer; }
.row_card_active{background-color: rgba(43,103,226,0.5)}
.font-inf{
	display: flex;           /* 使用flex布局 */
	align-items: center;     /* 垂直居中对齐 */
	justify-content: start; /* 水平居中对齐 */
	height: 100%;
	cursor: pointer;
}
.n-anchor-link-row{background-color: #fad0c4 !important;}
.right-workers-contents{height: 100%;padding-top: 1rem}
</style>
