<template>
  <div class="control-card" id="alarm-info-card">
    <div class="card-header card-header__adjust">
      <span class="card-header-title">我的告警</span>
      <div class="time-change">
        <span :class="{ isActive: timeType === 0 }" @click="changeTime(0)">
          近7日
        </span>
        <span :class="{ isActive: timeType === 1 }" @click="changeTime(1)">
          近30日
        </span>
      </div>
    </div>
    <div class="card-content">
      <div :class="`alarm-message-box ${alarmInfo.bgColor}`">
        <img :src="alarmInfo.bgImg" alt="" />
        <div class="title">TIPS</div>
        <div class="info">
          {{
            alarmInfo.bgColor === "green"
              ? `${timeType === 0 ? "近7日" : "近30日"}` + alarmInfo.text
              : alarmInfo.text
          }}
        </div>
      </div>
      <el-row :gutter="12" class="alarm-self-box">
        <el-col :span="8" v-for="(item, index) in alarmData.selfAlarm">
          <div class="alarm-self-data-card" @click="goAlarmCenter(index, true)">
            <div class="top-content">
              <div class="text">
                {{ stateMap[index] }}
                <div class="icon-hover">
                  <FontIcon style="font-size: 18px" icon="icon-you" />
                </div>
              </div>
              <div class="number">{{ item.num }}</div>
            </div>
            <div class="detail-numbers">
              <div class="fx-row">
                <el-tooltip effect="dark" content="严重" placement="bottom">
                  <div class="detail-number detail-number__5">
                    <span class="dot"></span>
                    {{ item.items[4].num }}
                  </div>
                </el-tooltip>

                <el-tooltip effect="dark" content="重要" placement="bottom">
                  <div class="detail-number detail-number__4">
                    <span class="dot"></span>
                    {{ item.items[3].num }}
                  </div>
                </el-tooltip>

                <el-tooltip effect="dark" content="次要" placement="bottom">
                  <div class="detail-number detail-number__3">
                    <span class="dot"></span>
                    {{ item.items[2].num }}
                  </div>
                </el-tooltip>
              </div>

              <div class="fx-row">
                <el-tooltip effect="dark" content="警告" placement="bottom">
                  <div class="detail-number detail-number__2">
                    <span class="dot"></span>
                    {{ item.items[1].num }}
                  </div>
                </el-tooltip>

                <el-tooltip effect="dark" content="信息" placement="bottom">
                  <div class="detail-number detail-number__1">
                    <span class="dot"></span>
                    {{ item.items[0].num }}
                  </div>
                </el-tooltip>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-divider />
      <div class="alarm-team">
        <div class="alarm-team-title">所有告警</div>
        <el-row :gutter="15" class="alarm-team-box">
          <el-col :span="8" v-for="(item, index) in alarmData.teamAlarm">
            <div
              class="alarm-team-data-card"
              @click="goAlarmCenter(index, false)"
            >
              <div class="text">
                <span :class="`dot dot-${teamAlarmColorMap[index]}`"></span>
                {{ stateMap[index] }}
              </div>
              <div class="number">{{ item.num }}</div>
              <div class="icon-hover">
                <FontIcon
                  style="font-size: 18px; line-height: 1; color: #bfbfbf"
                  icon="icon-you"
                />
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import ControlApi from "@/apis/control";
import dayjs from "dayjs";
import { AlarmInfo } from "../constant";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage } from "element-plus";

interface AlarmType {
  num?: number;
  type?: 0 | 1 | 2 | 3 | 4 | 5; // 0恢复 1信息 2警告 3次要 4重要 5严重
}

interface AlarmData {
  num?: number;
  state?: 0 | 1 | 2; // 0 待处理; 1 处理中; 2 已关闭;
  items?: AlarmType[];
}

const pickData = (data: AlarmData[]) => {
  const newData = data.sort((a, b) => a.state - b.state);
  newData.forEach((item) => {
    item.items = item.items
      .filter((d) => d.type !== 0)
      .sort((a, b) => a.type - b.type); // 不显示0恢复
  });
  return newData;
};

const userStore = useUserStore();

const timeType = ref(0); // 0: 近7日, 1: 近30日
const changeTime = (type) => {
  timeType.value = type;
  requestData(timeType.value);
};
const alarmInfo = ref(AlarmInfo.safe); // 页面显示信息
const alarmData = reactive<{ selfAlarm: AlarmData[]; teamAlarm: AlarmData[] }>({
  selfAlarm: [
    {
      items: [{ num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }], // items[0] 信息; items[1] 警告; items[2] 次要; items[3] 重要; items[4] 严重
      num: 0,
    },
    {
      items: [{ num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }],
      num: 0,
    },
    {
      items: [{ num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }, { num: 0 }],
      num: 0,
    },
  ],
  teamAlarm: [{ num: 0 }, { num: 0 }, { num: 0 }],
});
const stateMap = ["待处理", "处理中", "已关闭"];
const teamAlarmColorMap = ["yellow", "blue", "gray"];

