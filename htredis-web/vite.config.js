import { defineConfig,loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
// https://vitejs.dev/config/
import { resolve } from "path"
export  default  defineConfig(({command,mode})=>{
	const env = loadEnv(mode,process.cwd(),'');
	return {
		resolve:{
			alias:[
				{
					find: '@',
					replacement: resolve(__dirname, "src"),
				},
				{
					find: '~',
					replacement: resolve(__dirname, "static/api/"),
				},

			],
		},
		optimizeDeps: {
		},
		plugins: [
			vue(),
		],
		eslint: {
			enable: false
		},
		/**
		 * 静态资源目录，开发模式下会自动放到 / 下，生产模式下会自动放到 outDir 根路径下。
		 * 该目录可以配置为文件系统绝对目录或者相对于项目根目录的相对路径
		 */
		publicDir:'static',
		base:env.BASE_URI,//开发环境
		server: {
			host: '0.0.0.0',
			port: 3000,
			open: false,
			strictPort: false,
			https: false,
			proxy:{
				'/api/': {
					target: 'http://localhost:30002/',
					changeOrigin: true,
					rewrite: (path) => path.replace(/^\/api/, ''),
				},
			}

		},
		//生产模式打包配置
		build:{
			assetsInlineLimit:0,//小于此阈值的导入或引用资源将内联为 base64 编码，以避免额外的 http 请求。设置为 0 可以完全禁用此项。
			outDir:"../htredis-java/src/main/resources/static/",
			assetsDir:"static",
			cssCodeSplit:false,
		},
		logLevel:'info',
		define:{
			'process.env':{
				BASE_URL:env.BASE_URI,
				BASE_API_URL:env.BASE_API_URL,
			}
		}

	}
})


