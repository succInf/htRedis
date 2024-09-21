<script setup>
import {ref} from "vue";
const tip = ref("程序加载中... 请稍后");

</script>

<template>
<div class="box">
  <div class="loadInf">
    <div class="loading"></div>
  </div>

  <div class="textInf">
    <span>{{tip}}</span>
  </div>


</div>

</template>

<style  lang="less">

@keyframes moveText {
  0% { transform: translateX(0); }
  25% { transform: translateX(10px); }
  50% { transform: translateX(20px); }
  75% { transform: translateX(30px); }
  100% { transform: translateX(0); }
}
@keyframes slideBackground {
  from {
    background-position: 0 0;
  }
  to {
    background-position: 100% 0;
  }
}
.box{
  height: 100vh;
  background-image: url("../static/images/loading.jpg");
  background-repeat: no-repeat;
  width: 100%;
  overflow: hidden;
  background-position: center;
  background-size: 100vw 100vh; /* 背景图大小设置为视口的宽度和高度 */
  animation: slideBackground 10s linear infinite;
}

.box .textInf{
top:50%;
left:40%;
position: absolute;
display: flex;
font-size: 42px;
margin: auto;
overflow: hidden;
animation: moveText 2s infinite alternate;
background-image: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%);
-webkit-background-clip: text;
background-clip: text;
color: transparent;
}
.loadInf{
  position: absolute;
  top:35%;
  left:50%;
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content: center; /* 水平居中 */

}
.loading {
  border: 6px solid #fad0c4; /* Light grey */
  border-top: 6px solid #ff9a9e; /* Blue */
  border-radius: 50%;
  width: 80px;
  height: 80px;
  animation: spin 2s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
