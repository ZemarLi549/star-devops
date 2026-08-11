<template>
  <el-form-item :label="title" class="title">
    <div class="tips">
      <el-icon class="info-filled"><InfoFilled /></el-icon
      >{{
        `多个${
          dataSource === "assign-police"
            ? "分派"
            : props.dataSource === "pushPolice"
            ? "推送"
            : "屏蔽"
        }条件之间是“或”的关系`
      }}
    </div>
    <div
      class="assign-condition"
      v-for="(item, index) in ruleForm.conditions"
      :key="index"
    >
      <el-form-item
        class="condition-rule"
        :prop="`conditions[${index}].dataGroupId`"
        :rules="rules.dataGroupId"
      >
        <el-tree-select
          v-model="item.dataGroupId"
          :data="data"
          :filter-method="filterMethod"
          filterable
          :render-after-expand="false"
          style="width: 240px"
          placeholder="请选择数据单元"
          :disabled="isReadonly"
        >
          <template #default="{ data: { label, iselement } }">
            <img
              src="@/assets/images/shujvdanyuan.png"
              alt=""
              style="width: 20px; vertical-align: middle"
              v-if="iselement"
            />
            <img
              src="@/assets/images/yewuzu.png"
              alt=""
              style="width: 20px; vertical-align: middle"
              v-else
            />
            {{ label }}</template
          >
        </el-tree-select>

        <div class="relation">
          <span>规则之间的关系</span>

          <el-radio-group v-model="item.relation" :disabled="isReadonly">
            <el-radio value="AND">且</el-radio>
            <el-radio value="OR">或</el-radio>
          </el-radio-group>
        </div>
        <span
          class="icon-delete"
          v-if="ruleForm.conditions?.length > 1 && !isReadonly"
          @click="removeCondition(index)"
          ><FontIcon icon="icon-shanchu1"
        /></span>
      </el-form-item>
      <div style="position: relative">
        <template v-if="item.items.length > 1">
          <div class="logic" v-if="item.items.length > 1">
            {{ item.relation === "AND" ? "且" : "或" }}
          </div>
          <div
            class="line"
            v-for="(line, index) in Math.ceil(item.items.length / 2)"
            :style="{
              height: `${48 * (item.items.length - index * 2 - 1)}px`,
            }"
          ></div>
        </template>
        <div
          class="condition-content"
          v-for="(conditionChild, conditionIndex) in item.items"
          :key="conditionIndex"
        >
          <AssignConditionForm
            :index="index"
            :conditionIndex="conditionIndex"
            :conditionChild="conditionChild"
            :nameOption="nameOption"
            :metricsOption="metricsOption"
            :rules="rules"
            :isReadonly="isReadonly"
            @removeRule="removeRule"
          />
        </div>
      </div>
      <el-button
        class="add-rule-btn"
        @click="addRule(index)"
        v-if="!isReadonly"
      >
        <FontIcon icon="icon-xinzeng" />
        添加
      </el-button>
    </div>
    <el-button
      class="add-condition-btn"
      @click="addCondition"
      type="primary"
      v-if="!isReadonly"
      >添加</el-button
    >
    <!-- <div
      class="alarm-polymerization"
      v-if="dataSource === 'assign-police' || false"
    > -->
    <div
      class="alarm-polymerization"
      v-if="props.dataSource === 'assign-police'"
    >
      <el-form-item label="告警聚合" class="alarm-switch">
        <el-switch
          v-model="ruleForm.assignAggregate.aggregate"
          :disabled="isReadonly"
        />

        <!-- <el-radio-group
          v-model="ruleForm.assignAggregate.aggregate"
          style="width: calc(100% - 100px)"
          :disabled="isReadonly"
        >
          <el-radio :value="false">不聚合</el-radio>
          <el-radio :value="true">规则聚合</el-radio>
        </el-radio-group> -->
      </el-form-item>
      <template v-if="ruleForm.assignAggregate.aggregate">
        <el-form-item
          prop="assignAggregate.labelKey"
          :rules="rules.labelKey"
          class="label-key"
          label="聚合规则"
        >
          <el-select
            v-model="ruleForm.assignAggregate.labelKey"
            placeholder="请选择维度"
            :disabled="isReadonly"
          >
            <el-option
              v-for="(dimension, index) in dimensionOption"
              :key="index"
              :label="dimension.title"
              :value="dimension.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="聚合周期" class="period" required>
          <el-input-number
            v-model="ruleForm.assignAggregate.period"
            class="mx-4"
            :min="1"
            :max="60"
            controls-position="right"
            :disabled="isReadonly"
            style="width: 100px"
          />
          <span class="ml-12" style="width: 595px"
            >分钟内将聚合规则下的告警合并在一起</span
          >
        </el-form-item>
      </template>
    </div>
  </el-form-item>
