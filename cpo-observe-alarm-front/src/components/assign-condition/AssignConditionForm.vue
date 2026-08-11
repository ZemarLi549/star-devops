<template>
  <el-form-item
    :prop="`conditions[${index}].items[${conditionIndex}].name`"
    :rules="rules.name"
  >
    <el-select
      v-model="conditionChild.name"
      placeholder="请选择"
      class="name"
      @change="nameChange"
      :disabled="isReadonly"
    >
      <el-option
        v-for="(item, index) in nameOption"
        :label="item.title"
        :value="item.name"
        :key="index"
      />
    </el-select>
  </el-form-item>

  <el-form-item
    :prop="`conditions[${index}].items[${conditionIndex}].condition`"
    :rules="rules.condition"
    class="condition-form"
  >
    <el-select
      v-model="conditionChild.condition"
      placeholder="请选择"
      class="condition"
      @change="conditionChange"
      :disabled="isReadonly"
    >
      <el-option
        v-for="(item, index) in conditionOption"
        :label="item.decision"
        :value="item.decision"
        :key="index"
      />
    </el-select>
  </el-form-item>

  <el-form-item
    :prop="`conditions[${index}].items[${conditionIndex}].values`"
    :rules="rules.values"
    class="value-form"
  >
    <el-input
      v-model.trim="conditionChild.values[0]"
      placeholder="请输入"
      v-if="valueObj.options.length === 0"
      :disabled="isReadonly"
    />
    <el-select
      v-model="conditionChild.values[0]"
      placeholder="请选择"
      v-else-if="valueObj.options.length && valueObj.isSingle"
      :disabled="isReadonly"
    >
      <el-option
        v-for="(item, index) in valueObj.options"
        :label="item.desc"
        :value="JSON.stringify(item.code)"
        :key="index"
      />
    </el-select>
    <el-select
      v-model="conditionChild.values"
      placeholder="请选择"
      multiple
      :disabled="isReadonly"
      v-else
    >
      <el-option
        v-for="(item, index) in valueObj.options"
        :label="item.desc"
        :value="JSON.stringify(item.code)"
        :key="index"
      />
    </el-select>
  </el-form-item>

  <FontIcon
    icon="icon-jianshao"
    @click="removeRule(index, conditionIndex)"
    v-if="!isReadonly"
  />
</template>

<script lang="ts" setup>
import { defineProps, ref, onMounted, watch } from "vue";
import { deepClone } from "@/utils";
interface NameOption {
  conditionId: number;
  name: string;
  title: string;
  items: any;
}

const props = defineProps({
  index: {
    type: Number,
  },
  conditionIndex: {
    type: Number,
  },
  conditionChild: {
    type: Object,
  },
  metricsOption: {
    type: Array,
  },
  rules: {
    type: Object,
  },
  nameOption: {
    type: Array,
  },
  isReadonly: {
    type: Boolean,
  },
});
const selectName = ref("");

const emit = defineEmits(["removeRule"]);

const removeRule = (index: number, conditionIndex: number) => {
  emit("removeRule", index, conditionIndex);
};

const conditionOption = ref([]);
const valueObj = ref({
  isSingle: true,
  decision: "",
  options: [],
  useRegex: false,
});

const nameChange = (val) => {
  const selectOption = props.nameOption.find(
    (item: NameOption) => item.name === val
  );
  selectName.value = val;
  conditionOption.value = deepClone((selectOption as NameOption)?.items);
  props.conditionChild.condition = "";
  props.conditionChild.values = [];
  valueObj.value.options = [];
};

const conditionChange = (val) => {
  const selectOption = conditionOption.value.find(
    (item) => item.decision === val
  );
  valueObj.value = selectOption;
  getValueObj();

  props.conditionChild.values = [];
};

const getValueObj = () => {
  const metrics: any = props.metricsOption.filter((item: any) => {
    return item.metricName === selectName.value;
  });

  if (selectName.value === "level") {
    valueObj.value.options = metrics[0].metricItems.slice(1, 6);
  } else if (metrics[0]) {
    // 自定义指标, 不在metricsOption中, 因此这里需要进行判断, 默认给空数组
    valueObj.value.options = metrics[0].metricItems;
  } else {
    valueObj.value.options = [];
  }
};

const init = () => {
  const selectOption = props.nameOption.find(
    (item: NameOption) => item.name === props.conditionChild.name
  );
  selectName.value = (selectOption as NameOption)?.name;
  conditionOption.value = deepClone((selectOption as NameOption)?.items);
  if (conditionOption.value) {
    valueObj.value = conditionOption.value.find(
      (item) => item.decision === props.conditionChild.condition
    );
    getValueObj();
  }
};

onMounted(() => {
  init();
});
watch(
  () => props.conditionChild,
  () => {
    init();
  }
);
</script>

<style scoped lang="scss">
.condition-form {
  margin-left: -1px;
}
.name,
.condition {
  width: 220px;
  :deep(.observer-alarm-el-select__wrapper) {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
  }
}
.condition {
  :deep(.observer-alarm-el-select__wrapper) {
    border-radius: 0;
  }
}
.value-form {
  flex: 1;
  margin-left: -1px;
  :deep(.observer-alarm-el-input__wrapper) {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
  :deep(.observer-alarm-el-select__wrapper) {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
}
.icon-jianshao {
  margin-left: 12px;
  color: #8c8c8c;
  cursor: pointer;
}
:deep(.observer-alarm-el-select__wrapper.is-focused) {
  position: relative;
  z-index: 999;
}
:deep(.observer-alarm-el-select__wrapper) {
  &:hover {
    position: relative;
    z-index: 999;
  }
}
</style>
