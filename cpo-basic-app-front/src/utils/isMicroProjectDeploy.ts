import { useConfigStore } from "@/stores/modules/microConfig";
export default function isMicroProjectDeploy(project:string|string[]) {
  const configStore = useConfigStore()
  const { deployList } = configStore
  
 if (typeof project === 'string') {
    // project为字符串为判断是否该项目部署
    return deployList.includes(project)
  } else if (typeof project === 'object'){
    // project为数组为判断是否该数组中项目是否有任意一个部署
    return deployList.some(item => project.includes(item));
 } else {
   return false
  }
}