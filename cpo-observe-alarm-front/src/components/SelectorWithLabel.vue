<template>
  <div class="base-selector-with-label">
    <div class="base-selector-label" :class="[size, defaultLabelWidth ? 'default-label-width' : '']">
      <slot name="label"></slot>
    </div>
    <el-select
      :size="size"
      :model-value="modelValue"
      :placeholder="placeholder"
      @change="changeSelected"
      :multiple="multiple"
      :disabled="disabled"
      :style="{ borderRadius }"
      :clearable="clearable"
      :remote="isRemote"
      :reserve-keyword="isRemote"
      :remote-method="remoteMethod"
      :filterable="filterable"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      >
      </el-option>
    </el-select>
  </div>
</template>

<script lang="ts">
export default {
  name: "SelectorWithLabel",
};
</script>

<script lang="ts" setup>
import type { PropType } from "vue";

const emit = defineEmits(["change", "query", "update:modelValue"]);
const props = defineProps({
  options: {
    type: Array as PropType<
      ({
        label: string | number;
        value: string | number;
      } & { disabled?: boolean })[]
    >,
    default: () => [],
  },
  modelValue: {
    type: [Array, String, Number] as PropType<any>,
    default: undefined,
  },
  size: { type: null, default: "default" },
  placeholder: {
    type: [String, undefined] as PropType<string>,
    default: "请选择",
  },
  borderRadius: { type: Number, default: 3 },
  multiple: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  clearable: { type: Boolean, default: false },
  isRemote: { type: Boolean, default: false },
  filterable: { type: Boolean, default: true },
  defaultLabelWidth: { type: Boolean, default: false },
});

function changeSelected(value) {
  emit("update:modelValue", value);
  emit("change", value);
}

function remoteMethod(query: string) {
  if (props.isRemote) {
    emit("query", query);
  }
}

</script>
<style lang="scss" scoped>
.observer-alarm-el-input__inner {
  border-radius: unset !important;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
:deep(.observer-alarm-el-input__inner) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.base-selector-with-label {
  display: flex;
  align-items: center;
  .base-selector-label {
    background-color: #fff;
    font-size: 14px;
    white-space: nowrap;
    height: 32px;
    line-height: 32px;
    border: 1px solid #dcdfe6;
    border-right: 0;
    // display: flex;
    align-items: center;
    padding: 0 12px;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
    &.default-label-width {
      min-width: 80px;
      text-align: center;
    }
  }
  :deep(.observer-alarm-el-select__wrapper) {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
    width: 160px;
    // box-shadow: none;
    // border: 1px solid #dcdfe6;
  }
  .base-selector-label.small {
    height: 24px;
    font-size: 12px;
  }
}
</style>
