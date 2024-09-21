const windowMainUtils={

}
windowMainUtils.darwin="darwin";
windowMainUtils.mac="mac";
windowMainUtils.win32="win32";
windowMainUtils.win="win";
windowMainUtils.linux="linux";

windowMainUtils.getOsPlatform=()=>{
    let osPlatform = process.platform;
    if (osPlatform === windowMainUtils.darwin) {
        osPlatform = windowMainUtils.mac;
    } else if (osPlatform === windowMainUtils.win32) {
        osPlatform = windowMainUtils.win;
    } else if (osPlatform === windowMainUtils.linux) {
        osPlatform = windowMainUtils.linux;
    }
    // 可以根据需要添加更多的操作系统判断
    return osPlatform;

}


module.exports = windowMainUtils