
// 我们需要将之前单独提出来的action添加到我们注册的子应用的props当中作为在子应用中接收的参数
import { registerMicroApps } from "qiankun"
import router from '@/router'
import { action } from "./action"
import { useConfigStore } from "@/stores/modules/microConfig";
import { useUserStore } from "@/stores/modules/user";
import isMicroProjectDeploy from "@/utils/isMicroProjectDeploy";
import ControlApi from "@/apis/control";
import { PROJECT_MAP, microAppsDataAll } from "@/globals/constant";
import { isBase64 } from '@/utils'

interface MicroAppInit {
  name: string;
  entry: string;
  activeRule: string[];
  container: string;
  sandbox: object;
  props: object;
}

interface DeployConfig {
  menuClosed: boolean;
  metaList: Array<any>;
  deployMode?: string;
}

const configStore = useConfigStore()
const userStore = useUserStore()
const name = import.meta.env.VITE_NAME
const microAppInit = (microAppsData) => {
  const microApps: MicroAppInit[] = microAppsData.map(item => {
    return {
      name: item.name, // 子应用的名称 链路相关
      entry: item.entry, // 默认会加载这个路径下的html，解析里面的js
      activeRule: item.activeRule, // 匹配的路由
      container: "#sub_app", // 加载的容器
      sandbox: {
        strictStyleIsolation: true, // 开启样式隔离
      },
      props: {
        action,
        hasPathAuth: userStore.hasPathAuth,
        isMicroProjectDeploy,
        ...PROJECT_MAP,
      }
    }
  })
  registerMicroApps(microApps, {
    beforeLoad: [(app) => { console.log('before load', app.name); return Promise.resolve() }],
    beforeMount: [(app) => { console.log('before mount', app.name); return Promise.resolve() }],
    afterMount: [(app) => {
      const name = app.name.split('-').join('_')
      const appContainer = document.getElementById(`__qiankun_microapp_wrapper_for_${name.toLowerCase()}__`)
      appContainer.style.height = '100%'
      return Promise.resolve()
    }],
  })
}
export const microAppLoad = async () => {
  try {
    if (configStore.deployList.length) return false
    let deployConfig: DeployConfig = {
      menuClosed: true,
      metaList: [],
      deployMode: 'all', // 是否独立部署
    }
    // 本地允许调试时，直接加载子应用
    if (name === 'development') {
      deployConfig = {
        deployMode: 'all',
        menuClosed: true,
        metaList: [
          {
            moduleName: 'observe-log',
            moduleUrl: 'http://localhost:3004/'
            // moduleUrl: 'http://172.30.34.73/observe-log/'
          },
          {
            moduleName: 'observe-metric',
            moduleUrl: 'http://localhost:3002/'
            //  moduleUrl: 'http://172.30.34.73/observe-metric/'
          },
          {
            moduleName: 'observe-trace',
            moduleUrl: 'http://localhost:3001/'
            //  moduleUrl: 'http://172.30.34.73/observe-trace/'
          },
          {
            moduleName: 'observe-config',
            moduleUrl: 'http://localhost:3003/'
            //  moduleUrl: 'http://172.30.34.73/observe-config/'
          },
          {
            moduleName: 'observe-alarm',
            moduleUrl: 'http://localhost:3005/'
            //  moduleUrl: 'http://172.30.34.73/observe-alarm/'
          },
          {
            moduleName: 'ai-productivity',
            moduleUrl: 'http://localhost:3011/'
          },
        ]
      }
    } else {
      // 请求后端获取部署项目
      deployConfig = await ControlApi.getDeployConfig()
    }
    const deployList = deployConfig.metaList.map(item => {
      return item.moduleName
    })

    configStore.setDeployConfig(deployList)
    configStore.setMenuClosed(deployConfig.menuClosed)
    // 独立部署
    if (deployConfig.deployMode === 'single') {
      configStore.setSingleDepoly(true)

      // 路由配置
      router.addRoute(
        {
          path: "/",
          redirect: userStore.menuList[0],
        },
      )
    }

    const microAppsData = microAppsDataAll.filter((item) => deployList.includes(item.name))
    microAppsData.forEach(item => {
      deployConfig.metaList.forEach(element => {
        if (item.name === element.moduleName) {
          item.entry = isBase64(element.moduleUrl) ? atob(element.moduleUrl) : element.moduleUrl
          // item.entry = element.moduleUrl
        }
      })
    })
    microAppInit(microAppsData)

  } catch (error) {
    console.log(error);
  }
}

//  文档中心和登录配置
const data = JSON.parse(JSON.stringify(window.__config__))
if (data) {
  const accessPath = data[name]
  configStore.setDocumentUrl(accessPath.VITE_DOC_URL)
  configStore.setLoginConfig(accessPath.VITE_LOGIN_CONFIG)
}


