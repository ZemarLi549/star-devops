<template>
  <div class="user-drawer">
    <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="900px"
      :title="drawerProps.title"
      @close="cancel"
    >
      <div class="flex items-center" v-if="!user?.userId">
        <el-select
          class="input-with-select"
          style="width: 220px;"
          v-model="addMemberId"
          filterable
          remote
          reserve-keyword
          placeholder="搜索账号或用户名"
          :loading="loading.search"
          @change="addMemberChange"
        >
          <el-option
            v-for="item in searchMemberList"
            :key="item.key"
            :label="item.value"
            :value="item.key"
            :disabled="item.isselect"
          >
            <span style="float: left">{{ item.value }}</span>
            <el-tag
              v-if="item.isselect"
              style="
                font-size: 12px;
                color: #8c8c8c;
                background-color: #f5f5f5;
                margin-left: 8px;
                height: 20px;
                width: 60px;
                border: none;
              "
              >已加入</el-tag
            ></el-option
          >
          <template #footer>
            <div style="text-align: center;">
              <el-button type="text" @click="handleEditMerchant()">新增用户</el-button>
            </div>
          </template>
        </el-select>
        <el-button
          class="input-append-button"
          type="primary"
          @click="handleAddMember"
          :loading="loading.add"
          >选择成员</el-button
        >
        <!-- <el-tooltip>
          <template #content>
            若没有找到对应成员，请前往"用户池"进行新增;若无"用户池"菜单，请联系相关管理员开通使用
          </template>
          <span><FontIcon icon="icon-yiwen" /></span>
        </el-tooltip> -->
      </div>
      <EmptyVue
        v-if="!isDetailShow"
        text="请先在上方选择成员"
        :isNotTableCenter="true"
      />
      <el-form
        label-width="120px"
        v-if="isDetailShow"
        label-position="top"
        :model="ruleForm"
        :rules="rules"
        ref="ruleFormRef"
      >
        <el-row>
          <el-col :span="6">
            <el-form-item label="用户名" prop="nickName">
              {{ addUserDetail.nickName || "--" }}</el-form-item
            >
          </el-col>
          <el-col :span="6">
            <el-form-item label="登录账户" prop="account">
              {{ addUserDetail.account || "--" }}</el-form-item
            >
          </el-col>
          <el-col :span="6">
            <el-form-item label="工作空间" prop="workSpaceName">
              {{ addUserDetail.workSpaceName || "--" }}</el-form-item
            >
          </el-col>
        </el-row>
        <el-row class="role-tree">
          <el-col :span="24" class="role-auth">
            <el-tooltip>
              <template #content>
                不同的角色类型将对应授权不同的服务与菜单功能
              </template>
              <span style="position: absolute; top: 15px; left: 128px">
                <FontIcon icon="icon-yiwen"
              /></span>
            </el-tooltip>
            <el-form-item label="分配角色类型" prop="roleId">
              <el-select
                v-model="ruleForm.roleId"
                placeholder="请选择角色类型"
                filterable
                @change="roleChange"
                :disabled="Boolean(drawerProps?.detail)"
              >
                <el-option
                  v-for="item in roleList"
                  :key="item.key"
                  :label="item.value"
                  :value="item.key"
                />
              </el-select>
            </el-form-item>
            <!-- <div style="margin: 16px 48px 0 0">角色拥有的菜单权限</div> -->
            <AuthBox
              :auth-list="authMenuList"
              :readonly="menuTreeReadonly"
              :height="treeMenuHeight"
              nodeName="menuName"
              nodeId="menuId"
              childrenName="children"
              originType="authUserMenu"
              :isNotCreateForm="Boolean(drawerProps?.rowData)"
            />
          </el-col>
          <!-- <el-col :span="11" class="data-auth">
            <el-form-item label="" prop="dataAuth">
              <AuthBox
                ref="authDataBox"
                :auth-list="authDataList"
                :readonly="dataTreeReadonly"
                :height="treeHeight"
                nodeName="dataGroupName"
                nodeId="dataGroupId"
                childrenName="child"
                originType="authUserData"
                :isNotCreateForm="Boolean(drawerProps?.rowData)"
              />
            </el-form-item>
          </el-col> -->
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="cancel">取消</el-button>
        <el-button
          type="primary"
          :loading="loading.submit"
          @click="submitForm(ruleFormRef)"
          v-if="!drawerProps.detail"
          >{{ user?.userId ? "确认" : "确认添加" }}</el-button
        >
        <el-button
          type="primary"
          @click="submitForm(ruleFormRef, 'next')"
          v-if="!user?.userId"
          >确认并下一个</el-button
        >
        <el-button type="primary" v-if="drawerProps.detail" @click="changeAuth"
          >更改授权</el-button
        >
      </template>
    </el-drawer>

    <DialogForm ref="editDialogRef" :hideSpace="true" @refreshList="cancel" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from "vue";
