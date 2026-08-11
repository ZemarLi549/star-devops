<template>
  <div class="login">
    <div class="login-page">
      <img src="@/assets/brand/logo-light.svg" alt="鑫图平台" />
      <div class="login_panel">
        <div class="login_panel_form">
          <div class="login_panel_form_title">
            <p class="login_panel_form_title_p">您好，欢迎登录！</p>
          </div>
          <el-form
            ref="loginForm"
            class="login-form"
            :model="loginFormData"
            :rules="rules"
            :validate-on-rule-change="false"
            @keyup.enter="submitForm"
            label-position="top"
          >
            <div v-if="loginOptions.length > 1" class="login-mode-switch">
              <el-radio-group v-model="loginMode" size="large">
                <el-radio-button label="LOCAL">本地登录</el-radio-button>
                <el-radio-button label="LDAP">LDAP 登录</el-radio-button>
              </el-radio-group>
            </div>
            <el-form-item prop="username" label="账户名">
              <el-input
                v-model="loginFormData.username"
                size="large"
                :placeholder="loginMode === 'LDAP' ? '请输入 LDAP 账号' : '请输入账户名'"
              />
            </el-form-item>
            <el-form-item prop="password" label="密码">
              <el-input
                v-model="loginFormData.password"
                show-password
                size="large"
                type="password"
                :placeholder="loginMode === 'LDAP' ? '请输入 LDAP 密码' : '请输入密码'"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                class="login-el-button"
                type="primary"
                size="large"
                @click="submitForm"
                >{{ loginMode === "LDAP" ? "LDAP 登 录" : "登 录" }}</el-button
              >
            </el-form-item>
          </el-form>
        </div>
        <!-- <div class="login_panel_right" /> -->
      </div>
    </div>
  </div>
</template>
<script lang="ts">
export default {
  name: "login",
};
</script>
<script lang="ts" setup>
import { reactive, ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user.js";
import userApi from "@/apis/user";
import { useRoute } from "vue-router";

const userStore = useUserStore();

const loginForm = ref(null);
const loginFormData = reactive({
  username: "",
  password: "",
});
const loginMode = ref("LOCAL");
const loginOptions = ref(["LOCAL"]);

onMounted(async () => {
  try {
    const config = await userApi.loginMode();
    loginOptions.value = config?.options?.length ? config.options : ["LOCAL"];
    loginMode.value = loginOptions.value[0];
  } catch (error) {
    loginOptions.value = ["LOCAL"];
    loginMode.value = "LOCAL";
  }
});

const checkUsername = (rule, value, callback) => {
  if (value.length < 1) {
    return callback(new Error("请输入正确的账户名"));
  } else {
    callback();
  }
};
const checkPassword = (rule, value, callback) => {
  if (value.length < 1) {
    return callback(new Error("请输入正确的密码"));
  } else {
    callback();
  }
};

const rules = reactive({
  username: [{ validator: checkUsername, trigger: "blur" }],
  password: [{ validator: checkPassword, trigger: "blur" }],
});

function submitForm() {
  console.log("loginForm", loginForm);
  loginForm.value.validate((v) => {
    if (!v) return;
    submit({});
  });
}

const submit = async (verifyInfo) => {
  const { username, password } = loginFormData;
  try {
    await userStore.login(username, password, verifyInfo, loginMode.value);
    redirect();
  } catch (error) {
    console.log(error);
    // 显示错误消息弹窗
    ElMessage.error(error.errorMessage || '登录失败，请稍后重试');
  }
};
const route = useRoute();
const redirect = () => {
  const path = <string>route.query.redirect;
  if (!path || path.indexOf("login") !== -1)
    return window.history.pushState({}, "", "/");
  window.history.pushState({}, "", path);
};
</script>
<style lang="scss" scoped>
@import "./login.scss";
</style>
