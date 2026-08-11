<template>
  <el-form ref="labelListFormRef" :model="form" v-loading="loading">
    <div class="label-list-item-wrapper">
      <div
        class="label-list-item"
        v-for="(item, index) in form.labelList"
        :key="item.id"
      >
        <div class="label-base-info">
          <el-form-item
            ref="nameRefs"
            :prop="`labelList.${index}.name`"
            :rules="rules.name"
          >
            <el-input v-model="item.name" placeholder="自定义标签名称" v-trim />
          </el-form-item>
          <el-form-item
            ref="keyRefs"
            :prop="`labelList.${index}.labelKey`"
            :rules="rules.labelKey"
          >
            <el-input
              :disabled="disabledEditKey"
              v-model="item.labelKey"
              placeholder="建议标签Key"
              v-trim
            />
          </el-form-item>
          <el-form-item
            :prop="`labelList.${index}.labelValue`"
            :rules="rules.labelValue"
          >
            <el-input
              v-model="item.labelValue"
              placeholder="标签默认值(非必填）"
              v-trim
            />
          </el-form-item>
          <el-form-item
            style="margin-left: 16px"
            :prop="`labelList.${index}.exp`"
            :rules="rules.exp"
          >
            <el-input
              ref="jsonInputRefs"
              v-model="item.exp"
              placeholder="请输入Json表达式"
              @focus="handleFocus(index)"
              @blur="handleBlur"
              v-trim
            />
          </el-form-item>
          <el-button
            :disabled="
              alarmStore.currentTemplate === '' &&
              alarmStore.currentMonitorType.source !== undefined
            "
            class="valid-btn"
            @click="handleValid(item)"
          >
            校验
          </el-button>
        </div>
        <span
          v-if="form.labelList.length > 1"
          class="delete-btn"
          @click="deleteLabel(index)"
        >
          <FontIcon icon="icon-jianshao" />
        </span>
      </div>
    </div>
    <div class="add-label-btn">
      <el-button v-if="!isEdit" @click="addLabel">
        <FontIcon icon="icon-xinzeng" />
        添加
      </el-button>
    </div>
    <el-form-item class="submit">
      <el-button type="primary" @click="submit(labelListFormRef)">
        确认
      </el-button>
      <el-button @click="() => router.push('/custom-label')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from "vue";
import type { FormRules, FormInstance, FormItemInstance } from "element-plus";
import customLabelApi from "@/request/api/custom-label";
import lodash from "lodash";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useAlarmStore } from "@/stores/modules/alarm";
const alarmStore = useAlarmStore();
const { setGenerateExp } = alarmStore;
const router = useRouter();
const props = defineProps(["isEdit", "labelId"]);
const labelListFormRef = ref<FormInstance>();
const loading = ref(false);
const validateKey = (_, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("请输入标签Key"));
    return;
  }
  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error("标签Key只能包含字母、数字、下划线"));
    return;
  }
  if (form.labelList.filter((item) => item.labelKey === value).length > 1) {
    callback(new Error("内容重复, 请修改"));
    return;
  }
  callback();
};
const validateName = (_, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("请输入标签名称"));
    return;
  }
  if (form.labelList.filter((item) => item.name === value).length > 1) {
    callback(new Error("名称重复, 请修改"));
    return;
  }
  callback();
};
const rules = reactive<FormRules>({
  labelKey: [
    { max: 10, message: "最大输入10字符", trigger: "blur" },
    { validator: validateKey, trigger: "blur" },
  ],
  name: [
    { max: 100, message: "最大输入100字符", trigger: "blur" },
    { validator: validateName, trigger: "blur" },
  ],
  labelValue: [{ max: 100, message: "最大输入100字符", trigger: "blur" }],
  exp: [
    { required: true, trigger: "change", message: "请输入Json表达式" },
    { max: 100, message: "最大输入100字符", trigger: "blur" },
  ],
});
const form = reactive({
  labelList: [
    {
      labelKey: "",
      name: "",
      labelValue: "",
      exp: "",
      id: 0,
    },
  ],
});
const addLabel = () => {
  const id = form.labelList.length
    ? form.labelList[form.labelList.length - 1]?.id + 1
    : 0;
  form.labelList.push({
    labelKey: "",
    name: "",
    labelValue: "",
    exp: "",
    id,
  });
};
const deleteLabel = (index) => {
  form.labelList.splice(index, 1);
};
const submit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid) => {
    if (!valid) return;
    props.isEdit ? submitEdit() : submitAdd();
  });
};
const submitEdit = () => {
  loading.value = true;
  const data = { ...pickData(form.labelList[0]), labelId: props.labelId };
  customLabelApi
    .editCustomLabel(data)
    .then(() => {
      ElMessage.success("编辑成功");
      router.push("/custom-label");
    })
    .finally(() => {
      loading.value = false;
    });
};
const submitAdd = () => {
  loading.value = true;
  const data = { items: form.labelList.map((item) => pickData(item)) };
  customLabelApi
    .addCustomLabel(data)
    .then(async (res) => {
      if (res.success) {
        ElMessage.success("创建成功");
        router.push("/custom-label");
        return;
      }
      ElMessage.error("存在重复的标签或名称");
      res.items.forEach((item, index) => {
        if (item.duplicateName) {
          showError(nameRefs.value[index], "标签名称重复");
        }
        if (item.duplicateKey) {
          showError(keyRefs.value[index], "标签key重复");
        }
      });
    })
    .finally(() => {
      loading.value = false;
    });
};
const handleValid = (item) => {
  if (alarmStore.currentMonitorType.source === undefined) {
    ElMessage.warning("请在右侧选择对应的数据模板");
    return;
  }
  if (!item.exp) {
    ElMessage.warning("请输入Json表达式");
    return;
  }
  alarmStore.setCurrentExp(item.exp);
  alarmStore.validTemplate();
};

