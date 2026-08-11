<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogProps.title"
    :width="560"
    draggable
    align-center
    :destroy-on-close="true"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <!-- 表单内容 -->
    <el-form
      ref="ruleFormRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="旧密码" prop="oldPwd">
        <el-input
          v-model="formData.oldPwd"
          placeholder="请输入旧密码"
          type="password"
          show-password
          maxlength="16"
          @cut.prevent="handleFalse"
          @copy.prevent="handleFalse"
        />
      </el-form-item>
      <el-form-item label="新密码" prop="passwd">
        <el-input
          v-model="formData.passwd"
          placeholder="请输入新密码"
          type="password"
          show-password
          maxlength="16"
          @cut.prevent="handleFalse"
          @copy.prevent="handleFalse"
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="conpasswd">
        <el-input
          v-model="formData.conpasswd"
          placeholder="请确认密码"
          type="password"
          show-password
          maxlength="16"
          @cut.prevent="handleFalse"
          @copy.prevent="handleFalse"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="submitForm(ruleFormRef)"
      >
        保存
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import ControlApi from "@/apis/control";

const ruleFormRef = ref<FormInstance>();

const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;

        if (formData.value?.userId) {
          await ControlApi.updatepasswd({
            ...formData.value,
          });
        }
        loading.value = false;
        ElMessage({
          type: "success",
          message: "操作成功",
          showClose: true,
        });
        dialogVisible.value = false;
        resetForm(formEl);
        emit("refreshList");
      } catch (error) {
        console.log(error);
        loading.value = false;
      }
    }
  });
};

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.resetFields();
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const emit = defineEmits(["refreshList"]);

// dialog框状态
const dialogVisible = ref(false);
const dialogProps = ref({
  title: "",
  rowData: null,
});

const open = (params): void => {
  dialogProps.value = params;
  formData.value = {
    userId: params.rowData.userId,
    oldPwd: "",
    passwd: "",
  };

  dialogVisible.value = true;
};
defineExpose({
  open,
});

const validateOldPwd = (rule, value, cb) => {
  if (!value) {
    return cb(new Error("请输入旧密码"));
  } else {
    cb();
  }
};

const validatePwd = (rule, value, cb) => {
  if (!value) {
    return cb(new Error("请输入新密码"));
  } else if (value == formData.value.oldPwd) {
    return cb(new Error("新密码不能与旧密码一致"));
  } else if (!/^(?=.*[a-zA-Z])(?=.*\d)(?=.*[\W_]).{8,}$/.test(value)) {
    return cb(new Error("以字母开头，由数字、字母和符号组成，至少8位数"));
  } else {
    return cb();
  }
};

function validateConpasswd(rule, value, cb) {
  if (value != formData.value.passwd || !value) {
    cb(new Error("请与新密码保持一致"));
  } else {
    cb();
  }
}

const rules = {
  oldPwd: [{ required: true, validator: validateOldPwd, trigger: "change" }],
  passwd: [{ required: true, validator: validatePwd, trigger: "change" }],
  conpasswd: [
    { required: true, validator: validateConpasswd, trigger: "change" },
  ],
};

const handleFalse = () => {
  return false
}
</script>
