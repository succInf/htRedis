### 注意事项 
+ 安装依赖时，不能用cnpm，必须用npm否则无法正常发布版本

### 下载打包依赖

npm install --save-dev electron

npm install --save-dev @electron-forge/cli

npm exec --package=@electron-forge/cli -c "electron-forge import"

npm install --save-dev @electron-forge/plugin-fuses

### 启动方式
npm install

npm run start

npm run make 打包

### resoures说明
resources 目录 是打包需要用到的目录。里面是jdk文件 和java应用。
由于github限制 这里就不在进行提交。
具体参考对应目录下的readme.md文件。


### 说明
本目录旨在将项目打包成exe文件或者二进制文件。
本项目可以打包成window、mac、linux二进制文件。目前只封装了window系统。


### 技术支撑
如需定制化开发 可以添加微信 worker_680 进行私信。

如需提bug 可以直接提issue

作者仅利用空余时间进行更新代码 因此修复时间不确定 勿催 如确需使用可加微信进行定制化开发。



