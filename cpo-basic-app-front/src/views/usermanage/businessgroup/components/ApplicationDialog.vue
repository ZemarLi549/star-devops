<template>
  <el-dialog
    :title="title"
    v-model="visible"
    width="480px"
    :close-on-click-modal="false"
  >
    <el-form label-position="top" :model="formData" ref="form" :rules="rules">
      <el-form-item label="应用名称" required prop="applicationName">
        <el-input
          v-model="formData.applicationName"
          placeholder="请输入"
        ></el-input>
        <!-- <div class="tips">创建完成后会自动生成唯一应用ID标识</div> -->
      </el-form-item>
      <!-- <el-form-item
        label="应用ID"
        prop="applicationCode"
        v-if="formData.applicationId"
      >
        <el-tooltip
          class="box-item"
          effect="dark"
          content="用于标记某个业务应用的唯一ID，用户SDK采集数据上传。"
        >
          <el-icon style="position: relative; top: -36px; left: 42px"
            ><InfoFilled
          /></el-icon>
        </el-tooltip>
        <span>{{ formData.applicationCode }}</span>
        <el-icon style="cursor: pointer; margin-left: 6px;" @click="copyToken"
          ><DocumentCopy
        /></el-icon>
      </el-form-item> -->
      <el-form-item label="描述" prop="remark">
        <el-input
          type="textarea"
          v-model="formData.remark"
          placeholder="请输入"
          maxlength="150"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="btnLoading">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import businessGroupApi from "@/apis/usermanage/businessGroup";
import {
  ApplicationDto,
  ApplicationRow,
} from "@/apis/usermanage/businessGroup/types";
import { useAuthStore } from "@/stores/modules/authStore";
import { ElMessage } from "element-plus";
import { FormInstance } from "element-plus";
import { reactive, ref, computed, watch } from "vue";
import { DocumentCopy, InfoFilled } from "@element-plus/icons-vue";
const props = defineProps<{
  dataGroupId: number | string;
}>();
const defaultForm: ApplicationDto = {
  workSpaceId: "",
  dataGroupId: "",
  applicationName: "",
  remark: "",
  applicationId: "",
  applicationCode: "",
};

const formData = reactive<ApplicationDto>({ ...defaultForm });
const validateName = (rule: any, value: any, callback: any) => {
  if (/^[a-zA-Z0-9_-]+$/.test(value)) {
    callback()
  } else {
    callback(new Error('应用名称只能包含数字、字母、_和-'))
  }
}
const rules = {
  applicationName: [
    { required: true, message: "请输入应用名称", trigger: "blur" },
    { validator: validateName, trigger: 'blur' }
  ],
  applicationId: [{ required: true, message: "请输入应用ID", trigger: "blur" }],
};

const visible = ref(false);
const open = (type: string, data?: ApplicationRow) => {
  visible.value = true;
  action.value = type;
  initState(data);
};
const close = () => {
  visible.value = false;
};
const authStore = useAuthStore();
const initState = (data?: ApplicationRow | ApplicationDto) => {
  // 编辑用传入的数据
  const id = data ? data.dataGroupId : props.dataGroupId;
  data = data || defaultForm;
  Object.assign(formData, data, {
    dataGroupId: id,
    workSpaceId: authStore.authSpaceId,
  });
};

defineExpose({
  open,
  close,
});

const action = ref("");
const title = computed(() => {
  return action.value === "create" ? "新建应用" : "编辑应用";
});

const form = ref<FormInstance>();
const btnLoading = ref(false);
const handleConfirm = () => {
  form.value.validate(async (v) => {
    console.log(v);
    if (!v) return;
    btnLoading.value = true;
    const { applicationInsert, applicationUpdate } = businessGroupApi;
    const method =
      action.value === "create" ? applicationInsert : applicationUpdate;
    try {
      await method.bind(businessGroupApi)({ ...formData });
      ElMessage.success("成功");
      emit("success");
      close();
    } catch (error) {
    } finally {
      btnLoading.value = false;
    }
  });
};
const copyToken = () => {
  const textarea = document.createElement("textarea");
  // textarea.value = JSON.stringify(formData.applicationCode);
  textarea.value = formData.applicationCode;
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand("copy");
  document.body.removeChild(textarea);
  ElMessage.success("复制成功");
};

watch(visible, (val) => {
  if (val) return;
  form.value.clearValidate();
});

const emit = defineEmits(["success"]);
</script>
<style lang="scss" scoped>
.el-select {
  flex: 1;
}
.tips {
  font-size: 12px;
  color: #8c8c8c;
  width: 420px;
}
</style>
