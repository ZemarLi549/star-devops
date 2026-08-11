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
        <el-form-item label="推送策略名称" prop="policyName">
          <el-input
            v-model.trim="ruleForm.policyName"
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
              dataSource="pushPolice"
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
            <el-form-item label="推送接口" prop="policyApi" class="title">
              <el-input
                v-model="ruleForm.policyApi"
                :placeholder="
                  !isReadonly && !ruleForm.policyApi
                    ? '请输入'
                    : ruleForm.policyApi
                "
                :disabled="isReadonly"
                style="width: 500px"
              >
              </el-input>
            </el-form-item>
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
import pushPolice from "@/request/api/push-police/index";
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
  policyName: "",
  remark: "",
  conditions: [
    {
      dataGroupId: "",
      items: [],
      relation: "AND",
    },
  ],
  policyApi: "",
});

const isReadonly = computed(() => route.name === "viewPushPolice");

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

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      const reqName =
        route.name === "editPushPolice" ? "editPushPolice" : "createPushPolice";
      pushPolice[reqName]({
        ...ruleForm,
        id: route.name === "editPushPolice" ? route.params?.id : undefined,
      })
        .then(() => {
          let message = "创建成功";
          if (route.name === "editPushPolice") {
            message = "编辑成功";
          } else if (route.name === "clonePushPolice") {
            message = "克隆成功";
          }
          ElMessage.success(message);
          router.push("/push-police");
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
  router.push("/push-police");
};
const goEdit = () => {
  router.push(`/push-police/edit/${route.params.id}`);
};

// 获取业务组树
const dataGroupTree = ref<DataGroupTree[]>([]);
const getDataGroupTree = async () => {
  const res = await pushPolice.getDataGroupTree({
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
    nameOption.value = await pushPolice.getConditionList({});
  } catch (error) {
    console.log(error);
  }
};

const metricsOption = ref([]);
const getMetricsList = async () => {
  try {
    metricsOption.value = await pushPolice.getMetricsList();
  } catch (error) {
    console.log(error);
  }
};

const init = async () => {
  try {
    await getDataGroupTree();
    await getConditionList();
    await getMetricsList();
    if (route.name !== "newPushPolice") {
      // 获取详情
      getPushPoliceListDetail(route.params?.id as string);
    }
  } catch (error) {
    console.log(error);
  }
};

const getPushPoliceListDetail = (id: string) => {
  pushPolice
    .pushPoliceDetail(id)
    .then((res) => {
      Object.keys(ruleForm).forEach((key) => {
        ruleForm[key] = res[key];
      });
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
