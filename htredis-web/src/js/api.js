import httpRequest from "../../static/js/HttpRequest.js";

const API = {

}
//获取连接信息
API.getConnectionList=()=>{
	return httpRequest.adminBodyPost("/redis/hosts/getList");
}
API.addConnection=(data)=>{
	return httpRequest.adminBodyPost("/redis/hosts/doSave",data);
}

API.connectionDel=(id)=>{
	return httpRequest.adminBodyPost("/redis/hosts/doDelete?id="+id);
}
API.editConnection=(data)=>{
	return httpRequest.adminBodyPost("/redis/hosts/doUpdate",data);
}

API.openConnection =(id)=>{
	return httpRequest.adminBodyPost("/redis/hosts/openConnection?id="+id);
}

API.getDbs =(id)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getAllDbSizes?id="+id);
}

API.getInfs=(id)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getRedisBaseInf?id="+id);
}
API.getKeysByPage=(id,cursor,db,size,searchKey)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getKeysByDb?id="+id+"&cursor="+cursor+"&db="+db+"&size="+size+"&searchKey="+searchKey);
}

API.getValueOb = (id,db,type,key)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getValueByType?id="+id+'&db='+db+'&type='+type+"&key="+key)
}

API.addOrUpateStrValue = ( id, db, key, value, times)=>{
	return httpRequest.adminBodyPost("/redis/hosts/addOrUpateStrValue",{id:id,db:db,key:key,value:value,expire:times})
}

API.removeStrAndValue = ( id, db, key,type)=>{
	return httpRequest.adminBodyPost("/redis/hosts/removeStrAndValue",{id:id,db:db,key:key,type:type})
}

API.addOrUpdateHashValue=(data)=>{
	return httpRequest.adminBodyPost("/redis/hosts/addHashValue",data);
}
API.removeHashField=(id,db,key,field)=>{
	return httpRequest.adminBodyPost("/redis/hosts/removeHashField?id="+id+"&db="+db+"&key="+key+"&field="+field);
}
API.removeHash=(id,db,key)=>{
	return httpRequest.adminBodyPost("/redis/hosts/removeHash?id="+id+"&db="+db+"&key="+key);
}
API.getHashItems=(id,db,key)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getHashItems?id="+id+"&db="+db+"&key="+key);
}
API.getHashItem=(id,db,key,field)=>{
	return httpRequest.adminBodyPost("/redis/hosts/getHashItem?id="+id+"&db="+db+"&key="+key+"&item="+field);
}


export default API
