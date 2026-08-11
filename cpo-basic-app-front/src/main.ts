import { createApp } from 'vue'
import App from './App.vue'
import { store } from "@/stores"
import router from './router'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import "element-plus/theme-chalk/src/index.scss"
import './styles/reset.scss'
import './styles/elementplus.scss'
import './styles/style.scss'
import './styles/element-rewrite.scss'
import EmptyVue from './components/empty/Empty.vue'
import Btn from './directives/btn.ts'
import Tooltip from './directives/tooltip.ts'

import "./assets/iconfont-new/iconfont.js";
import "./assets/iconfont-new/iconfont.css";

// 路由守卫
import "./permission"

// 引入微服务
import "./plugin/qiankun/micro-app"

// 导航条
import Nprogress from "nprogress";
import "nprogress/nprogress.css";
Nprogress.configure({ showSpinner: false, ease: "ease", speed: 500 });
Nprogress.start();


const app =  createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 全局注册`@iconify/vue`图标库
import {
	FontIcon
} from "./components/ReIcon";

app.component("FontIcon", FontIcon);
app.component("EmptyVue", EmptyVue);


app.use(ElementPlus)
app.use(store)
app.use(router)
app.mount('#basic_app');

// 创建自定义指令，解决elementplus按钮不会失焦的问题
app.directive('btn', Btn);
app.directive('tooltip', Tooltip)