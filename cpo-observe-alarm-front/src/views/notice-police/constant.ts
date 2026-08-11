export const statusOptions = [
  { label: '发生时', value: 0 },
  { label: '认领时', value: 1 },
  { label: '关闭时', value: 2 },
  // { label: '全部', value: 4 },
]

export const timeOptions = [
  { label: '任何时间', value: 0 },
  { label: '工作日时间', value: 1 },
  { label: '自定义', value: 2 },
]

export const levelOptions = [
  { label: '严重', value: 5 },
  { label: '重要', value: 4 },
  { label: '次要', value: 3 },
  { label: '警告', value: 2 },
  { label: '信息', value: 1 },
]

// const validatorName = (rule, value, cb) => {
//   if(!value) return cb(new Error('请输入通知策略名称'))
//   if (value.length > 25) return cb(new Error('策略名称过长'))
//   return cb()
// }

// const validatorRemark = (rule, value, cb) => {
//   if (value && value.length > 100) return cb(new Error('描述内容过长'))
//   return cb()
// }

export const rules = {
  notifyName: [
    // { required: true, validator: validatorName, trigger: ['blur']}
    { required: true, message: '策略名称不能为空', trigger: ['blur'] }
  ],
  // remark: [
  //   { validator: validatorRemark, trigger: ['blur']}
  // ]
}

export const ChanneMap = {
  mail: {
    bgColor: "#fff7e6",
    hoverColor: "#fbb602",
    img_url: "smtp",
  },
  qywechat: {
    bgColor: "#ebf5ff",
    hoverColor: "#1f69ff",
    img_url: "wechat",
  },
  message: {
    bgColor: "#e2f8f5",
    hoverColor: "#00ccae",
    img_url: "message",
  },
  mrsr_message: {
    bgColor: "#f0f0d3",
    hoverColor: "#b3b523",
    img_url: "mrsr_message",
  },
  zhpt_message: {
    bgColor: "#d3f0e4",
    hoverColor: "#23b579",
    img_url: "zhpt_message",
  },
  qywechat_bot: {
    bgColor: "#ebf5ff",
    hoverColor: "#1f69ff",
    img_url: "weixin_robot",
  },
  feishu_bot: {
    bgColor: "#ebf5ff",
    hoverColor: "#1f69ff",
    img_url: "robot",
  },
  txnlpt_message: {
    bgColor: "#f0e9d3",
    hoverColor: "#b59023",
    img_url: "message",
  },
  default: {
    bgColor: "#ebf5ff",
    hoverColor: "#1f69ff",
    img_url: "message",
  },
};