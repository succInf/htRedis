/**
 * 自动注入组件
 */
const autoComponents = {
	version:"0.0.1",

}
autoComponents.asyncComponent = function(app){
	const components = import.meta.glob("../components/**/*.vue");
	Object.keys(components).forEach((key)=>{
		var file = components[key];
	    var fileName = autoComponents.getFileName(key);
			console.log(fileName)
		app.component(fileName,file.default);
	})


}

autoComponents.getFileName=function(originName){
	originName = originName.match(/\/components\/(\S*).vue/)[1].split("\/")[1];
	return originName;
}


export default autoComponents
