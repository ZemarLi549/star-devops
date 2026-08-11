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
      <el-form-item prop="content" class="code-content">
        <Codemirror
          class="code"
          v-model="formData.content"
          :style="{ width: '100%', height: '355px', overflow: 'auto' }"
          :dark="false"
          :lang="json()"
          basic
          wrap
        />
      </el-form-item>
    </el-form>
    <div class="reference-file" @click="goDocument">参考文档</div>
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
import Codemirror from "vue-codemirror6";
import { json } from "@codemirror/lang-json";

import noticeChannelApi from "@/request/api/notice-channel/index";
import { Base64 } from 'js-base64';

const ruleFormRef = ref<FormInstance>();

const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      if (!formData.value.content) {
        return ElMessage({
          type: "warning",
          message: "内容不能为空",
          showClose: true,
        });
      }
      try {
        loading.value = true;
        if (dialogProps.value.title == "编辑模板") {
          await noticeChannelApi.editChannel({
            channel: formData.value.channel,
            // channelId: formData.value.channelId,
            templateContent: formData.value.content,
            config: "",
          });
        } else if (formData.value?.channel && formData.value?.hasConfig) {
          // 修改配置
          await noticeChannelApi.editChannel({
            channel: formData.value.channel,
            // channelId: formData.value.channelId,
            config: Base64.encode(formData.value.content),
            templateContent: "",
          });
        } else if (formData.value?.channel && !formData.value?.hasConfig) {
          // 立即配置，传channel
          await noticeChannelApi.createChannel({
            channel: formData.value.channel,
            config: Base64.encode(formData.value.content),
            templateContent: "",
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
  formData.value = JSON.parse(
    JSON.stringify(
      params.rowData || {
        name: "",
        content: "",
      }
    )
  );

  dialogVisible.value = true;
};

const rules = {
  content: [{ required: false, message: "内容不能为空", trigger: "change" }],
};

const goDocument = () => {
  const data = JSON.parse(JSON.stringify(window.__config__));
  if (!data) return;
  window.open(data["production"].VITE_DOC_URL + "guide/通知方式操作文档.html");
};

defineExpose({
  open,
});
</script>

<style lang="scss" scoped>
:deep(.observer-alarm-el-form-item__content) {
  overflow: auto;
}
.reference-file {
  position: absolute;
  bottom: 28px;
  color: #1f69ff;
  cursor: pointer;
}
</style>
