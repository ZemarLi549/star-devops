export const assignOptions = [
  {
    label:'全部',
    value:2,
  },
  {
    label:'已启用',
    value:1,
  },
  {
    label:'已停用',
    value:0,
  },

]
import type { FormRules } from "element-plus";
export const rules = <FormRules<RuleForm>>({
  assignName: [
    { required: true, message: "请输入分派策略名称", trigger: "change" },
    { min: 1, max: 100, message: '字符长度不超过100', trigger: 'change' },
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
    }, {
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
});

export interface AssignPoliceList{
  assignName: string;
  notifyGroups: string;
  policyId: number;
  remark?: string|null;
  status: number;
  updateTime: string;
  updater?: string|null;
}

export interface Condition {
  dataGroupId: string;
  items: any[];
  relation: string;
}
export interface RuleForm {
  assignName: string;
  remark: string;
  groupIds: number[];
  notifyId: number|undefined;
  conditions: Condition[];
  assignAggregate: {
    aggregate: boolean,
    period: number,
    labelKey: string,
  };
}