import { FormInstance, FormRules, ElMessage } from "element-plus";
import AuthBox from "@/components/AuthBox/index.vue";

import userApi from "@/apis/usermanage/user";

import DialogForm from "@/views/usermanage/pool/components/dialogform.vue";

import { useAuthStore } from "@/stores/modules/authStore";

const authStore = useAuthStore();

interface DrawerProps {
  title: string;
  rowData?: any;
  detail?: boolean;
}

const props = defineProps({
  authSpaceId: {
    type: Number,
  },
});

const ruleForm = ref({
  roleId: "",
});
const ruleFormRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  roleId: [{ required: true, message: "请分配角色", trigger: "blur" }],
});

// drawer框状态
const drawerVisible = ref(false);
const drawerProps = ref<DrawerProps>({
  title: "",
  rowData: null,
});
const treeHeight = ref(window.innerHeight - 490);
const treeMenuHeight = ref(window.innerHeight - 490);

const loading = reactive({
  search: false,
  add: false,
  remove: false,
  submit: false,
});

// 参考角色
// const roleId = ref("");
const roleList = ref([]);

const user = ref(); //用户信息
const addUserDetail = ref(); //添加用户的信息
const searchMemberList = ref([]); //添加成员列表
const addMemberId = ref(""); //添加成员id

const isDetailShow = ref(false);

const addMemberChange = () => {
  isDetailShow.value = false;
  addUserDetail.value = "";
  ruleForm.value.roleId = "";
  authMenuList.value = [];
};

const editDialogRef = ref();
const handleEditMerchant = (rowData?) => {
  const params = {
    title: rowData ? "编辑用户" : "新增用户",
    rowData: rowData ? { ...rowData } : {
      account: "",
      nickName: "",
      email: "",
      phone: "",
      workSpaceIdList: [Number(authStore.authSpaceId)]
    }
  };

  editDialogRef.value.open(params);
};

// 成员搜索
const searchMember = async (query: string = undefined) => {
  loading.search = true;
  const res = await userApi.getUserOption({
    workSpaceId: props.authSpaceId,
  });
  searchMemberList.value = res || [];
  loading.search = false;
};

// 添加成员
const handleAddMember = () => {
  if (!addMemberId.value) {
    ElMessage.warning("请搜索并选择要添加的成员");
    return;
  }
  loading.add = true;
  userApi
    .getAddUserDetail({
      workSpaceId: props.authSpaceId,
      userId: user.value?.userId ? user.value?.userId : addMemberId.value,
    })
    .then((res: any) => {
      addUserDetail.value = res;
      if (!user.value?.userId) {
        // ElMessage.success("添加成功");
        // getAuthDataTree(); // 获取数据权限树
      } else {
        nextTick(() => {
          ruleForm.value.roleId = String(res.roleId || "");
          getAuthMenuTree();
        });
        // 只读
        if (drawerProps.value.detail) {
          authDataList.value = res.dataGroupList;
        } else {
          // getAuthDataTree(); // 获取数据权限树
        }
      }
      isDetailShow.value = true;
      getAllRoleList(); //获取角色列表
    })
    .finally(() => {
      loading.add = false;
    });
};

const open = async (params: DrawerProps) => {
  // 计算新增树高度
  treeHeight.value = window.innerHeight - 490;
  treeMenuHeight.value = window.innerHeight - 460;

  if (treeHeight.value < 400) {
    treeHeight.value = 400;
  }
  if (treeMenuHeight.value < 440) {
    treeMenuHeight.value = 440;
  }

  drawerProps.value = params;
  user.value = params.rowData;
  drawerVisible.value = true;
  dataTreeReadonly.value = false;
  await searchMember();
  if (user.value?.userId) {
    addMemberId.value = user.value?.userId;
    handleAddMember();
    if (params.detail) {
      dataTreeReadonly.value = true;
    }
  }
  // 计算编辑详情树高度
  if (drawerProps?.value.rowData) {
    treeHeight.value = window.innerHeight - 410;
    treeMenuHeight.value = window.innerHeight - 380;
    if (treeHeight.value < 420) {
      treeHeight.value = 420;
    }
    if (treeMenuHeight.value < 440) {
      treeMenuHeight.value = 440;
    }
  }
};

const authDataBox = ref();
const submitForm = async (formEl: FormInstance | undefined, flag = "save") => {
  if (!addUserDetail.value)
    return ElMessage.warning("请搜索并选择要添加的成员");
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      // 新增
      handleAdd(flag);
    } else {
      console.log("error submit!", fields);
    }
  });
};

// 编辑
const emit = defineEmits(["save"]);

