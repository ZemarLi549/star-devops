import { defineStore } from "pinia";
import { reactive, ref } from "vue";
import customLabelApi from "@/request/api/custom-label";
import { StarTraceMonitor, OtherMonitorCate } from "@/views/custom-label/constant";
export const useAlarmStore = defineStore("alarm", () => {
  const alarmDuration = ref([
    new Date(new Date().getTime() - 3600 * 1000 * 24 * 30),
    new Date(),
  ]);
  const setAlarmDuration = (date: Date[]) => {
    alarmDuration.value = date
  };

  /* 自定义标签 */ 
  // json表达式
  const currentExp = ref("")
  const setCurrentExp = (exp) => {
    currentExp.value = exp
  }
  // 通过点击生成的json表达式
  const generateExp = ref("")
  const setGenerateExp = (exp) => {
    generateExp.value = exp
  }
  // json解析结果
  const validResult = reactive({
    result: "", // 解析结果
    status: 0 // 解析状态 0: 未解析, 1: 已解析
  })
  const clearValidResult = () => {
    validResult.result = "";
    validResult.status = 0
  }
  // 模板类型
  const currentMonitorType = reactive({
    source: undefined,
    cate: 1
  })
  const setMonitorType = (monitorType) => {
    currentMonitorType.source = monitorType.source
    currentMonitorType.cate = monitorType.cate
  }
  // 当前模板
  const currentTemplate = ref(null);
  const setCurrentTemplate = (template) => {
    currentTemplate.value = template
  }
  // 模板获取
  const requestTemplate = () => {
    const cate =
        currentMonitorType.source === StarTraceMonitor
        ? currentMonitorType.cate
        : OtherMonitorCate;
    customLabelApi.getLabelTemplate({cate, source: currentMonitorType.source}).then((res) => {
      setCurrentTemplate(res)
    });
  }

  // json解析
  const validTemplate = () => {
    const cate =
      currentMonitorType.source === StarTraceMonitor
      ? currentMonitorType.cate
      : OtherMonitorCate;
    customLabelApi.testCustomLabel({cate, source: currentMonitorType.source, exp: currentExp.value}).then((res) => {
      if (res.isValid) {
        validResult.result = res.result
        validResult.status = 1
      } else {
        validResult.result = ""
        validResult.status = 1
      }
    })
  }

  return {
    alarmDuration,
    setAlarmDuration,
    currentExp,
    currentMonitorType,
    validResult,
    setCurrentExp,
    setMonitorType,
    validTemplate,
    currentTemplate,
    setCurrentTemplate,
    requestTemplate,
    clearValidResult,
    generateExp,
    setGenerateExp
  };
});
