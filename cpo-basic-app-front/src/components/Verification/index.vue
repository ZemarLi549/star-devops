<template>
  <el-dialog
    v-model="dialogVisible"
    :destroy-on-close="true"
    :show-close="false"
    width="278px"
    align-center
    class="verification-code-dialog"
    :close-on-click-modal="false"
  >
    <Slider
      ref="slider"
      :dialog-status="dialogVisible"
      @verification-code="handleVerified"
      @close="close"
    />
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";
import Slider from "./slider.vue";
const dialogVisible = ref(false);
// 打开弹窗
const open = (): void => {
  dialogVisible.value = true;
};

const close = (): void => {
  dialogVisible.value = false;
};

const emit = defineEmits(["verified"]);
const handleVerified = (VerifyInfo) => {
  emit("verified", VerifyInfo);
};

const slider = ref();
// 出错
const error = (VerifyInfo) => {
  slider.value.error();
};

defineExpose({
  open,
  close,
  error,
});
</script>
<style lang="scss">
.verification-code-dialog {
  border-radius: 4px;
  overflow: hidden;
  .basic-el-dialog__header {
    display: none;
  }
  .basic-el-dialog__body {
    padding: 0 !important;
  }
}
</style>
