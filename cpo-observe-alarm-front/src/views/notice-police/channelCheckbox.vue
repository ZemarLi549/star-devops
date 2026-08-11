<template>
  <div style="display: flex; flex-wrap: wrap">
    <div
      v-for="(item, index) in data"
      :class="[
        'checkbox-card',
        {
          checked: modelValue.indexOf(item.value) > -1,
          disabled: !item.hasConfig || disabled,
        },
      ]"
      :key="index"
      @click="handdleClick(item)"
    >
      <div :class="['observer-alarm-el-checkbox']" style="margin-right: 12px">
        <span
          :class="[
            'observer-alarm-el-checkbox__input',
            'observer-alarm-is-checked',
            {
              'is-checked': modelValue.indexOf(item.value) > -1,
              'is-disabled': !item.hasConfig || disabled,
            },
          ]"
        >
          <span class="observer-alarm-el-checkbox__inner"></span>
        </span>
      </div>
      <div class="checkbox-name">
        <img
          :src="
            getAssetsFile(
              `${
                ChanneMap[item.value]
                  ? ChanneMap[item.value].img_url
                  : ChanneMap['default'].img_url
              }.png`
            )
          "
          alt=""
        />
        <span>{{ item.label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from "vue";
import { ChanneMap } from "./constant";
const props = defineProps({
  modelValue: {
    type: Array,
    default: () => [],
  },
  data: {
    type: Array,
    default: () => [],
  },
  disabled: {
    type: Boolean,
    default: () => false,
  },
});

const val = ref([]);

const getAssetsFile = (url) => {
  return new URL(`../../assets/images/${url}`, import.meta.url).href;
};

watch(
  () => props.modelValue,
  (newVal) => {
    val.value = newVal;
    console.log(props.modelValue, "new", val.value.length);
    console.log("props.data", props.data);
  },
  {
    immediate: true,
    deep: true,
  }
);
const emit = defineEmits(["update:modelValue"]);

const handdleClick = (item) => {
  if (!item.hasConfig || props.disabled) return;
  const index = props.modelValue.indexOf(item.value);
  if (index > -1) {
    // 取消选中
    val.value.splice(index, 1);
  } else {
    // 选中
    val.value.push(item.value);
  }
  emit("update:modelValue", [...val.value]);
};
</script>

<style lang="scss" scope>
.checkbox-card {
  --observer-alarm-el-checkbox-font-size: 14px;
  --observer-alarm-el-checkbox-font-weight: var(
    --observer-alarm-el-font-weight-primary
  );
  --observer-alarm-el-checkbox-text-color: var(
    --observer-alarm-el-text-color-regular
  );
  --observer-alarm-el-checkbox-input-height: 14px;
  --observer-alarm-el-checkbox-input-width: 14px;
  --observer-alarm-el-checkbox-border-radius: 4px;
  --observer-alarm-el-checkbox-bg-color: var(
    --observer-alarm-el-fill-color-blank
  );
  --observer-alarm-el-checkbox-input-border: 1px solid #f0f0f0;
  --observer-alarm-el-checkbox-disabled-border-color: var(
    --observer-alarm-el-border-color
  );
  --observer-alarm-el-checkbox-disabled-input-fill: var(
    --observer-alarm-el-fill-color-light
  );
  --observer-alarm-el-checkbox-disabled-icon-color: var(
    --observer-alarm-el-text-color-placeholder
  );
  --observer-alarm-el-checkbox-disabled-checked-input-fill: var(
    --observer-alarm-el-border-color-extra-light
  );
  --observer-alarm-el-checkbox-disabled-checked-input-border-color: var(
    --observer-alarm-el-border-color
  );
  --observer-alarm-el-checkbox-disabled-checked-icon-color: var(
    --observer-alarm-el-text-color-placeholder
  );
  --observer-alarm-el-checkbox-checked-text-color: var(
    --observer-alarm-el-color-primary
  );
  --observer-alarm-el-checkbox-checked-input-border-color: var(
    --observer-alarm-el-color-primary
  );
  --observer-alarm-el-checkbox-checked-bg-color: var(
    --observer-alarm-el-color-primary
  );
  --observer-alarm-el-checkbox-checked-icon-color: var(
    --observer-alarm-el-color-white
  );
  --observer-alarm-el-checkbox-input-border-color-hover: var(
    --observer-alarm-el-color-primary
  );

  display: flex;
  padding: 14px 16px;
  margin: 8px 16px 8px 0;
  flex-direction: row;
  align-items: center;
  border: var(--observer-alarm-el-checkbox-input-border);
  border-radius: var(--observer-alarm-el-checkbox-border-radius);

  .checkbox-name {
    display: flex;
    align-items: center;
    width: 200px;
    img {
      width: 40px;
      border: 1px solid #f0f0f0;
      border-radius: 4px;
      padding: 6px;
      background-color: #fff;
    }
    span {
      margin-left: 8px;
      color: #262626;
    }
  }
}
.checkbox-card:last-child {
  margin-right: 0;
}
.checkbox-card:hover {
  border-color: var(--observer-alarm-el-checkbox-input-border-color-hover);

  // .observer-alarm-el-checkbox__inner {
  //   border-color: var(--observer-alarm-el-checkbox-input-border-color-hover);

  // }
}
.observer-alarm-el-checkbox__inner:hover {
  border-color: #d9d9d9 !important;
}
.show-check {
  opacity: 1 !important;
}
.hide-check {
  opacity: 0;
}
.checked {
  border-color: var(--observer-alarm-el-checkbox-input-border-color-hover);
  background-color: #f2f7ff;
}
.disabled {
  background-color: #f5f5f5 !important;
  border-color: #d9d9d9 !important;
  cursor: not-allowed;

  .observer-alarm-el-checkbox__inner {
    border-color: #d9d9d9 !important;
    background-color: #f5f5f5 !important;
  }
  .observer-alarm-el-checkbox__input.is-disabled.is-checked
    .observer-alarm-el-checkbox__inner::after {
    border-color: #bfbfbf;
  }
  .checkbox-name {
    img {
      opacity: 0.55;
    }
  }
}
</style>
