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
        <el-form-item label="分派策略名称" prop="assignName" >
          <el-input
            v-model.trim="ruleForm.assignName"
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
              dataSource="assign-police"
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
            <el-form-item label="分派通知组" class="group-ids" prop="groupIds">
              <el-select
                v-model="ruleForm.groupIds"
                placeholder="请选择"
                style="width: 50%"
                multiple
                :disabled="isReadonly"
                :clearable="true"
                @clear="notifyClear"
                collapse-tags
                collapse-tags-tooltip
                :max-collapse-tags="1"
              >
                <el-option
                  v-for="(item, index) in notifyOption"
                  :key="index"
                  :label="item.label"
                  :value="item.groupId"
                  :disabled="item.status === 0"
                >
                  <span>{{ item.groupName }}</span>
                  <span
                    v-if="item.status === 0"
                    style="
                      border: 1px solid #d9d9d9;
                      background: #f5f5f5;
                      padding: 0 7px;
                      margin-left: 8px;
                      border-radius: 2px;
                      height: 20px;
                      display: inline-block;
                      line-height: 18px;
                      font-size: 12px;
                    "
                  >
                    已停用</span
                  >
                </el-option>
              </el-select>
              <div class="tips">告警发生后，将会立即通知对应通知组的成员</div>
            </el-form-item>
          </template>
        </el-step>
        <el-step>
          <template v-slot:description>
            <el-form-item label="通知策略" class="group-ids" prop="notifyId">
              <el-select
                v-model="ruleForm.notifyId"
                placeholder="请选择"
                style="width: 50%"
                :disabled="isReadonly"
                :clearable="true"
              >
                <el-option
                  v-for="(item, index) in notifyMethodOption"
                  :key="index"
                  :label="item.label"
                  :value="item.notifyId"
                  :disabled="item.status === 0"
                >
                  <span>{{ item.notifyName }}</span>
                  <span
                    v-if="item.status === 0"
                    style="
                      border: 1px solid #f2f3f5;
                      background: #f2f3f5;
                      padding: 2px 10px 2px 3px;
                      margin-left: 8px;
                      border-radius: 4px;
                    "
                  >
                    已停用</span
                  >
                </el-option>
              </el-select>
              <div class="tips">
                选择预先配置的通知策略，策略包含通知的事件等级、通知方式、时间等
              </div>
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
import { rules, RuleForm } from "./assist.ts";
import AssignCondition from "@/components/assign-condition/AssignCondition.vue";
import type { FormInstance } from "element-plus";
import assignPolice from "@/request/api/assign-police/index";
import { useRouter, useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
const { userInfo } = useUserStore();
const { workspace_id } = userInfo;

const router = useRouter();
const route = useRoute();

const formSize = ref("default");
const ruleFormRef = ref<FormInstance>();
const ruleForm = reactive<RuleForm>({
  assignName: "",
  remark: "",
  groupIds: [],
  notifyId: undefined,
  conditions: [
    {
      dataGroupId: "",
      items: [],
      relation: "AND",
    },
  ],
  assignAggregate: {
    aggregate: false,
    period: 0,
    labelKey: "",
  },
});

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
  const conditions = JSON.parse(JSON.stringify(ruleForm.conditions));
  conditions.splice(i, 1);
  nextTick(() => {
    ruleForm.conditions = conditions;
  });
};

const isReadonly = computed(() => route.name === "viewAssignPolice");

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      const reqName =
        route.name === "editAssignPolice"
          ? "editPoliceListDetail"
          : "createAssignPolice";

      assignPolice[reqName]({
        ...ruleForm,
        assignId:
          route.name === "editAssignPolice" ? route.params?.id : undefined,
      }).then(() => {
        let message = "创建成功";
        if (route.name === "editAssignPolice") {
          message = "编辑成功";
        } else if (route.name === "cloneAssignPolice") {
          message = "克隆成功";
        }
        ElMessage.success(message);
        router.push("/assign-police");
      });
    } else {
      console.log("error submit!", fields);
    }
  });
};

const goBack = () => {
  router.push("/assign-police");
};
const goEdit = () => {
  router.push(`/assign-police/edit/${route.params.id}`);
};

// 获取业务组树
const dataGroupTree = ref([]);
const getDataGroupTree = async () => {
  try {
    const res = await assignPolice.getDataGroupTree({
      workSpaceId: workspace_id,
    });
    // 处理业务组树
    handleDataGroupTree(res);
    dataGroupTree.value = res;
  } catch (error) {
    console.log(error);
  }
};

const handleDataGroupTree = (res: any) => {
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
    nameOption.value = await assignPolice.getConditionList({});
  } catch (error) {
    console.log(error);
  }
};

const metricsOption = ref([]);
const getMetricsList = async () => {
  try {
    metricsOption.value = await assignPolice.getMetricsList();
  } catch (error) {
    console.log(error);
  }
};

const notifyClear = () => {
  ruleForm.groupIds = [];
};

const notifyOption = ref([]);
const notifyMethodOption = ref([]);
const init = async () => {
  Promise.all([
    getDataGroupTree(),
    getConditionList(),
    assignPolice.getNotifyList(),
    assignPolice.getNotifyPolicy(),
    getMetricsList(),
  ])
    .then((res) => {
      // 分派通知组列表
      notifyOption.value = res[2].map((item: any) => {
        return {
          ...item,
          label: `${item.groupName}${item.status === 0 ? "(已停用)" : ""}`,
        };
      });
      // 通知策略列表
      notifyMethodOption.value = res[3].map((item: any) => {
        return {
          ...item,
          label: `${item.notifyName}${item.status === 0 ? "(已停用)" : ""}`,
        };
      });

      if (route.name !== "newAssignPolice") {
        // 获取详情
        getAssignPoliceListDetail(route.params?.id as string);
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

const getAssignPoliceListDetail = (id: string) => {
  assignPolice
    .assignPoliceListDetail({
      assignId: id,
    })
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
  .group-ids {
    margin-bottom: 40px;
    :deep(.observer-alarm-el-form-item__label) {
      font-weight: bold;
      &:before {
        display: none;
      }
    }
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
      margin: 0 0 0 35px;
    }
    .notify-status {
      border-radius: 2px;
      background-color: red;
    }
  }
}
</style>
