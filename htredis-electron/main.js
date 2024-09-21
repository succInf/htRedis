// 引入 app 和 BrowserWindow 模块
const { app, BrowserWindow } = require('electron');
const { spawn,exec,execFile } = require('child_process');
//主窗口对象
let mainWindow;
const path = require('path');
const windowMainUtils = require("./mains/js/mainUtils.js")
const windowMainMenus = require("./mains/js/menu.js")
const net = require('net');
const port = 30002; // 假设你的 Spring Boot 应用监听这个端口
const host = 'localhost';
const webIndex = "http://localhost:30002/index";

 const javaPath = path.join(__dirname, 'resources', 'jdk17', 'bin', 'java');
 const jarPath = path.join(__dirname, 'resources', 'htredis', 'htRedis.jar');
 console.log(javaPath)

const javaProcess = spawn(javaPath, ['-jar', jarPath]);

// //const configPath = path.join(__dirname, 'resources', 'boots', 'config','application.yml');,'--spring.config.location=file:'+configPath

javaProcess.stdout.on('data', (data) => {
    console.log(`stdout: ${data}`);
});
javaProcess.stderr.on('data', (data) => {
    console.error(`stderr: ${data}`);
});
//
javaProcess.on('close', (code) => {
    console.log(`window is close ${code}`);
    javaProcess.kill();
    console.log(`child process exited with code ${code}`);
});




function getOsPlatform() {
    let osPlatform = process.platform;
    if (osPlatform === 'darwin') {
        osPlatform = 'mac';
    } else if (osPlatform === 'win32') {
        osPlatform = 'win';
    } else if (osPlatform === 'linux') {
        osPlatform = 'linux';
    }
    // 可以根据需要添加更多的操作系统判断
    return osPlatform;
}


const checkPort = (port, host, callback) => {
    const socket = net.createConnection(port, host);

    socket.on('connect', () => {
        callback(true);
        socket.end();
    });

    socket.on('error', (err) => {
        callback(false);
    });
};

const timer = setInterval(()=>{

    checkPort(port, host, (isListening) => {
        if (isListening) {
            //应用启动成功 清除定时器。
            clearInterval(timer);
            //启动成功 加载远程地址。
            console.log(`Spring Boot 应用已经启动并监听端口 ${port}。‌`);
            mainWindow.loadURL(webIndex);
        } else {
            console.log(`Spring Boot 应用尚未启动或未监听端口 ${port}。‌`);
        }
    });

},200)




const createWindow=()=>{
    console.log("platform::",windowMainUtils.getOsPlatform())


    mainWindow = new BrowserWindow({
        width: 1920,
        height: 1080,
        frame: false,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false
        }
    });
    //初始化加载文件。
    mainWindow.loadFile('./static/loading.html');
    // 打开开发者工具
    // mainWindow.webContents.openDevTools();
    mainWindow.on('closed', function () {
        // 取消引用 window 对象，‌如果你的应用支持多窗口的话，‌
        // 通常会把多个 window 对象存放在一个数组里面，‌
        // 与此同时，‌你应该删除相应元素。‌
        mainWindow = null;
    });
}



// Electron 会在初始化后并准备
// 创建浏览器窗口时，‌调用这个函数。‌
// 部分 API 在 ready 事件触发后才能使用。‌
//设置语言包
app.commandLine.appendSwitch('lang', 'zh-CN');
//创建菜单
app.on("ready",windowMainMenus.createMenus);
app.whenReady().then(createWindow);


// 当全部窗口关闭时退出。‌
app.on('window-all-closed', function () {
    // 在 macOS 上，‌除非用户使用 Cmd + Q 确定地退出，‌
    // 否则绝大部分应用及其菜单栏会保持激活。‌
    console.log("监听窗口关闭")
    let osPlatform = process.platform;
    if(osPlatform==='darwin'){
        //sudo lsof -i tcp:端口号 | awk 'NR>1 {print $2}' | xargs sudo kill -9
        exec('lsof -i tcp:'+port+' | awk \'NR>1 {print $2}\' | xargs  kill -9', (error, stdout, stderr) => {
            if (error) {
                console.error(`执行的错误: ${error}`);
                return;
            }
            console.log(`stdout: ${stdout}`);
            if (stderr) {
                console.log(`stderr: ${stderr}`);
            }
        });


    }else if(osPlatform==="win32"||osPlatform==="win64"){
        exec('for /f "tokens=5" %i in (\'netstat -ano ^| findstr "'+port+'"\') do taskkill /f /pid %i', (error, stdout, stderr) => {
            if (error) {
                console.error(`执行的错误: ${error}`);
                return;
            }
            console.log(`stdout: ${stdout}`);
            if (stderr) {
                console.log(`stderr: ${stderr}`);
            }
        });

    }else{

    }
    console.log(process.platform)
    if (process.platform !== 'darwin') app.quit();
});

app.on('activate', function () {
    // 在 macOS 上，‌当点击 dock 图标并且该应用中没有其他窗口打开时，‌
    // 通常在应用程序中重新创建一个窗口。‌
    if (mainWindow === null) createWindow();
});
