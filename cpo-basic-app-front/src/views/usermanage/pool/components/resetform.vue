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
    <div class="title-tag">
      <el-icon size="21" style="color: #1f69ff"><InfoFilled /></el-icon>
      <span>重置密码后，请将初始密码发送给用户。</span>
    </div>
    <!-- 表单内容 -->
    <el-form
      ref="ruleFormRef"
      :model="formData"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="初始密码">
        <div style="display: flex; width: 100%; justify-content: space-between">
          <el-input
            style="width: 418px"
            :value="initpasswd"
            readonly
            disabled
          ></el-input>
          <el-button type="primary" @click="copypasswd">复制密码</el-button>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="submitForm(ruleFormRef)"
      >
        确认重置密码
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import PoolApi from "@/apis/usermanage/pool";
import useClipboard from "vue-clipboard3";

const ruleFormRef = ref<FormInstance>();

const initpasswd = ref("");
const { toClipboard } = useClipboard();

const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;

        if (formData.value?.userId) {
          await PoolApi.resetpasswd({
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
  };

  dialogVisible.value = true;

  if (formData.value?.userId) {
    PoolApi.getDefault().then((res) => {
      initpasswd.value = atob(res);
    });
  }
};
defineExpose({
  open,
});

const copypasswd = async () => {
  await toClipboard(
    `欢迎使用鑫图平台！\n您的初始登录密码为${initpasswd.value}，请您尽快登录并完成密码修改，感谢使用！`
  );
  ElMessage({
    message: "复制成功",
    type: "success",
    showClose: true,
  });
};
</script>

<style lang="scss" scoped></style>