// 开通
const handleAdd = (flag) => {
  loading.submit = true;
  // const checked = authDataBox.value!.getChecked();
  userApi
    .saveUser({
      workSpaceId: props.authSpaceId,
      userId: addMemberId.value,
      roleId: ruleForm.value.roleId,
      dataGroupIdList: [],
    })
    .then(() => {
      ElMessage.success("保存成功");
      if (flag === "next") {
        isDetailShow.value = false;
        addMemberId.value = "";
        addUserDetail.value = "";
        ruleForm.value.roleId = "";
      } else {
        cancel();
      }
    })
    .finally(() => {
      loading.submit = false;
      searchMember()
    });
};

const authMenuList = ref([]); // 菜单权限列表
const authDataList = ref([]); // 数据权限列表

// 角色
const getAllRoleList = () => {
  userApi
    .getRoleList({
      workSpaceId: props.authSpaceId,
    })
    .then((res: any) => {
      roleList.value = res || [];
    });
};

const roleChange = async () => {
  await ruleFormRef.value.validate((valid, fields) => {
    getAuthMenuTree(); // 获取菜单权限树
  });
};

const menuTreeReadonly = ref(false);
const dataTreeReadonly = ref(false);
const getAuthMenuTree = () => {
  if (ruleForm.value.roleId) {
    userApi
      .getRoleDetail({
        roleId: ruleForm.value.roleId,
      })
      .then((res) => {
        const menuData = res["menuList"] ? res["menuList"] : [];
        menuTreeReadonly.value = true;
        authMenuList.value = menuData;
      })
      .catch((err) => {
        console.log(err);
      });
  } else {
    menuTreeReadonly.value = true;
    authMenuList.value = [];
  }
};

// const getAuthDataTree = () => {
//   userApi
//     .getAuthGroupTree({
//       workSpaceId: props.authSpaceId,
//       userId: addMemberId.value,
//     })
//     .then((res) => {
//       const menuData = res;
//       if (menuData.length) {
//         authDataList.value = menuData;
//       }
//     })
//     .catch((err) => {
//       console.log(err);
//     });
// };

const cancel = () => {
  drawerVisible.value = false;
  isDetailShow.value = false;
  addMemberId.value = "";
  addUserDetail.value = "";

  emit("save");
};

const changeAuth = () => {
  drawerProps.value.detail = false;
  // getAuthDataTree();
  dataTreeReadonly.value = false;
};

defineExpose({
  open,
});
</script>
<style lang="scss" scoped>
.items-center {
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
  margin-bottom: 16px;
}
.el-form-item-msg {
  font-size: 12px;
  color: var(--el-text-color-regular);
  line-height: 20px;
  margin-top: 2px;
}
:deep(.auth-list) {
  .auth-list-box {
    height: calc(100vh - 480px);
  }
}
:deep(.auth-list-user) {
  height: calc(100% - 65px) !important;
  .auth-list-box {
    height: calc(100vh - 445px) !important;
  }
}
:deep(.auth-list-user-menu-readonly) {
  .auth-list-box {
    height: calc(100vh - 405px) !important;
  }
}
:deep(.auth-list-user-data-readonly) {
  .auth-list-box {
    height: calc(100vh - 405px) !important;
  }
}
.input-with-select {
  // :deep(.basic-el-input__wrapper) {
  //   // border-top-right-radius: 0;
  //   // border-bottom-right-radius: 0;
  //   width: 220px;
  // }
}
.input-append-button {
  // border-top-left-radius: 0;
  // border-bottom-left-radius: 0;
  // border-left: 0;
  margin: 0 8px;
}
.role-tree {
  gap: 24px;
  .role-auth {
    position: relative;
    :deep(.basic-el-form-item) {
      padding: 16px 16px 0;
      margin-bottom: 0;
      background-color: #f7f8fa;
      border: 1px solid #ececec;
      border-bottom: none;
      border-radius: 4px;
      border-bottom-left-radius: 0;
      border-bottom-right-radius: 0;
      .basic-el-form-item__label {
        color: #262626;
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
      }
      .basic-el-form-item__content {
        font-weight: 600;
        overflow: visible;
        white-space: wrap;
        display: flex;
        width: 100%;
        .basic-el-select {
          width: 100%;
        }
        .basic-el-form-item__error {
          position: relative;
        }
      }
    }
    // padding: 16px;
    :deep(.auth-list-header) {
      padding: 0 !important;
    }
    :deep(.basic-el-input__wrapper) {
      width: 240px;
    }
  }
  .data-auth {
    // background-color: #f7f8fa;
    // padding: 16px;
    :deep(.auth-list-header) {
      padding: 0 !important;
    }
  }
}
.user-drawer {
  .tag {
    float: right;
    font-size: 12px;
    background: #f5f5f5;
    color: #8c8c8c;
    height: 20px;
    width: 52px;
    border: none;
    margin-top: 8px;
  }
  :deep(.basic-el-form-item) {
    .basic-el-form-item__label {
      color: #8c8c8c;
      margin-bottom: 5px;
    }
    .basic-el-form-item__content {
      font-weight: 600;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      display: block;
    }
  }
}
:deep(.basic-el-drawer__footer) {
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
</style>
