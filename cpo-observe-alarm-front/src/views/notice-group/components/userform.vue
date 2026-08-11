<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogProps.title"
    :width="594"
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
      <el-form-item label="" prop="userIds">
        <el-transfer
          v-model="formData.userIds"
          filterable
          :filter-method="filterMethod"
          filter-placeholder="请输入"
          :titles="['用户', '新添加通知成员']"
          :data="data"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="tips"
        >若没有可添加用户，请前往 「用户权限管理」进行创建</span
      >
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
import { ref, onMounted } from "vue";
import { ElMessage, FormInstance } from "element-plus";

import noticeGroupApi from "@/request/api/notice-group/index";

const ruleFormRef = ref<FormInstance>();

const data = ref([]);
const filterMethod = (query, item) => {
  return item.label.indexOf(query) > -1;
};

const formData = ref();
const loading = ref(false);
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;
        await noticeGroupApi.addUser({
          ...formData.value,
        });
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
        userIds: [],
        groupId: "",
      }
    )
  );

  dialogVisible.value = true;

  getUserList();
};

const getUserList = () => {
  noticeGroupApi.getUsers(formData.value.groupId).then((res) => {
    data.value = (res || []).map((item) => {
      return {
        key: item.userId,
        label: item.nickName,
      };
    });
  });
};

const rules = {
  userIds: [{ required: true, message: "请添加成员", trigger: "change" }],
};

onMounted(() => {});

defineExpose({
  open,
});
</script>

<style lang="scss">
.observer-alarm-el-transfer__buttons {
  position: relative;

  // button:nth-child(1) {
  //   position: absolute;
  //   top: -20px;
  //   left: 8px
  // }

  // button:nth-child(2) {
  //   position: absolute;
  //   top: 20px;
  //   left: 8px;
  //   margin-left: 0
  // }
}
.tips {
  float: left;
  color: #595959;
  line-height: 32px;
}
</style>
