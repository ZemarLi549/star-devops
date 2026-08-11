<template>
  <el-dialog
    :title="title"
    v-model="visible"
    width="480px"
    :close-on-click-modal="false"
  >
    <el-form label-position="top" ref="form" :model="formData" :rules="rules">
      <el-form-item :label="label" required prop="dataGroupName">
        <el-input
          v-model="formData.dataGroupName"
          placeholder="请输入"
        ></el-input>
        <div class="tips">
          {{
            `${
              baseConfig.isElement
                ? "创建完成后会自动生成Token,需要您手动复制到数据采集进行接入准备"
                : "业务分组仅用于管理数据单元层级"
            }`
          }}
        </div>
      </el-form-item>
      <!-- <el-form-item label="token" required>
        <el-input v-model="formData.dataGroupToken" placeholder="请输入"></el-input>
      </el-form-item> -->
      <el-form-item label="上级业务分组" prop="parentId">
        <!-- <el-select v-model="formData.parentId" class="el-select" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select> -->
        <el-tree-select
          v-model="formData.parentId"
          :data="treeData"
          check-strictly
          :render-after-expand="false"
          class="el-select"
          :props="defaultProps"
          node-key="dataGroupId"
        />
        <div class="tips">
          {{
            `${
              baseConfig.isElement
                ? "业务分组仅用于管理数据单元层级，建议根据实际业务需要进行创建接入准备"
                : ""
            }`
          }}
        </div>
      </el-form-item>
      <!-- <el-form-item label="所属工作空间">
        <span>{{ formData.workSpaceId || '--' }}</span>
      </el-form-item> -->
      <el-form-item
        label="Token"
        v-if="baseConfig.type !== 'create' && baseConfig.isElement"
      >
        <el-tooltip
          class="box-item"
          effect="dark"
          content="数据单元是定义数据访问权限的最小隔离级别,数据单元创建后会自动生成唯一Token"
        >
          <span style="position: relative; top: -36px; left: 42px"
            ><FontIcon icon="icon-yiwen"
          /></span>
        </el-tooltip>

        <span>{{ formData.dataGroupToken || "--" }}</span>
        <el-icon style="cursor: pointer; margin-left: 6px;" @click="copyToken"
          ><DocumentCopy
        /></el-icon>
      </el-form-item>
      <el-form-item label="描述" v-if="baseConfig.isElement">
        <el-input
          type="textarea"
          v-model="formData.remark"
          placeholder="请输入"
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
import { useAuthStore } from "@/stores/modules/authStore";
import { ElMessage } from "element-plus";
import { FormInstance } from "element-plus";
import { reactive, ref, computed, watch } from "vue";
import { DocumentCopy, InfoFilled } from "@element-plus/icons-vue";

const defaultForm = {
  dataGroupName: "",
  dataGroupToken: "",
  workSpaceId: "",
  remark: "",
  parentId: "",
  iselement: false, // 是否是数据单元
  workSpaceName: "",
};

const defaultProps = {
  children: "child",
  label: "dataGroupName",
};
const formData = reactive({ ...defaultForm });
const treeData = ref([]);
const baseConfig = ref({
  type: "",
  isElement: false,
  data: null,
});
const visible = ref(false);
const open = (config) => {
  visible.value = true;
  baseConfig.value = config;
  initState(config.data);
  initTree();
};
const authStore = useAuthStore();
const initState = (data) => {
  data = data || defaultForm;
  Object.assign(formData, data, {
    iselement: baseConfig.value.isElement,
    workSpaceId: authStore.authSpaceId,
    workSpaceName: authStore.spaceName,
  });
};

const initTree = async () => {
  const data = await businessGroupApi.bizTree({
    workSpaceId: authStore.authSpaceId,
  });
  treeData.value = data;
};
const close = () => {
  visible.value = false;
};

const actionName = computed(() => {
  const { type, isElement } = baseConfig.value;
  if (isElement) {
    return type === "create" ? "新建" : "编辑";
  } else {
    return type === "create" ? "创建" : "编辑";
  }
});

const targetName = computed(() => {
  const { isElement } = baseConfig.value;
  return isElement ? "数据单元" : "业务分组";
});

const title = computed(() => actionName.value + targetName.value);
const label = computed(() =>
  baseConfig.value.isElement ? "数据单元名称" : "业务分组名称"
);

defineExpose({
  open,
  close,
});
const form = ref<FormInstance>(null);
const handleConfirm = () => {
  form.value.validate().then(() => {
    submit();
  });
};

const btnLoading = ref(false);

const submit = async () => {
  btnLoading.value = true;
  const { dataGroupInsert, dataGroupUpdate } = businessGroupApi;
  const { type } = baseConfig.value;
  const request = type === "create" ? dataGroupInsert : dataGroupUpdate;
  try {
    await request.bind(businessGroupApi)({ ...formData });
    ElMessage.success("提交成功");
    close();
    emit("success", formData, type);
  } catch (error) {
  } finally {
    btnLoading.value = false;
  }
};

const rules = {
  dataGroupName: [{ required: true, message: "请输入名称", trigger: "blur" }],
};

const copyToken = () => {
  const textarea = document.createElement("textarea");
  // textarea.value = JSON.stringify(formData.dataGroupToken);
  textarea.value = formData.dataGroupToken;
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
