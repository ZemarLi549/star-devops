<template>
  <el-form
    ref="ruleFormRef"
    :model="ruleForm"
    :rules="rules"
    label-width="auto"
    class="demo-ruleForm"
    :size="formSize"
    label-position="top"
  >
    <div class="card">
      <div class="mb-24 bold">基本信息</div>
      <div style="display: flex; gap: 16px">
        <el-form-item label="屏蔽规则名称" prop="ruleName">
          <el-input
            v-model.trim="ruleForm.ruleName"
            placeholder="请输入"
            :disabled="isReadonly"
            style="width: 500px"
            maxlength="32"
          />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input
            v-model="ruleForm.remark"
            :placeholder="
              !isReadonly && !ruleForm.remark ? '请输入' : ruleForm.remark
            "
            :disabled="isReadonly"
            style="width: 500px"
            maxlength="256"
          />
        </el-form-item>
      </div>
    </div>
    <div class="card mt-20 config">
      <div class="bold mb-24">配置</div>
      <el-steps direction="vertical">
        <el-step>
          <template v-slot:description>
            <AssignCondition
              :ruleForm="ruleForm"
              dataSource="shieldRule"
              :rules="rules"
              :dataGroupTree="dataGroupTree"
              :nameOption="nameOption"
              :metricsOption="metricsOption"
              :isReadonly="isReadonly"
              @addCondition="addCondition"
              @removeCondition="removeCondition"
              @addRule="addRule"
              @removeRule="removeRule"
            />
          </template>
        </el-step>
        <el-step>
          <template v-slot:description>
            <el-form-item label="屏蔽时间" prop="notifyTime" class="title">
              <el-radio-group
                v-model="ruleForm.notifyTime.type"
                :disabled="isReadonly"
                @change="notifyTimeTypeChange"
              >
                <el-radio :value="0">任何时间</el-radio>
                <el-radio :value="1">工作日时间</el-radio>
                <el-radio :value="2">自定义</el-radio>
              </el-radio-group>
            </el-form-item>
            <div v-if="ruleForm.notifyTime.type === 0" class="any-time">
              周一至周日 7*24小时
            </div>
            <div class="work-time" v-else-if="ruleForm.notifyTime.type === 1">
              周一至周五
              <el-form-item
                prop="notifyTime.startTime"
                class="title"
                style="width: 256px"
                :rules="rules.startTime"
              >
                <el-time-picker
                  v-model="time"
                  format="HH:mm"
                  value-format="HH:mm"
                  is-range
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  :disabled="isReadonly"
                  @change="timeChange"
                />
              </el-form-item>
            </div>
            <div v-else class="custom-time">
              <div class="cycle">
                <span>周期</span>
                <el-form-item
                  prop="notifyTime.dayOfWeek"
                  :rules="rules.dayOfWeek"
                >
                  <el-checkbox-group
                    v-model="ruleForm.notifyTime.dayOfWeek"
                    :disabled="isReadonly"
                  >
                    <el-checkbox label="周一" :value="1" />
                    <el-checkbox label="周二" :value="2" />
                    <el-checkbox label="周三" :value="3" />
                    <el-checkbox label="周四" :value="4" />
                    <el-checkbox label="周五" :value="5" />
                    <el-checkbox label="周六" :value="6" />
                    <el-checkbox label="周日" :value="7" />
                  </el-checkbox-group>
                </el-form-item>
              </div>
              <div class="period">
                <span>时间段</span>
                <el-form-item
                  prop="notifyTime.startTime"
                  class="title"
                  :rules="rules.startTime"
                  style="width: 256px"
                >
                  <el-time-picker
                    v-model="time"
                    is-range
                    format="HH:mm"
                    value-format="HH:mm"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    :disabled="isReadonly"
                    @change="timeChange"
                  />
                </el-form-item>
              </div>
            </div>
          </template>
        </el-step>
      </el-steps>
      <el-form-item class="submit">
        <el-button
          type="primary"
          @click="submitForm(ruleFormRef)"
          v-if="!isReadonly"
        >
          确认
        </el-button>
        <el-button @click="goBack" v-if="!isReadonly">取消</el-button>
        <el-button @click="goEdit" v-if="isReadonly" type="primary"
          >编辑</el-button
        >
      </el-form-item>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, computed, nextTick } from "vue";
import { rules, RuleForm, DataGroupTree } from "./assist.ts";
import AssignCondition from "@/components/assign-condition/AssignCondition.vue";
import type { FormInstance } from "element-plus";
import shieldRule from "@/request/api/shield-rule/index";
import { ElMessage } from "element-plus";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
const { userInfo } = useUserStore();
const { workspace_id } = userInfo;

const router = useRouter();
const route = useRoute();

const formSize = ref("default");
const ruleFormRef = ref<FormInstance>();
const ruleForm = reactive<RuleForm>({
  ruleName: "",
  remark: "",
  conditions: [
    {
      dataGroupId: "",
      items: [],
      relation: "AND",
    },
  ],
  notifyTime: {
    type: 0,
    startTime: "",
    endTime: "",
    dayOfWeek: [],
  },
});
const time = ref<string[]>([]);

const isReadonly = computed(() => route.name === "viewShieldRule");

// 增加条件规则
const addRule = (index: number) => {
  ruleForm.conditions[index].items.push({
    condition: "",
    name: "",
    values: [],
  });
};

