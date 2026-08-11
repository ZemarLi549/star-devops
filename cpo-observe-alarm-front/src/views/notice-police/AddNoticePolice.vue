<template>
  <el-form
    ref="ruleFormRef"
    :model="ruleForm"
    :rules="rules"
    label-width="auto"
    class="notice-ruleForm"
    label-position="top"
  >
    <div class="card">
      <div class="mb-24 bold">基本信息</div>
      <el-row>
        <el-col :span="24" style="display: flex">
          <el-form-item label="通知策略名称" prop="notifyName">
            <el-input
              v-model="ruleForm.notifyName"
              placeholder="请输入"
              :disabled="isReadonly"
              maxlength="32"
              style="width: 500px; margin-right: 16px"
            />
          </el-form-item>

          <el-form-item label="描述" prop="remark">
            <el-input
              v-model="ruleForm.remark"
              :placeholder="
                !isReadonly && !ruleForm.remark ? '请输入' : ruleForm.remark
              "
              :disabled="isReadonly"
              maxlength="256"
              style="width: 500px"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div class="card mt-20 config">
      <div class="bold mb-24">配置</div>
      <el-steps direction="vertical">
        <el-step>
          <template v-slot:description>
            <el-form-item label="告警状态" class="title mb-20" prop="step1">
              <el-checkbox-group
                v-model="ruleForm.alarmStatus"
                :disabled="isReadonly"
              >
                <el-checkbox
                  v-for="item in statusOptions"
                  :value="item.value"
                  :key="item.value"
                  >{{ item.label }}</el-checkbox
                >
              </el-checkbox-group>
            </el-form-item>
          </template>
        </el-step>
        <el-step>
          <template v-slot:description>
            <el-form-item label="通知时间" prop="notifyTime" class="title">
              <el-radio-group
                v-model="ruleForm.notifyTime.type"
                :disabled="isReadonly"
                @change="initialVal"
              >
                <el-radio
                  v-for="item in timeOptions"
                  :value="item.value"
                  :key="item.value"
                  >{{ item.label }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
            <div v-if="ruleForm.notifyTime.type == 0" class="any-time">
              周一至周日 7*24小时
            </div>
            <div class="work-time" v-else-if="ruleForm.notifyTime.type == 1">
              周一至周五
              <el-time-picker
                v-model="time"
                format="HH:mm"
                value-format="HH:mm"
                is-range
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                :disabled="isReadonly"
              />
            </div>
            <div v-else class="custom-time">
              <div class="cycle">
                <span>周期</span>
                <el-checkbox-group
                  v-model="ruleForm.notifyTime.dayOfWeek"
                  :disabled="isReadonly"
                >
                  <el-checkbox :value="1">周一</el-checkbox>
                  <el-checkbox :value="2">周二</el-checkbox>
                  <el-checkbox :value="3">周三</el-checkbox>
                  <el-checkbox :value="4">周四</el-checkbox>
                  <el-checkbox :value="5">周五</el-checkbox>
                  <el-checkbox :value="6">周六</el-checkbox>
                  <el-checkbox :value="7">周日</el-checkbox>
                </el-checkbox-group>
              </div>
              <div class="period">
                <span>时间段</span>
                <el-time-picker
                  v-model="time"
                  is-range
                  format="HH:mm"
                  value-format="HH:mm"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  :disabled="isReadonly"
                />
              </div>
            </div>
          </template>
        </el-step>
        <el-step>
          <template v-slot:description>
            <el-form-item label="告警级别" class="title mb-20" prop="step3">
              <el-checkbox-group
                v-model="ruleForm.alarmLevel"
                :disabled="isReadonly"
              >
                <el-checkbox
                  v-for="item in levelOptions"
                  :value="item.value"
                  :key="item.value"
                  :class="['alarm-level', `alarm-color-${item.value}`]"
                  >{{ item.label }}</el-checkbox
                >
              </el-checkbox-group>
            </el-form-item>
          </template>
        </el-step>
        <el-step>
          <template v-slot:description>
            <el-form-item label="通知渠道" class="title" prop="notifyChannels">
              <ChannelCheckbox
                v-model="ruleForm.notifyChannels"
                :data="channelOptions"
                :disabled="isReadonly"
              />
              <div v-if="showChannel" class="tips">
                您还未配置通知渠道，请前往<el-button
                  type="primary"
                  link
                  @click="jumpTo"
                  >通知方式</el-button
                >配置
              </div>
            </el-form-item>
          </template>
        </el-step>
      </el-steps>
      <el-form-item class="submit">
        <el-button type="primary" @click="submitForm(ruleFormRef)">
          {{ submitText }}
        </el-button>
        <el-button v-if="route.name != 'detailNoticePolice'" @click="goBack"
          >取消</el-button
        >
      </el-form-item>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from "vue";
import { statusOptions, timeOptions, levelOptions, rules } from "./constant.ts";
import type { FormInstance } from "element-plus";
import { ElMessage } from "element-plus";
import noticePoliceApi from "@/request/api/notice-police/index";
import { useRouter, useRoute } from "vue-router";

import noticeChannelApi from "@/request/api/notice-channel/index";
import ChannelCheckbox from "./channelCheckbox.vue";

const router = useRouter();
const route = useRoute();

interface RuleForm {
  notifyName: string;
  remark: string;
  alarmStatus: string[] | number[];
  notifyTime: {
    type: string | number;
    dayOfWeek: number[];
    startTime: string | number;
    endTime: string | number;
  };
  alarmLevel: string[] | number[];
  notifyChannels: string[] | number[];
}

const isReadonly = computed(() => route.name === "detailNoticePolice");

const submitText = computed(() => {
  if (route.name == "editNoticePolice") {
    return "确认";
  } else if (route.name == "cloneNoticePolice") {
    return "克隆";
  } else if (route.name == "detailNoticePolice") {
    return "编辑";
  } else {
    return "确认";
  }
});
const ruleFormRef = ref<FormInstance>();
const ruleForm = reactive<RuleForm>({
  notifyName: "",
  remark: "",
  alarmStatus: [],
  notifyTime: {
    type: 0,
    dayOfWeek: [],
    startTime: "",
    endTime: "",
  },
  alarmLevel: [],
  notifyChannels: [],
});
const time = ref([]);
const channelOptions = ref([]);
const showChannel = ref(true);

const initialVal = () => {
  time.value = null;
  ruleForm.notifyTime.dayOfWeek = [];
};

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid) => {
    if (valid) {
      if (
        !ruleForm.alarmStatus.length ||
        !ruleForm.alarmLevel.length ||
        !ruleForm.notifyChannels.length
      ) {
        ElMessage.warning("请选择告警状态、告警级别、通知渠道");
        return false;
      }

      if (time.value && time.value[0]) {
        ruleForm.notifyTime.startTime = time.value[0];
        ruleForm.notifyTime.endTime = time.value[1];
      } else {
        ruleForm.notifyTime.startTime = "";
        ruleForm.notifyTime.endTime = "";
      }

      if (ruleForm.notifyTime.type == 1) {
        ruleForm.notifyTime.dayOfWeek = [1, 2, 3, 4, 5];
      }

      if (ruleForm.notifyTime.type == 1 && !ruleForm.notifyTime.startTime) {
        ElMessage.warning("请选择通知时间时间段");
        return false;
      }
      if (
        ruleForm.notifyTime.type == 2 &&
        (!ruleForm.notifyTime.startTime ||
          !ruleForm.notifyTime.dayOfWeek.length)
      ) {
        ElMessage.warning("请选择通知时间周期和时间段");
        return false;
      }
      if (route.name == "detailNoticePolice") {
        router.push("/notice-police/edit/" + route.params?.id);
      } else if (route.name === "editNoticePolice") {
        // 编辑
        noticePoliceApi
          .editNoticePolice({ ...ruleForm, policyId: route.params?.id })
          .then(() => {
            ElMessage.success("编辑成功");
            router.push("/notice-police");
          });
      } else {
        // 创建或者克隆
        let params: any = {};
        params = { ...ruleForm, policyId: "" };

        delete params?.policyId;
        noticePoliceApi.createNoticePolice(params).then(() => {
          ElMessage.success("操作成功");
          router.push("/notice-police");
        });
      }
    }
  });
};
const goBack = () => {
  router.push("/notice-police");
};
// 表单校验加上
const init = () => {
  try {
    // 获取通知方式
    noticeChannelApi.getNoticeChannel().then((res) => {
      channelOptions.value = res.map((item) => {
        if (item.hasConfig) {
          showChannel.value = false;
        }
        return {
          label: item.channelName,
          value: item.channel,
          hasConfig: item.hasConfig,
        };
      });
    });
    if (route.params?.id) {
      // 获取详情
      getNoticePoliceListDetail(route.params?.id as string);
    }
  } catch (error) {
    console.log(error);
  }
};