const requestData = async (timeType) => {
  const duration =
    timeType === 0 ? 3600 * 1000 * 24 * 7 : 3600 * 1000 * 24 * 30;
  const params = reactive({
    startTime: String(dayjs().endOf("date").valueOf() - duration + 1),
    endTime: String(dayjs().endOf("date").valueOf()),
  });
  try {
    await ControlApi.getAlarmOverviewData(params, true).then((res) => {
      alarmData.selfAlarm = pickData(res.items);
    });
    await ControlApi.getAlarmOverviewData(params, false).then((res) => {
      alarmData.teamAlarm = pickData(res.items);
    });
    if (alarmData.selfAlarm[0].num || alarmData.selfAlarm[1].num) {
      alarmInfo.value = AlarmInfo.error;
    } else if (alarmData.teamAlarm[0].num || alarmData.teamAlarm[1].num) {
      alarmInfo.value = AlarmInfo.warn;
    } else {
      alarmInfo.value = AlarmInfo.safe;
    }
  } catch (e) {
    console.error(e);
  }
};

const goAlarmCenter = (type, isSelf: boolean) => {
  const path =
    "/alarm-center/observe-alarm/" + (isSelf ? "self-alarm" : "all-alarm");
  if (!userStore.hasPathAuth(path))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");

  window.open(`${path}?status=${type}`);
};

onMounted(() => {
  requestData(timeType.value);
});
</script>

<style scoped lang="scss">
.time-change {
  padding: 2px;
  background-color: #f1f2f5;
  height: 32px;
  border-radius: 2px;
  span {
    background-color: #f1f2f5;
    display: inline-block;
    height: 28px;
    line-height: 28px;
    width: 60px;
    text-align: center;
    cursor: pointer;
    &.isActive {
      background-color: #fff;
    }
  }
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
}
.alarm-message-box {
  margin: 21px 0 20px;
  height: 87px;
  padding: 20px 0 20px 97px;
  position: relative;
  border-radius: 2px;
  &.red {
    background-color: #ffebeb;
  }
  &.yellow {
    background-color: #fef2e6;
  }
  &.green {
    background-color: #ecfce6;
  }
  img {
    position: absolute;
    width: 120px;
    height: 72px;
    left: 0;
    bottom: 0;
    z-index: 0;
  }
  .title {
    position: relative;
    font-size: 16px;
    font-weight: 500;
    color: #232a35;
  }
  .info {
    position: relative;
    font-size: 13px;
    color: #757f92;
    margin-top: 5px;
  }
}
.alarm-self-data-card {
  height: 160px;
  background-image: url("@/assets/imgs/control/self_alarm_bg.png");
  background-size: 244px 160px;
  background-position: left;
  border-radius: 2px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  &:hover {
    background-position: right;
    .icon-hover {
      display: block;
    }
  }
  .icon-hover {
    display: none;
  }
  .top-content {
    padding: 20px 16px;
    .text {
      color: #596376;
      display: flex;
      align-items: center;
      line-height: 22px;
      height: 22px;
      gap: 6px;
    }
    .number {
      margin-top: 10px;
      font-size: 30px;
      font-weight: 700;
    }
  }
  .detail-numbers {
    padding: 0 12px;
    flex: 1;
    // display: flex;
    // align-items: center;
    text-align: center;
    // justify-content: center;
    gap: 8px;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 0px 0px 4px 4px;
    backdrop-filter: blur(2px);
    .fx-row {
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .detail-number {
      // --red: #fa3946;
      // --yellow: #fad337;
      // --blue: #1f69ff;
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      &.detail-number__5 {
        &:hover {
          color: #fa3946;
        }
        .dot {
          background-color: #fa3946;
        }
      }
      &.detail-number__4 {
        &:hover {
          color: #f4319d;
        }
        .dot {
          background-color: #f4319d;
        }
      }
      &.detail-number__3 {
        &:hover {
          color: #fa830c;
        }
        .dot {
          background-color: #fa830c;
        }
      }
      &.detail-number__2 {
        &:hover {
          color: #f7bb21;
        }
        .dot {
          background-color: #f7bb21;
        }
      }
      &.detail-number__1 {
        &:hover {
          color: #1f69ff;
        }
        .dot {
          background-color: #1f69ff;
        }
      }
    }
  }
}
.alarm-team {
  .alarm-team-title {
    font-size: 16px;
    font-weight: 600;
    color: #232a35;
    margin-bottom: 20px;
  }
  .alarm-team-data-card {
    padding: 12px;
    border: 1px solid #eceef3;
    border-radius: 2px;
    cursor: pointer;
    position: relative;
    &:hover {
      background: #f5f7fc;
      .icon-hover {
        display: block;
      }
    }
    .text {
      font-size: 12px;
      color: #5f7292;
      display: flex;
      align-items: center;
      gap: 7px;
      .dot {
        &.dot-yellow {
          background: #fad337;
        }
        &.dot-blue {
          background: #2353b5;
        }
        &.dot-gray {
          background: #97a8cb;
        }
      }
    }
    .number {
      margin-left: 13px;
      font-size: 18px;
      color: #3c485c;
    }
    .icon-hover {
      display: none;
      position: absolute;
      right: 12px;
      top: 22px;
    }
  }
}
</style>
