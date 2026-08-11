// 引用 Mock
import Mock from "mockjs";

export const userData = Mock.mock("/data/list", "get", {
  data: {
    // 属性 list 的值是一个数组，随机生成 1 到 10 个元素
    "list|1-10": [
      {
        // 随机生成1-10个★
        "string|1-10": "★",
        // 随机生成1-100之间的任意整数
        "number|1-100": 1,
        // 生成一个浮点数，整数部分大于等于 1、小于等于 100，小数部分保留 1 到 10 位。
        "floatNumber|1-100.1-10": 1,
        // 随机生成一个布尔值，值为 true 的概率是 1/2，值为 false 的概率同样是 1/2。
        "boolean|1": true,
        // 随机生成一个布尔值，值为 false 的概率是 2 / (2 + 5)，值为 true 的概率是 5 / (2 + 5)。
        "bool|2-5": false,
        // 从属性值 object 中随机选取 2-4 个属性
        "object|2-4": {
          310000: "上海市",
          320000: "江苏省",
          330000: "浙江省",
          340000: "安徽省",
        },
        // 通过重复属性值 array 生成一个新数组，重复次数为 2
        "array|2": ["AMD", "CMD", "UMD"],
        // 执行函数 function，取其返回值作为最终的属性值，函数的上下文为属性 'name' 所在的对象。
        foo: "哇哈哈哈哈",
        name: function () {
          return this.foo;
        },
        // 根据正则表达式 regexp 反向生成可以匹配它的字符串。用于生成自定义格式的字符串。
        regexp: /\d{5,10}/,
      },
    ],
  },
  code: 200,
  success: "ok",
});

export const mockNoticePage = Mock.mock("/alarm-manager/notice/page", "post", {
  // 属性 list 的值是一个数组，随机生成 1 到 10 个元素
  data: {
    "records|1-10": [
      {
        "alarmId|1-100": "1",
        cate: 1,
        "level|0-2": 1,
        "noticeId|1-100": "1",
        notifyPolicies: "test01; test10",
        notifyGroups: "测试团队; 研发团队",
        notifyTime: "2024-03-27 15:36:07",
      },
    ],
  },
  code: 200,
  success: "ok",
});
export const mockNoticeDetail = Mock.mock(
  "/alarm-manager/notice/detail",
  "post",
  (options) => {
    return {
      data: {
        alarmId: 10000,
        cate: 1,
        sources: "星迹可观测; Prometheus",
        level: 1,
        notifyTime: "2024-03-27 15:44:29",
        notifyPolicies: ["test01", "test10"],
        notifyPolicyRemarks: ["666", "888"],
      },
      code: 200,
      success: "ok",
    };
  }
);

