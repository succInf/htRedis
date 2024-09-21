<script setup>
import { zhCN, dateZhCN,NConfigProvider } from 'naive-ui'

</script>

<template>

	<n-config-provider :hljs="hljs"   :locale="zhCN" :date-locale="dateZhCN">
		<n-message-provider >
			<n-notification-provider>
				<n-dialog-provider>
					<router-view></router-view>
			</n-dialog-provider>
			</n-notification-provider>
		</n-message-provider>
	</n-config-provider>


</template>

<style  >
@import url("../static/css/fonts/iconfont.css");
@import url("../static/css/index.css");
@import url("../static/css/light.css");


</style>
