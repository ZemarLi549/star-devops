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
      @submit.prevent
    >
      <el-form-item label="空间名称" prop="workSpaceName">
        <el-input
          v-model="formData.workSpaceName"
          placeholder="请输入空间名称"
          maxlength="20"
        />
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入描述"
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
import { ref } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import SpaceApi from "@/apis/usermanage/space";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();

const ruleFormRef = ref<FormInstance>();

const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;

        if (formData.value?.workSpaceId) {
          await SpaceApi.update(
            formData.value.workSpaceId,
            formData.value.workSpaceName,
            formData.value.remark
          );
        } else {
          await SpaceApi.add(
            formData.value.workSpaceName,
            formData.value.remark
          );
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
        try {
          await userStore.updateWorkspaceId();
        } catch (e) {
          console.log(e);
        }
      } catch (error) {
        loading.value = false;
        console.log(error);
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
  formData.value = JSON.parse(
    JSON.stringify(
      params.rowData || {
        workSpaceName: "",

        remark: "",
      }
    )
  );

  dialogVisible.value = true;
};
defineExpose({
  open,
});
const rules = {
  workSpaceName: [
    { required: true, message: "请输入空间名称", trigger: "blur" },
  ],
};
</script>
