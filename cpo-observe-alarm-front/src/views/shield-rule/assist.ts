export const statusOptions = [
  {
    label:'全部',
    value:2,
  },
  {
    label:'已开启',
    value:1,
  },
  {
    label:'已停用',
    value:0,
  },

]
import type { FormRules } from "element-plus";
export const rules =<FormRules<RuleForm>>({
  ruleName: [
    { required: true, message: "请输入屏蔽规则名称", trigger: "change" },
    {  min:1,max: 100, message: '字符长度不超过100', trigger: 'change' },
  ],
  groupIds: [
    {
      required: true,
      message: "请输入分派通知组",
      trigger: "change",
    },
  ],
  notifyId: [
    {
      required: true,
      message: "请输入通知策略",
      trigger: "change",
    },
  ],
  dataGroupId: [
    {
      required: true,
      message: "请选择数据单元",
      trigger: "change",
    },
  ],
  name: [
    {
      required: true,
      message: "请选择",
      trigger: "change",
    },
  ],
  condition: [
    {
      required: true,
      message: "请选择",
      trigger: "change",
    },
  ],
  values: [
    {
      type: 'array',
      required: true,
      message: "请选择",
      trigger: "change",
    },
    {
      validator: (_, value: any, callback: any) => {
        if (value && typeof value === 'object') {
       if(value[0] === '') return callback('请输入')
     }
      return callback()
    }, trigger: "change" }
  ],
  labelKey: [
    {
      required: true,
      message: "请选择",
      trigger: "change",
    },
  ],
  startTime: [
    {
      required: true,
      message: "请选择",
      trigger: "change",
    },
  ],
  dayOfWeek: [
    {
      required: true,
      message: "请选择",
      trigger: "change",
    },
  ],
});

export interface ShieldRuleList{
  remark: string;
  ruleId:number;
  ruleName:string;
  status:number;
  updateTime:string;
  updater:string;
}

export interface Condition {
  dataGroupId: string;
  items: any[];
  relation: string;
}
export interface NotifyTime {
  type:number;
  startTime?: string;
  endTime?:string;
  dayOfWeek?: number[];
}
export interface RuleForm {
  ruleName: string;
  remark: string;
  conditions: Condition[];
  notifyTime: NotifyTime;
}

export interface DataGroupTree {
  children?: DataGroupTree[];
  child?: DataGroupTree[];
  dataGroupId:number;
  dataGroupName:string;
  dataGroupToken:string;
  iselement:Boolean;
  isselect:Boolean;
  label?:string;
  parentId?:number|null;
  remark:string|null;
  sortNum:number;
  value:number;
  workSpaceId:number;
  workSpaceName?:number|null;
  disabled?:Boolean
}