</template>

<script lang="ts" setup>
import { computed, defineProps, onMounted, ref, watch } from "vue";
import assignPolice from "@/request/api/assign-police/index";
import AssignConditionForm from "./AssignConditionForm.vue";
import { InfoFilled } from "@element-plus/icons-vue";

const props = defineProps({
  ruleForm: {
    type: Object,
  },
  dataSource: {
    type: String,
  },
  rules: {
    type: Object,
  },
  dataGroupTree: {
    type: Array,
  },
  nameOption: {
    type: Array,
  },
  metricsOption: {
    type: Array,
  },
  isReadonly: {
    type: Boolean,
  },
});

const emit = defineEmits([
  "addCondition",
  "removeCondition",
  "addRule",
  "removeRule",
]);
const addCondition = () => {
  emit("addCondition");
};
const removeCondition = (index: number) => {
  emit("removeCondition", index);
};
const addRule = (index: number) => {
  emit("addRule", index);
};

const removeRule = (index: number, conditionIndex: number) => {
  emit("removeRule", index, conditionIndex);
};

const title = computed(() => {
  return props.dataSource === "assign-police"
    ? "分派条件"
    : props.dataSource === "pushPolice"
    ? "推送条件"
    : "屏蔽规则";
});

// const getTreeIconName = (iselement: Boolean) => {
//   return iselement ? "icon-shujvdanyuan" : "icon-yewuzu";
// };

const data = ref([]);
const filterMethod = (value) => {
  data.value = [...props.dataGroupTree].filter((item: any) =>
    item.label.includes(value)
  );
};

const dimensionOption = ref([]); //维度
const init = async () => {
  try {
    if (props.dataSource === "assign-police") {
      dimensionOption.value = await assignPolice.getConditionList({});
    }
  } catch (error) {
    console.log(error);
  }
};

onMounted(() => {
  init();
});

watch(
  () => props.dataGroupTree,
  () => {
    data.value = props.dataGroupTree;
  }
);
</script>

<style scoped lang="scss">
.title {
  width: 880px;
  :deep(.observer-alarm-el-form-item__label) {
    font-weight: 600;
  }
  .tips {
    position: absolute;
    top: -32px;
    left: 65px;
    width: 202px;
    height: 24px;
    background: #ebf5ff;
    border-radius: 4px;
    font-size: 12px;
    line-height: 24px;
    text-align: center;
    color: var(--color-primary);
    padding: 0 8px;
    .info-filled {
      position: relative;
      margin-right: 4px;
      top: 1px;
    }
  }
}
.assign-condition {
  background-color: #f2f3f5;
  border-radius: 4px;
  padding: 16px;
  width: 880px;
  margin: 8px 0;
  .condition-rule {
    display: flex;
    width: 100%;
    margin-bottom: 16px !important;
    .relation {
      display: flex;
      align-items: center;
      span {
        margin: 0 24px 0 32px;
      }
      :deep(.observer-alarm-el-radio) {
        margin-right: 20px;
      }
    }
    .icon-delete {
      position: absolute;
      right: 2px;
      cursor: pointer;
    }
  }
  .condition-content {
    display: flex;
    width: 100%;
    margin-bottom: 16px;
    align-items: center;
    padding-left: 25px;
  }
  .add-rule-btn {
    border: 1px dashed #d9d9d9;
    width: 796px;
    margin-left: 24px;
    .icon-xinzeng {
      margin: 0 6px 1px 0;
    }
  }
  .logic {
    width: 18px;
    height: 18px;
    background-color: #fff;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    position: absolute;
    line-height: 18px;
    text-align: center;
    font-size: 12px;
    top: 50%;
    transform: translateY(-50%);
    left: 0;
    z-index: 999;
  }
  .line {
    position: absolute;
    width: 16px;
    border: 1px solid #d9d9d9;
    left: 8px;
    top: 50%;
    transform: translateY(-50%);
    border-right: 0;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
  }
}
.add-condition-btn {
  width: 62px;
  height: 32px;
  margin: 12px 0 16px;
}
.alarm-polymerization {
  background: #f2f3f5;
  border-radius: 4px;
  padding: 16px;
  width: 100%;
  margin-bottom: 40px;
  .alarm-switch {
    display: flex;
    align-items: center;
    :deep(.observer-alarm-el-form-item__label) {
      margin: 0 16px 0 0 !important;
      font-weight: normal;
      padding-right: 0;
    }
    :deep(.observer-alarm-el-radio) {
      margin-right: 24px;
    }
  }
  .label-key {
    width: 50%;
    margin: 20px 0 20px 60px !important;
  }

  .period {
    margin-left: 60px;
    :deep(.observer-alarm-el-input__inner) {
      text-align: left;
    }
  }
}
</style>
