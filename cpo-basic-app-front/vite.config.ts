import { defineConfig } from 'vite'
import { fileURLToPath } from "node:url"
import vue from '@vitejs/plugin-vue'
import { visualizer } from 'rollup-plugin-visualizer'
import path from "path";
const serverList = [
  "http://127.0.0.1:9004/", // 开发环境
  "http://172.31.186.189/", // 测试环境
  "http://172.31.200.53/", // 演示环境
]
const target = serverList[0];

export default defineConfig({
  plugins: [
    vue(),
    visualizer({
      emitFile: false,
      file: "stats.html", //分析图生成的文件名
      open: true //如果存在本地服务端口，将在打包后自动展示
    })
  ],
  server: {
    host: true,
    port: 80,
    cors: true,
    proxy: {
      "/graphql": {
        target: target,
        // target: serverList[2],
        changeOrigin: true,
      },
      '/api/n9e': {
        // target: target,
        target: target,
        changeOrigin: true,
      },
      '/auth': {
        // target: 'http://127.0.0.1:9004',
        target: target,
        changeOrigin: true,
      },

      "/nodeman": {
        // target: target,
        changeOrigin: true,
        target: 'http://127.0.0.1:8091',
        rewrite: (path) => path.replace(/^\/nodeman/, '')
      },

      '/log-platform': { //日志分析
        target: target,
        changeOrigin: true,
      },

      "/aiops/resource": {
        target: 'http://127.0.0.1:9004',
        changeOrigin: true,
      },
      "/alarm-manager": {
        target: target,
        changeOrigin: true,
        // rewrite(path) {
        //     return path.replace(/^\/alarm-manager/, '/alarm')
        // },
      },

      "/alarm-chain": {
        target: target,
        changeOrigin: true,
      },
    },
  },
  build: {
    target: "modules", //设置最终构建的浏览器兼容目标  //es2015(编译成es5) | modules
    outDir: 'dist',
    chunkSizeWarningLimit: 800,
    sourcemap: false,
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    },
    rollupOptions: {
      output: {
        chunkFileNames: 'assets/js/[name]-[hash].js',
        entryFileNames: 'assets/js/[name]-[hash].js',
        assetFileNames: 'assets/static/[name]-[hash].[ext]',
        manualChunks(id) {
          // 判断是否为第三方依赖，将其拆分到 vendor 中
          if (id.includes('node_modules')) {
            // 这里代码可以优化一下，但是我懒，我相信你一定可以的！
            if (id.includes('element-plus')) {
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
  resolve: {
    //设置便捷图片路径引用
    extensions: [".mjs", ".js", ".ts", ".jsx", ".tsx", ".json", ".vue"],
    alias: {
      "@": path.resolve(__dirname, "./src"),
      "@img": fileURLToPath(new URL("./src/assets/imgs", import.meta.url)),
    },
  },
  // 重写命名空间
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/element/index.scss" as *;`,
      },
    },
  },
})
