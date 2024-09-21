const { app, Menu } = require('electron');

const windowMainMenus = {

}
windowMainMenus.menus = [
    {"label":"文件",
     "submenu":[
         {"label":"退出",accelerator:"CmdOrCtrl+Q",click:()=>app.quit()},

     ]
    }


]


windowMainMenus.createMenus = ()=>{
    const menu = Menu.buildFromTemplate(windowMainMenus.menus);
    Menu.setApplicationMenu(menu);
}

module.exports = windowMainMenus