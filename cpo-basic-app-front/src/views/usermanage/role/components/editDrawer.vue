<template>
  <div>
    <el-drawer
      v-model="drawerVisible"
      :destroy-on-close="true"
      size="900px"
      :title="drawerProps.title"
    >
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        :rules="rules"
        label-width="80px"
        label-position="top"
        :class="{ 'role-form-readonly': readonly }"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <div class="!w-full">
                <el-input
                  v-model.trim="ruleForm.roleName"
                  placeholder="请输入角色名称"
                  clearable
                  :maxlength="64"
                  :disabled="readonly"
                />
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色描述" prop="remark">
              <div class="!w-full">
                <el-input
                  v-model.trim="ruleForm.remark"
                  type="textarea"
                  :placeholder="readonly ? '-' : '请输入角色描述'"
                  clearable
                  :rows="1"
                  :maxlength="50"
                  :show-word-limit="true"
                  :disabled="readonly"
                />
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24" v-if="!readonly">
          <el-col :span="12">
            <el-form-item label="参考角色">
              <el-select
                v-model="roleId"
                placeholder="请选择"
                class="!w-full"
                filterable
                @change="getAuthMenuTree"
                :disabled="readonly"
              >
                <el-option
                  v-for="item in roleList"
                  :key="item.key"
                  :label="item.value"
                  :value="item.key"
                />
              </el-select>
              <div class="el-form-item-msg">
                默认选中参考角色的所有权限点，便于快速配置新角色的权限点
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="" class="base-resource">
          <AuthBox
            ref="authBox"
            :authList="authList"
            :loading="loading.authList"
            :height="treeHeight"
            :readonly="readonly"
            nodeName="menuName"
            nodeId="menuId"
            childrenName="children"
            originType="authRoleMenu"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="loading.submit"
          @click="submitForm(ruleFormRef)"
          v-if="!readonly"
          >确定</el-button
        >
        <el-button
          type="primary"
          @click="startEdit"
          v-if="readonly && !role.isdefault"
          >编辑</el-button
        >
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { ElMessage, FormInstance, FormRules } from "element-plus";
import roleApi from "@/apis/usermanage/role";
import AuthBox from "@/components/AuthBox/index.vue";

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

// drawer框状态
const drawerVisible = ref(false);
const drawerProps = ref<DrawerProps>({
  title: "",
  rowData: null,
});
const treeHeight = ref(window.innerHeight);

// 打开弹窗
const initData = {
  roleName: "",
  remark: "",
};

const readonly = ref(false);

const role = ref();
const ruleForm = ref(JSON.parse(JSON.stringify(initData)));
const open = (params: DrawerProps): void => {
  treeHeight.value = window.innerHeight - 520;
  if (treeHeight.value < 400) {
    treeHeight.value = 400;
  }
  readonly.value = false;
  drawerProps.value = params;
  role.value = params.rowData;
  ruleForm.value = JSON.parse(JSON.stringify(role.value || initData));
  roleId.value = undefined;
  const detail = params.detail;
  if (detail) {
    getRoleDetail();
  } else {
    getAuthMenuTree();
    getAllRoleList(); //获取所有角色模板
  }
  drawerVisible.value = true;
};

const ruleFormRef = ref<FormInstance>();

const rules = reactive<FormRules>({
  roleName: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
  remark: [{ required: true, message: "请输入角色描述", trigger: "blur" }],
});

const authBox = ref();
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate((valid, fields) => {
    if (valid) {
      if (role.value?.roleId) {
        handleEdit();
      } else {
        // 新增
        handleAdd();
      }
    } else {
      console.log("error submit!", fields);
    }
  });
};

// 编辑
const emit = defineEmits(["save"]);
const loading = reactive({
  authList: false,
  submit: false,
});

const handleEdit = () => {
  loading.submit = true;
  const checked = authBox.value!.getChecked();
  roleApi
    .editRole({
      ...ruleForm.value,
      workSpaceId: props.authSpaceId,
      menuIds: checked,
    })
    .then(() => {
      ElMessage.success("编辑成功");
      emit("save");
      drawerVisible.value = false;
    })
    .finally(() => {
      loading.submit = false;
    });
};