const getNoticePoliceListDetail = (id: string) => {
  noticePoliceApi
    .noticePoliceListDetail({
      notifyId: id,
    })
    .then((res) => {
      Object.assign(ruleForm, res);
      console.log(ruleForm, res);
      time.value = [ruleForm.notifyTime.startTime, ruleForm.notifyTime.endTime];
    });
};

const jumpTo = () => {
  window.open(
    location.origin + "/alarm-center/observe-alarm/notice-channel",
    "_blank"
  );
};

onMounted(() => {
  init();
});
</script>

<style lang="scss" scoped>
.notice-ruleForm {
  // height: 100%;
  // overflow-y: auto;
  :deep(.observer-alarm-el-form-item) {
    margin-bottom: 0;
  }
  .mb-20 {
    margin-bottom: 20px;
  }
  .mb-40 {
    margin-bottom: 40px;
  }
  .config {
    .notify {
      width: 266px;
      height: 64px;
      line-height: 64px;
      padding: 0 16px;
      border: 1px solid #e5e5e5;
      border-radius: 4px;
      margin-right: 20px;
      color: #262626;
      display: flex;
      justify-content: space-between;
      align-items: center;
      cursor: pointer;
      &:hover {
        border: 1px solid var(--color-primary);
      }
    }
    .tips {
      color: #595959;
      width: 100%;
      .tips-method {
        color: var(--color-primary);
        cursor: pointer;
        margin: 0 4px;
      }
    }
    :deep(.observer-alarm-el-step__line) {
      width: 1px;
      top: 32px;
      bottom: 8px;
      left: 11px;
      background: #e5e5e5;
    }
    :deep(.observer-alarm-el-step__description) {
      padding-right: 0;
    }
    :deep(.observer-alarm-el-step__icon.is-text) {
      border: 1px solid #f0f0f0;
      border-color: #f0f0f0;
      .observer-alarm-el-step__icon-inner {
        font-weight: 500;
        color: #262626;
      }
    }
    .submit {
      margin: 24px 0 0 35px;
    }
  }
}

