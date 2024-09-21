
import axios from 'axios';
import QS from 'qs';


const httpRequest={
    ContentTypeValue:null,
};
axios.defaults.baseURL = process.env.BASE_API_URL;
axios.defaults.timeout = 10000;
axios.defaults.withCredentials=true;

//拦截器设置
axios.interceptors.request.use(function (config) {
	if(httpRequest.ContentTypeValue!=null){
		config.headers['Content-Type'] = httpRequest.ContentTypeValue;
	}
    return config;
}, function (error) {
    if(top.$message){
        top.$message.error(error);
    }
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    return response.data;
}, function (error) {
    // 对响应错误做点什么
	return Promise.reject(error);
});


httpRequest.adminBodyPost=function(url,data){
    //遍历data，如果是对象，则删除该元素。
    httpRequest.ContentTypeValue = "application/json;charset=UTF-8";
    return axios({
        method: 'post',
        url: url,
        data:data,
    });
}


httpRequest.adminBodyGet=function(url,data){
	//遍历data，如果是对象，则删除该元素。
	httpRequest.ContentTypeValue = "application/json;charset=UTF-8";
	return axios({
		method: 'get',
		url: url,
		data:data,
	});
}

httpRequest.adminParamPost = function (url,data){
    httpRequest.ContentTypeValue = "application/x-www-form-urlencoded;charset=UTF-8";
    return axios({
        method: 'post',
        url: url,
        params:QS.stringify(data)
    });
}
httpRequest.adminGet = function (url,data){
    httpRequest.ContentTypeValue = "application/x-www-form-urlencoded;charset=UTF-8";
    return axios({
        method: 'get',
        url: url,
        params:QS.stringify(data),
    });
}





httpRequest.adminPost = function (url,data){
    httpRequest.ContentTypeValue = "application/x-www-form-urlencoded;charset=UTF-8";
    return axios({
        method: 'post',
        url: url,
        data:QS.stringify(data),
    });
}

export default  httpRequest

