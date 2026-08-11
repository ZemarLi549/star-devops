<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogProps.title"
    :width="560"
    align-center
    :append-to-body="true"
    :destroy-on-close="true"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <el-form
      ref="ruleFormRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="分组名称" prop="groupName">
        <el-input
          v-model="formData.groupName"
          placeholder="请输入"
          maxlength="16"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio :value="1">是</el-radio>
          <el-radio :value="0">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          placeholder="请输入"
          type="textarea"
          rows="4"
          maxlength="200"
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
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref } from "vue"
import { ElMessage, FormInstance } from "element-plus"

import noticeGroupApi from '@/request/api/notice-group/index'

const ruleFormRef = ref<FormInstance>();


const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        if(formData.value?.groupId) {
          await noticeGroupApi.editGroup({
            ...formData.value
          })
        } else {
          await noticeGroupApi.createGroup({
            ...formData.value
          })
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
}

const closeDialog = () => {
  dialogVisible.value = false;
}

const emit = defineEmits(["refreshList"])

// dialog框状态
const dialogVisible = ref(false);
const dialogProps = ref({
  title: "",
  rowData: null,
})

const open = (params): void => {
  dialogProps.value = params;
  formData.value = JSON.parse(
    JSON.stringify(
      params.rowData || {
        groupName: '',
        remark: '',
        status: 1
      }
    )
  )

  dialogVisible.value = true;
}

const rules = {
  groupName: [{ required: true, message: "请输入通知组名称", trigger: "change" }]
};

defineExpose({
  open,
})
</script>

<style lang="scss">
</style>
