<template>
  <el-dialog
    v-model="dialogVisible"
    :title="
      (isMulti ? '批量' : '') +
      (handleType === HandleType.ACK ? '认领' : '手动关闭')
    "
    width="480px"
    :close-on-click-modal="false"
    destroy-on-close
    modal-class="self-alarm-dialog"
    @close="closeDialog"
  >
    <!-- <div class="info-box" v-if="showInfo">
      <el-icon>
        <InfoFilled />
      </el-icon>
      <span class="info-box-content">
        部分未识别到启用的分派策略, 仅{{ validCount }}条支持批量处理。
      </span>
    </div> -->
    <el-form
      ref="formRef"
      :model="formData"
      label-position="top"
      :rules="handleType === HandleType.ACK ? null : rules"
      v-loading="loading"
    >
      <el-form-item
        v-if="handleType === HandleType.ACK"
        label="认领描述"
        prop="closeReason"
      >
        <el-input
          v-model="formData.ackDesc"
          placeholder="建议简单描述认领的原因或处理办法"
          type="textarea"
          :rows="4"
        />
      </el-form-item>
      <el-form-item v-else label="关闭原因" prop="closeReason">
        <el-input
          v-model="formData.closeReason"
          placeholder="请输入手动关闭的原因或解决方案"
          type="textarea"
          :rows="4"
        />
      </el-form-item>
    </el-form>
    <el-checkbox v-if="isMulti" v-model="formData.autoClose" label="自动关闭未启用分派策略的告警" size="large" />
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from "vue";
import alarmManageApi from "@/request/api/alarm-management/index.ts";
// import { ElMessage } from "element-plus";
import { HandleType } from "@/views/management/constant";
// import { InfoFilled } from "@element-plus/icons-vue";
import {buildMultiHandleResultMsg, buildHandleResultMsg} from "@/views/management/assist"
const emits = defineEmits(["confirmCallback"]);
const dialogVisible = ref(false);
const loading = ref(false);
const handleType = ref(HandleType.ACK); // 操作类型
const handleTypeText = computed(() => {
  return handleType.value === HandleType.ACK ? "认领" : "关闭";
});
const isMulti = ref(false); // 是否是批量操作
// const showInfo = ref(false);
// const validCount = ref(0);
const formData = reactive({
  alarmIds: [],
  closeReason: "",
  ackDesc: "",
  autoClose: true
});
const rules = {
  closeReason: [{ required: true, message: "请输入关闭原因", trigger: "blur" }],
};
const formRef = ref(null);
const handleConfirm = () => {
  formRef.value.validate(async (v) => {
    if (!v) return;
    loading.value = true;
    try {
      const params = isMulti.value ? {...formData} : {...formData, autoClose: false}
      const res =
        handleType.value === HandleType.ACK
          ? await alarmManageApi.ackAlarms(params)
          : await alarmManageApi.closeAlarms(params);
      const countArr = [0, 0, 0, 0]; // countArr[0] 成功 1 被认领 2 无分派策略 3 已自动关闭
      res.forEach((status) => countArr[status]++);
      isMulti.value ? buildMultiHandleResultMsg(countArr, handleTypeText.value) : buildHandleResultMsg(countArr, handleTypeText.value)
    } finally {
      emits("confirmCallback");
      loading.value = false;
      dialogVisible.value = false;
    }
  });
};
const closeDialog = () => {
  formData.closeReason = "";
  formData.ackDesc = "";
  // showInfo.value = false;
};
const openDialog = (ids, type = HandleType.ACK, multi = false) => {
  formData.alarmIds = ids;
  handleType.value = type;
  isMulti.value = multi;
  formData.autoClose = true
  dialogVisible.value = true;
};
defineExpose({
  openDialog,
});
</script>

<style lang="scss">
.self-alarm-dialog {
  .info-box {
    height: 32px;
    gap: 8px;
    align-items: center;
    display: flex;
    background: #ebf5ff;
    border: 1px solid #99c5ff;
    padding: 0 12px;
    border-radius: 4px;
    margin-bottom: 16px;

    .observer-alarm-el-icon {
      font-size: 17px;
      color: #1f69ff;
    }

    .info-box-content {
      color: #595959;
    }
  }
}
</style>