.title {
  :deep(.observer-alarm-el-form-item__label) {
    font-weight: 600;
  }
}
.any-time,
.custom-time,
.work-time {
  background-color: #f2f3f5;
  margin-top: 13px;
  padding-left: 24px;
  color: #595959;
  width: 880px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.any-time {
  height: 32px;
  line-height: 32px;
}
.custom-time {
  height: 95px;
  // line-height: 95px;
  .cycle {
    display: flex;
    align-items: center;
    gap: 32px;

    span {
      min-width: 28px;
    }
  }
  .cycle {
    height: 28px;
    padding-top: 13px;
  }
  .period {
    height: 32px;
    margin-top: 16px;
    :deep(.observer-alarm-el-range-editor.observer-alarm-el-input__wrapper) {
      width: 256px !important;
      margin-left: 32px;
    }
  }
  // span {
  //   width: 20px;
  // }
}
.work-time {
  height: 64px;
  line-height: 64px;
  :deep(.observer-alarm-el-range-editor.observer-alarm-el-input__wrapper) {
    width: 256px;
    margin-left: 32px;
  }
}

.alarm-color-5 {
  --border-color: #ffb8b5;
  --bg-color: #fff1f0;
  --color: #fa3946;
}
.alarm-color-4 {
  --border-color: #f4319d;
  --bg-color: #ffe8f1;
  --color: #f4319d;
}
.alarm-color-3 {
  --border-color: #ffcf87;
  --bg-color: #fff7e6;
  --color: #fa830c;
}
.alarm-color-2 {
  --border-color: #ffb8b5;
  --bg-color: #fefce8;
  --color: #f7bb21;
}
.alarm-color-1 {
  --border-color: #99c5ff;
  --bg-color: #ebf5ff;
  --color: #1f69ff;
}

.alarm-level {
  :deep(.observer-alarm-el-checkbox__label) {
    padding: 6px 8px;
    color: var(--color) !important;
    background: var(--bg-color);
    border: 0.5px solid var(--border-color);
    border-radius: 2px;
    margin-left: 8px;
  }
}
</style>