// 新增角色
const handleAdd = () => {
  loading.submit = true;
  const checked = authBox.value!.getChecked();
  roleApi
    .addRole({
      ...ruleForm.value,
      workSpaceId: props.authSpaceId,
      menuIds: checked,
    })
    .then(() => {
      ElMessage.success("添加成功");
      emit("save");
      drawerVisible.value = false;
    })
    .catch((err) => {
      console.log(err);
    })
    .finally(() => {
      loading.submit = false;
    });
};

const authList = ref([]); // 权限列表

// 参考角色
const roleId = ref("");
const roleList = ref([]);
const getAllRoleList = () => {
  roleApi
    .getRoleTemplate()
    .then((res: any) => {
      roleList.value = res || [];
    })
    .catch((err) => {
      console.log(err);
    });
};

// 获取权限树请求
const getAuthMenuTree = () => {
  roleApi
    .getAuthMenuTree({
      workSpaceId: props.authSpaceId,
      roleId: roleId.value ? roleId.value : ruleForm.value.roleId,
    })
    .then((res) => {
      // authList.value = formatAuthTree(res || []);
      const menuData = res;
      if (menuData.length) {
        authList.value = menuData;
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

// 格式化权限树
// const formatAuthTree = (list) => {
//   let menuData = list[0].children;
//   if (menuData.length) {
//     // 运行递归过滤函数
//     // menuData = menuData.filter((obj) => {
//     //   if (obj.ismenu !== false) {
//     //     filterObjectsWithIsMenu(obj);
//     //     return true;
//     //   }
//     //   return false;
//     // });
//   }
//   return menuData;
// };

// 递归过滤函数
// function filterObjectsWithIsMenu(obj) {
//   for (let key in obj) {
//     if (Array.isArray(obj[key])) {
//       obj[key] = obj[key].filter((innerObj) => {
//         if (innerObj.ismenu !== false) {
//           if (Object.keys(innerObj).length > 1) {
//             filterObjectsWithIsMenu(innerObj);
//           }
//           return true;
//         }
//         return false;
//       });
//     } else if (typeof obj[key] === "object" && obj[key] !== null) {
//       if (obj[key].ismenu !== false) {
//         if (Object.keys(obj[key]).length > 1) {
//           filterObjectsWithIsMenu(obj[key]);
//         }
//       } else {
//         delete obj[key];
//       }
//     }
//   }
// }

// 获取角色详情
const getRoleDetail = () => {
  roleApi
    .getRoleDetail({
      roleId: ruleForm.value.roleId,
    })
    .then((res) => {
      const menuData = res["menuList"] ? res["menuList"] : [];
      authList.value = menuData;
      readonly.value = true;
      if (readonly.value) {
        treeHeight.value = window.innerHeight - 410;
        if (treeHeight.value < 415) {
          treeHeight.value = 415;
        }
      }
    })
    .catch((err) => {
      console.log(err);
    });
};

// 开启编辑
const startEdit = () => {
  treeHeight.value = window.innerHeight - 520;
  getAuthMenuTree();
  getAllRoleList(); //获取所有角色模板
  readonly.value = false;
};

defineExpose({
  open,
});
</script>
<style lang="scss" scoped>
.el-form-item-msg {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 20px;
  margin-top: 8px;
}
.\!w-full {
  width: 100% !important;
}
.base-resource {
  :deep(.basic-el-form-item__label) {
    margin-top: 8px;
  }
}
:deep(.basic-el-drawer__footer) {
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
:deep(.basic-el-input.is-disabled) {
  .basic-el-input__wrapper {
    box-shadow: none;
    border: none;
    background-color: #fff;
    .basic-el-input__inner {
      color: #262626;
      -webkit-text-fill-color: #262626;
    }
  }
}
:deep(.basic-el-textarea.is-disabled) {
  .basic-el-textarea__inner {
    box-shadow: none;
    border: none;
    background-color: #fff;
    color: #262626;
  }
}
.role-form-readonly {
  :deep(.basic-el-form-item) {
    .basic-el-form-item__label {
      color: #8c8c8c;
    }
    .basic-el-form-item__content {
      .basic-el-input__inner {
        font-weight: 600;
      }
    }
  }
}
:deep(.auth-list) {
  .auth-list-box {
    height: calc(100vh - 500px) !important;
  }
}
:deep(.auth-list-role-readonly) {
  .auth-list-box {
    height: calc(100vh - 390px) !important;
  }
}
</style>