const nameRefs = ref([]); // 标签名称对应ref列表
const keyRefs = ref([]); // 标签key对应ref列表
const jsonInputRefs = ref([]); // json对应ref列表

const handleIndex = ref(-1); // 当前正在输入的Json表达式, 用于模板点击提取表达式回显
const handleFocus = (index) => {
  setGenerateExp("");
  handleIndex.value = index;
};

// blur回调函数需要在click回调之后执行
let timer;
const handleBlur = () => {
  timer = setTimeout(() => {
    handleIndex.value = -1; // 重置
    timer && clearTimeout(timer);
  }, 1000);
};

const showError = (el: FormItemInstance, msg) => {
  el.validateState = "error";
  el.validateMessage = msg;
};
const pickData = (labelItemObj) => {
  return lodash.pick(labelItemObj, ["labelKey", "name", "labelValue", "exp"]);
};
const disabledEditKey = ref(false);
onMounted(() => {
  if (props.isEdit) {
    customLabelApi.getCustomLabelDetail(props.labelId).then((res) => {
      form.labelList = [{ ...pickData(res), id: 0 }];
      disabledEditKey.value = res.refer;
    });
  }
});

watch(
  () => alarmStore.generateExp,
  () => {
    if (form.labelList[handleIndex.value] && alarmStore.generateExp) {
      // 点击模板时将数据内容显示在当前输入框中
      jsonInputRefs.value[handleIndex.value].focus();
      form.labelList[handleIndex.value].exp = alarmStore.generateExp;
      timer && clearTimeout(timer);
    }
  }
);
</script>

<style scoped lang="scss">
.label-list-item-wrapper {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 32px;
}

.label-list-item {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;

  .label-base-info {
    flex: 1;
    display: flex;
    align-items: center;

    .observer-alarm-el-form-item {
      &:first-child,
      &:nth-child(2),
      &:nth-child(4) {
        flex-grow: 1;

        :deep(.observer-alarm-el-input__wrapper) {
          border-top-right-radius: 0;
          border-bottom-right-radius: 0;
        }
      }

      &:nth-child(3),
      &:nth-child(2) {
        :deep(.observer-alarm-el-input__wrapper) {
          border-top-left-radius: 0;
          border-bottom-left-radius: 0;
          margin-left: -1px;
        }
      }

      &:nth-child(4) {
        :deep(.observer-alarm-el-input__wrapper) {
          margin-right: -1px;
          // margin-left: 16px;
        }
      }

      &:first-child {
        flex-basis: 144px;
      }

      &:nth-child(2) {
        flex-basis: 120px;
      }

      &:nth-child(3) {
        flex-basis: 160px;
        flex-grow: 0;
      }

      &:nth-child(4) {
        flex-basis: 180px;
      }
    }

    .valid-btn {
      &.observer-alarm-el-button {
        border: 1px solid #1f69ff;
        color: #1f69ff;
        z-index: 999;
      }

      &.observer-alarm-el-button.is-disabled {
        opacity: 0.4;
      }

      &.observer-alarm-el-button {
        border-radius: 0px 4px 4px 0px;
      }
    }
  }

  .delete-btn {
    color: #8c8c8c;
    position: absolute;
    top: 7px;
    right: -26px;
    line-height: 1;
    cursor: pointer;
    // margin-right: 1px;
  }
}

.observer-alarm-el-form-item {
  margin-bottom: 0;
  flex: 1;

  &.is-error {
    z-index: 999;
  }

  :deep(.observer-alarm-el-input__wrapper) {
    &.is-focus,
    &:hover {
      position: relative;
      z-index: 999;
    }
  }

  :deep(
      .observer-alarm-el-input.is-disabled .observer-alarm-el-input__wrapper
    ) {
    box-shadow: 0 0 0 1px #d9d9d9 inset;
    z-index: 0;
  }
}

.add-label-btn {
  width: 100%;
  margin-top: 16px;
  padding-right: 32px;

  button {
    width: 100%;
    border: 1px dashed #d9d9d9;
  }
}

.submit {
  margin-top: 32px;
}
</style>