export const mockAlarmPage = Mock.mock(
  "/alarm-manager/alarm/agg/page",
  "post",
  (options) => {
    const params = JSON.parse(options.body);
    const data = [];
    for (let i = 0; i < 10; i++) {
      data.push({
        alarmId: "333333333333333333333333333333333333333333333333333333" + i,
        assignRuleNames: "fwaefawf",
        level: i % 3,
        cate: 1,
        uniqueAgg: "18/18",
        state: params.state ?? i % 3,
        firstTriggerTime: "2023-11-12 10:12:12",
        notifyTime: "2023-11-12 10:12:12",
        updateTime: "2023-11-12 10:12:12",
        updater: "张三",
        content: "告警告警告警告警告警告警告警告",
        duration: "14分钟",
        eventSize: 10,
        sources: "星迹可观测; Prometheus",
      });
    }
    return {
      data: {
        records: data,
        total: 100,
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockAlarmDetail = Mock.mock(
  "/alarm-manager/alarm/agg/detail",
  "post",
  (options) => {
    return {
      data: {
        alarmId: "1773290264106921986",
        assignRuleNames: ["weipan4", "qiangzou"],
        assignRuleRemarks: [
          "这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长",
          "这是qiangzou创建的分派策略",
        ],
        cate: 1,
        closeReason: "关闭原因1",
        content:
          "这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长",
        duration: "14分钟",
        firstTriggerTime: "2024-03-28 17:50:08",
        labels: `{"service_name":"nodeA","service_id":"123456"}`,
        level: 1,
        notifyTime: "2024-03-28 18:05:08",
        sources: "星迹可观测; Prometheus",
        state: 0,
        updateTime: "2024-03-28 18:04:08",
        updater: "admin",
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockAlarmEventPage = Mock.mock(
  "/alarm-manager/alarm/agg/page/event",
  "post",
  (options) => {
    const data = [];
    for (let i = 0; i < 100; i++) {
      data.push({
        eventId:
          "177329026410692198617732902641069219861773290264106921986" + i,
        level: i % 3,
        cate: 1,
        triggerTime: "2023-11-12 10:12:12",
      });
    }
    return {
      data: {
        records: data,
        total: 100,
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockAckAlarms = Mock.mock(
  "/alarm-manager/alarm/agg/ack",
  "post",
  (options) => {
    console.log(options);
    return {
      data: true,
      code: 200,
      success: "ok",
    };
  }
);
export const mockCloseAlarms = Mock.mock(
  "/alarm-manager/alarm/agg/close",
  "post",
  (options) => {
    console.log(options);
    return {
      data: true,
      code: 200,
      success: "ok",
    };
  }
);
export const mockAlarmCount = Mock.mock(
  "/alarm-manager/alarm/agg/count",
  "post",
  (options) => {
    return {
      data: {
        closed: 3,
        dealing: 3,
        pending: 3,
        total: 9,
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockUnassignPage = Mock.mock("/alarm-manager/alarm/unique/page/unassign", "post",   (options) => {
  const data = [];
  for (let i = 0; i < 10; i++) {
    data.push({
      "alarmId": 1772999351484903426,
      "cate": 1,
      "level": 1,
      "state": 1,
      "triggerTime":"2024-03-27 22:49:09",
      "unique": 6
    });
  }
  return {
    data: {
      records: data,
      total: 100,
    },
    code: 200,
    success: "ok",
  };
});
export const mockUnassignDetail = Mock.mock(
  "/alarm-manager/alarm/unique/detail/unassign",
  "post",
  (options) => {
    return {
      data: {
        alarmId: "1773290264106921986",
        sources: "星迹可观测; Prometheus",
        level: 1,
        cate: 1,
        content:
          "这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长",
        labels: `{"service_name":"nodeA","service_id":"123456"}`,
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockBlockPage = Mock.mock("/alarm-manager/alarm/unique/page/block", "post",   (options) => {
  const data = [];
  for (let i = 0; i < 10; i++) {
    data.push({
      "alarmId": 1772999351484903426,
      "cate": 1,
      "level": 1,
      "firstTriggerTime":"2024-03-27 22:49:09",
      "unique": 6,
      "blockTime": "2024-03-29 18:39:23",
      blockRuleNames: "fwaefawf",
      content: "告警告警告警告警告警告警告警告",
    });
  }
  return {
    data: {
      records: data,
      total: 100,
    },
    code: 200,
    success: "ok",
  };
});
export const mockBlockDetail = Mock.mock(
  "/alarm-manager/alarm/unique/detail/block",
  "post",
  (options) => {
    return {
      data: {
        alarmId: "1773290264106921986",
        blockRuleNames: ["weipan4", "qiangzou"],
        blockRuleRemarks: [
          "这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长",
          "这是qiangzou创建的分派策略",
        ],
        blockTime: "2024-03-28 18:05:08",
        firstTriggerTime: "2024-03-28 17:50:08",
        cate: 1,
        closeReason: "关闭原因1",
        content:
          "这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长的显示超长这个一段内容的显示超长这个一段内容的显示超长",
        labels: `{"service_name":"nodeA","service_id":"123456"}`,
        level: 1,
        sources: "星迹可观测; Prometheus",
      },
      code: 200,
      success: "ok",
    };
  }
);
export const mockCustomLabel = Mock.mock("/alarm-manager/alarm/custom/label/page", "post", (options) => {
  const data = [];
  for (let i = 0; i < 10; i++) {
    data.push({
      "labelId": 64,
      "isDefault": false,
      "status": 1,
      "labelKey": "add1",
      "labelValue": null,
      "name": "新增1",
      "updateTime": 1720491453000,
      "updater": null
    });
  }
  return {
    data: {
      records: data,
      total: 100,
    },
    code: 200,
    success: "ok",
  };
});
export const mockApi = Mock.mock("/mock", "post", (options) => {
  return {
    data: {},
    code: 200,
    success: "ok",
  };
});
// mockAckAlarms = '/alarm-manager/alarm/agg/ack', // 我的/所有告警—批量认领
// mockCloseAlarms = '/alarm-manager/alarm/agg/close', // 我的/所有告警—批量关闭
