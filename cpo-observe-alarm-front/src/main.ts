import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import "ds-datetime-picker-plus/dist/style.css";
import 'element-plus/theme-chalk/src/index.scss'
import '@/styles/reset.scss'
import "@/styles/style.scss"
import "@/styles/elementplus/elementplus-cover.scss"
import '@/styles/lib.scss'

import { createPinia } from 'pinia'
import router from './router'
import { renderWithQiankun, qiankunWindow } from 'vite-plugin-qiankun/dist/helper';
import actions from "@/plugin/qiankun/action";
// import "@/mock/index.js"
// 导入字体图标
import "./assets/iconfont/iconfont.js";
import "./assets/iconfont/iconfont.css";

import {
	FontIcon
} from "./components/ReIcon";
import Btn from './directives/btn.ts'
import Tooltip from './directives/tooltip.ts'
import Trim from './directives/trim.ts'
import VueJsonPretty from "vue-json-pretty";
import "vue-json-pretty/lib/styles.css";
const pinia = createPinia();

let app: any = null;

const render = (props: any) => {
  app = createApp(App)
   // 注入 actions 实例
   if (props) {
    actions.setActions(props);
  }
  // 全局注册`@iconify/vue`图标库
  app.component("FontIcon", FontIcon);
  app.component("VueJsonPretty", VueJsonPretty);
  app.use(ElementPlus).use(router).use(pinia).mount('#app')
  
  // 创建自定义指令，解决elementplus按钮不会失焦的问题
  app.directive('btn', Btn);
  // 根据内容是否溢出动态显示tooltip
  app.directive('tooltip', Tooltip)
  // 输入框失去焦点自动去除空格
  app.directive('trim', Trim)
}

if (qiankunWindow.__POWERED_BY_QIANKUN__) {
  renderWithQiankun({
    // bootstrap 只会在微应用初始化的时候调用一次，
    // 下次微应用重新进入时会直接调用 mount 钩子，不会再重复触发 bootstrap。
    bootstrap() {
      console.log('vue app bootstrap');
    },
    // 应用每次进入都会调用 mount 方法，通常我们在这里触发应用的渲染方法
    async mount(props: any) {
      render(props)
    },
    // 可选生命周期钩子，仅使用 loadMicroApp 方式加载微应用时生效
    update() {
      console.log('vue app update');
    },
    // 应用每次 切出/卸载 会调用的方法，通常在这里我们会卸载微应用的应用实例
    unmount() {
      console.log('vue app unmount');
      app.unmount();
      app = null;
    }
  });
}