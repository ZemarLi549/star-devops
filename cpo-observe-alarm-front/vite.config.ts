import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from "path"; //配置别名路径
import qiankun from 'vite-plugin-qiankun';
import { visualizer } from 'rollup-plugin-visualizer'
// https://vitejs.dev/config/
export default defineConfig({
  base:'/observe-alarm',
  plugins: [
    vue(),
    qiankun('observe-alarm', { // 配置qiankun插件
      useDevMode: true 
    }),
    visualizer({
      emitFile: false,
      file: "stats.html", //分析图生成的文件名
      open:true //如果存在本地服务端口，将在打包后自动展示
    })
  ],
  resolve: {
    //设置便捷图片路径引用
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname, './src'),
      }
    ]
  },
    // 重写命名空间
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/elementplus/namespace.scss" as *;`,
        },
      },
  },
  server: {
    host: true,
    port: 3005,
    cors: true,
    origin: 'http://localhost:3005', //不配这个的话静态资源加载都会从基座应用的地址
    proxy: {
      '/log-platform': { //日志分析
        changeOrigin: true,
        target: "http://172.30.35.68:8100",
        // target: "http://172.31.186.217:8100",
        // target: "http://172.31.186.189",
      },
      '/alarm-overview': {
        target: 'http://172.31.65.61:17000/api/n9e/',
        changeOrigin: true,
        rewrite: (path)=> path.replace(/^\/alarm-overview/, '')
      },
      // 工作台联调
      "/aiops/resource": {
        target: 'http://172.30.34.73:80/', // 开发环境
        // target: 'http://172.31.186.189:80/', // 测试环境 
        changeOrigin: true,
      },
      // 告警
      "/alarm-manager": {
        // target: 'http://172.31.65.35:9011/', // 开发环境
        // target: 'http://10.5.172.59:9011/',  
        target: 'http://172.30.34.73/', // 开发环境
        changeOrigin: true,
      },
      "/auth": {
        target: 'http://172.30.34.73/', // 开发环境
        changeOrigin: true,
      }
    },
  },
    //项目构建配置
    build: {
      terserOptions: {
        compress: {
          //生产环境时移除console
          drop_console: true,
          drop_debugger: true,
        },
      },
      // 关闭文件计算
      reportCompressedSize: false,
      target: "modules", //设置最终构建的浏览器兼容目标  //es2015(编译成es5) | modules
      outDir: "dist", // 构建得包名  默认：dist
      assetsDir: "assets", // 静态资源得存放路径文件名  assets
      sourcemap: false, //构建后是否生成 source map 文件
      minify: "terser", // 项目压缩 :boolean | 'terser' | 'esbuild'
      chunkSizeWarningLimit: 1000, //chunk 大小警告的限制（以 kbs 为单位）默认：500
      cssTarget: "chrome61", //防止 vite 将 rgba() 颜色转化为 #RGBA 十六进制符号的形式  (要兼容的场景是安卓微信中的 webview 时,它不支持 CSS 中的 #RGBA 十六进制颜色符号)
      rollupOptions: {
        output: {
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: 'assets/static/[name]-[hash].[ext]',
          manualChunks(id) {  
            // 判断是否为第三方依赖，将其拆分到 vendor 中  
            if (id.includes('node_modules')) {  
                // 这里代码可以优化一下，但是我懒，我相信你一定可以的！
                if (id.includes('element-plus')){  
                  return 'element-plus';  
                }
                if (id.includes('echarts') || id.includes('echarts-wordcloud')) {  
                  return 'echarts';  
                }
                if (id.includes('dayjs')) {  
                  return 'dayjs';  
                }
                if (id.includes('lodash-es') || id.includes('lodash')) {  
                  return 'lodash-es';  
                }
                if (id.includes('codemirror')) {  
                  return 'codemirror';  
                }
                if (id.includes('zrender')) {  
                  return 'zrender';  
                }
                if (id.includes('ds-datetime-picker-plus')) {  
                  return 'ds-datetime-picker-plus';  
                }
                return 'vendor';  
              }
        },  
        },
      },
    },
})