// 删除条件规则
const removeRule = (index: number, conditionIndex: number) => {
  const items = ruleForm.conditions[index].items;
  items.splice(conditionIndex, 1);
  ruleForm.conditions[index].items = [];
  nextTick(() => {
    ruleForm.conditions[index].items = items;
  });
};

// 添加分派条件
const addCondition = () => {
  ruleForm.conditions.push({
    dataGroupId: "",
    items: [],
    relation: "AND",
  });
};
const removeCondition = (i: number) => {
  const conditions = ruleForm.conditions;
  ruleForm.conditions.splice(i, 1);
  ruleForm.conditions = [];
  nextTick(() => {
    ruleForm.conditions = conditions;
  });
};

const timeChange = () => {
  if (ruleForm.notifyTime.type === 1 || ruleForm.notifyTime.type === 2) {
    ruleForm.notifyTime.startTime = time.value ? `${time.value[0]}` : undefined;
    ruleForm.notifyTime.endTime = time.value ? `${time.value[1]}` : undefined;
  }
};

const notifyTimeTypeChange = () => {
  ruleForm.notifyTime.dayOfWeek.length = 0;
  ruleForm.notifyTime.startTime = undefined;
  ruleForm.notifyTime.endTime = undefined;
  time.value = [];
};

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      const reqName =
        route.name === "editShieldRule" ? "editShieldRule" : "createShieldRule";
      if (ruleForm.notifyTime.type === 1) {
        ruleForm.notifyTime.dayOfWeek = [1, 2, 3, 4, 5];
      }
      shieldRule[reqName]({
        ...ruleForm,
        ruleId: route.name === "editShieldRule" ? route.params?.id : undefined,
      })
        .then(() => {
          let message = "创建成功";
          if (route.name === "editShieldRule") {
            message = "编辑成功";
          } else if (route.name === "cloneShieldRule") {
            message = "克隆成功";
          }
          ElMessage.success(message);
          router.push("/shield-rule");
        })
        .catch((err) => {
          console.log(err);
        });
    } else {
      console.log("error submit!", fields);
    }
  });
};

const goBack = () => {
  router.push("/shield-rule");
};
const goEdit = () => {
  router.push(`/shield-rule/edit/${route.params.id}`);
};

// 获取业务组树
const dataGroupTree = ref<DataGroupTree[]>([]);
const getDataGroupTree = async () => {
  const res = await shieldRule.getDataGroupTree({
    workSpaceId: workspace_id,
  });
  // 处理业务组树
  handleDataGroupTree(res);
  dataGroupTree.value = res;
};
const handleDataGroupTree = (res: DataGroupTree[]) => {
  res.forEach((item) => {
    item.label = item.dataGroupName;
    item.value = item.dataGroupId;
    item.children = item.child;
    if (item.children.length === 0 && !item.iselement) {
      item.disabled = true;
    }
    if (item.children.length) {
      handleDataGroupTree(item.children);
    }
  });
};

// 获取判断条件
const nameOption = ref([]);
const getConditionList = async () => {
  try {
    nameOption.value = await shieldRule.getConditionList({});
  } catch (error) {
    console.log(error);
  }
};

const metricsOption = ref([]);
const getMetricsList = async () => {
  try {
    metricsOption.value = await shieldRule.getMetricsList();
  } catch (error) {
    console.log(error);
  }
};

const init = async () => {
  try {
    await getDataGroupTree();
    await getConditionList();
    await getMetricsList();
    if (route.name !== "newShieldRule") {
      // 获取详情
      getShieldRuleListDetail(route.params?.id as string);
    }
  } catch (error) {
    console.log(error);
  }
};

const getShieldRuleListDetail = (id: string) => {
  shieldRule
    .shieldRuleListDetail({
      ruleId: id,
    })
    .then((res) => {
      Object.keys(ruleForm).forEach((key) => {
        ruleForm[key] = res[key];
      });
      time.value = [
        res["notifyTime"]["startTime"],
        res["notifyTime"]["endTime"],
      ];
    })
    .catch((err) => {
      console.log(err);
    });
};

onMounted(() => {
  init();
});
</script>

<style lang="scss" scoped>
.demo-ruleForm {
  :deep(.observer-alarm-el-form-item) {
    margin-bottom: 0;
  }
  // :deep(.observer-alarm-el-form-item__label) {
  //   font-weight: bold;
  // }
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
      background-color: #f5f5f5;
      .observer-alarm-el-step__icon-inner {
        font-weight: 500;
        color: #262626;
      }
    }
    .submit {
      margin: 24px 0 0 35px;
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
        gap: 40px;

        span {
          min-width: 28px;
        }
      }
      .cycle {
        height: 28px;
        padding-top: 13px;
        :deep(.observer-alarm-el-form-item__error) {
          position: relative;
        }
      }
      .period {
        height: 32px;
        margin-top: 16px;
        display: flex;
        align-items: center;
        :deep(
            .observer-alarm-el-range-editor.observer-alarm-el-input__wrapper
          ) {
          width: 256px !important;
          margin-left: 32px;
        }
        :deep(.observer-alarm-el-form-item__error) {
          left: 30px;
        }
      }
      // span {
      //   width: 20px;
      // }
    }
    .work-time {
      height: 64px;
      line-height: 64px;
      display: flex;
      align-items: center;
      :deep(.observer-alarm-el-range-editor.observer-alarm-el-input__wrapper) {
        width: 256px;
        margin-left: 32px;
      }
      :deep(.observer-alarm-el-form-item__error) {
        left: 30px;
      }
    }
  }
}
</style>
