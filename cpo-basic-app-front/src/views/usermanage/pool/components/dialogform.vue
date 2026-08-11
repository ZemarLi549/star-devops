<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogProps.title"
    :width="560"
    draggable
    align-center
    :append-to-body="true"
    :destroy-on-close="true"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    custom-class="basic-app-workspace-dialog"
  >
    <!-- 表单内容 -->
    <div v-if="!formData.userId" class="title-tag">
      <el-icon size="21" style="color: #1f69ff"><InfoFilled /></el-icon>
      <span>请提前复制好初始密码，创建完成后发送给用户。</span>
    </div>
    <div class="title-tag secondary-tag">
      <el-icon size="21" style="color: #5b7cff"><InfoFilled /></el-icon>
      <span>
        飞书 user_id 和三方 IT 工作台 ID 可在这里维护。
        LDAP 用户直接使用“登录账号”认证，不需要单独填写 LDAP 账号。
      </span>
    </div>
    <el-form
      ref="ruleFormRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="登录账号" prop="account">
        <el-input
          v-model="formData.account"
          placeholder="请输入登录账号"
          :readonly="!!formData.userId"
        />
      </el-form-item>
      <el-form-item label="用户名" prop="nickName">
        <el-input
          v-model="formData.nickName"
          placeholder="请输入用户名"
          maxlength="40"
        />
      </el-form-item>
      <el-form-item label="联系手机号" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入联系手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱" />
      </el-form-item>
      <div class="form-subtitle">扩展身份信息</div>
      <el-form-item label="飞书 user_id">
        <el-input
          v-model="formData.feishuUserId"
          placeholder="预留飞书用户唯一标识"
        />
      </el-form-item>
      <el-form-item label="三方 IT 工作台 ID">
        <el-input
          v-model="formData.itWorkbenchUserId"
          placeholder="预留三方 IT 工作台用户 ID"
        />
      </el-form-item>
      <el-form-item label="身份来源">
        <el-select
          v-model="formData.identitySource"
          placeholder="请选择身份来源"
          style="width: 100%"
        >
          <el-option label="本地账号" value="LOCAL" />
          <el-option label="LDAP" value="LDAP" />
          <el-option label="外部同步" value="SYNC" />
        </el-select>
      </el-form-item>
      <el-form-item
        label="工作空间"
        prop="workSpaceIdList"
        v-if="dialogProps.key != 'acount'"
      >
        <el-select
          v-model="formData.workSpaceIdList"
          multiple
          placeholder="请选择"
          style="width: 100%"
          :disabled="hideSpace"
        >
          <el-option
            v-for="item in options"
            :key="item.key"
            :label="item.value"
            :value="item.key"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="工作空间" v-if="dialogProps.key == 'acount'">
        <template v-if="dialogProps.workSpaceList.length">
          <div
            v-for="item in dialogProps.workSpaceList"
            class="basic-app-workspace-tag"
          >
            {{ item.workSpaceName }}
          </div>
        </template>
        <div v-else>-</div>
      </el-form-item>
      <el-form-item label="初始密码" v-if="!formData.userId">
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
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref, defineProps } from "vue";
import { ElMessage, FormInstance } from "element-plus";
import PoolApi from "@/apis/usermanage/pool";
import useClipboard from "vue-clipboard3";
import ControlApi from "@/apis/control";

import SpaceApi from "@/apis/usermanage/space";

import UserApi from "@/apis/usermanage/user";

const props = defineProps({
  hideSpace: {
    type: Boolean,
    default: () => false,
  },
});

const ruleFormRef = ref<FormInstance>();

const initpasswd = ref("");
const { toClipboard } = useClipboard();

const options = ref([]);

const formData = ref();
const loading = ref(false);
const buildSubmitPayload = () => {
  const { ldapAccount, ...payload } = formData.value || {};
  return payload;
};

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true;
        const payload = buildSubmitPayload();

        if (dialogProps.value.title == "账号管理") {
          await ControlApi.update({
            ...payload,
          });
        } else {
          if (props.hideSpace) {
            await UserApi.addUser({
              ...payload,
            });
          } else {
            if (formData.value?.userId) {
              await PoolApi.update({
                ...payload,
              });
            } else {
              await PoolApi.add({
                ...payload,
              });
            }
          }
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
  key: "",
  workSpaceList: [],
});

const getCardListData = async () => {
  const res = await SpaceApi.listDrown();
  options.value = res || [];
};

const open = (params): void => {
  dialogProps.value = params;
  formData.value = JSON.parse(
    JSON.stringify(
      params.rowData || {
        account: "",
        nickName: "",
        email: "",
        phone: "",
        feishuUserId: "",
        itWorkbenchUserId: "",
        identitySource: "LOCAL",
        workSpaceIdList: [],
      }
    )
  );

  dialogVisible.value = true;

  if (!formData.value?.userId) {
    PoolApi.getDefault().then((res) => {
      initpasswd.value = atob(res);
    });
  }
  if (params.key != "acount") {
    getCardListData();
  }
};

defineExpose({
  open,
});

const checkNick = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入用户名"));
  } else if (value.length > 30) {
    callback(new Error("用户名过长"));
  } else {
    callback();
  }
};

const checkPhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error("手机号不能为空"));
  } else {
    if (value !== "") {
      const reg = /^1[3-9]\d{9}$/;
      if (!reg.test(value)) {
        callback(new Error("请输入有效的手机号码"));
      }
    }
    callback();
  }
};
const checkEmail = (rule, value, cb) => {
  //验证邮箱的正则表达式
  const regEmail = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
  if (regEmail.test(value)) {
    //合法的邮箱
    return cb();
  }
  cb(new Error("请输入合法的邮箱"));
};

const rules = {
  account: [{ required: true, message: "请输入账号", trigger: "change" }],
  nickName: [{ required: true, validator: checkNick, trigger: "change" }],
  phone: [{ required: true, validator: checkPhone, trigger: "change" }],
  email: [{ required: true, validator: checkEmail, trigger: "change" }],
};

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

<style lang="scss" scoped>
.secondary-tag {
  margin-top: 12px;
  color: #556277;
}

.form-subtitle {
  margin: 6px 0 8px;
  font-size: 14px;
  font-weight: 600;
  color: #21304a;
}
</style>
