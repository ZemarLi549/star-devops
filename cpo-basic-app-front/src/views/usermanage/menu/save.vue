<template>
  <el-card class="save-card" shadow="never">
    <el-container v-loading="loading.form">
      <el-main>
        <el-empty
          v-if="!form.menuId"
          :image="Nodata"
          description="请在「全部」下方进行菜单功能的配置"
          :image-size="145"
          style="margin-top: 20vh"
        />
        <el-row v-else style="flex-wrap: nowrap">
          <el-col :lg="12">
            <el-scrollbar style="max-height: calc(100vh - 250px)">
              <div class="menu-settings-box">
                <h2>{{ form?.menuName || "新增菜单" }}</h2>
                <el-form
                  ref="ruleFormRef"
                  :model="form"
                  :rules="rules"
                  label-width="80px"
                  label-position="top"
                >
                  <el-form-item label="显示名称" prop="menuName">
                    <el-input
                      v-model="form.menuName"
                      clearable
                      placeholder="菜单显示名字"
                    />
                  </el-form-item>
                  <el-form-item
                    label="是否为菜单分组名称"
                    prop="isgroup"
                    required
                  >
                    <el-radio-group
                      v-model="form.isgroup"
                      @change="isGroupChange"
                    >
                      <el-radio :label="true">是</el-radio>
                      <el-radio :label="false">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item
                    label="是否为一级菜单"
                    prop="ismenu"
                    required
                    v-if="!form.isgroup"
                  >
                    <el-radio-group v-model="form.ismenu">
                      <el-radio :label="false">是</el-radio>
                      <el-radio :label="true">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item
                    label="路由是否外链"
                    prop="isoutlink"
                    v-if="form.ismenu && !form.isgroup"
                  >
                    <el-radio-group v-model="form.isoutlink">
                      <el-radio :label="true">是</el-radio>
                      <el-radio :label="false">否</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item
                    label="路由地址"
                    prop="menuPath"
                    v-if="form.ismenu && !form.isgroup"
                  >
                    <el-input
                      v-model.trim="form.menuPath"
                      clearable
                      placeholder="请输入"
                    />
                  </el-form-item>
                  <el-form-item label="菜单图标" prop="icon">
                    <el-popover
                      trigger="click"
                      v-model:visible="iconListVisible"
                      placement="top"
                      :width="300"
                    >
                      <div class="icon-list">
                        <el-button
                          v-for="icon in iconList"
                          :key="icon.value"
                          text
                          style="width: 35px; margin: 0 5px !important"
                          @click="iconSelect(icon.value)"
                        >
                          <FontIcon :icon="icon.value" />
                        </el-button>
                      </div>
                      <div class="text-right">
                        <el-button type="primary" link @click="iconSelect('')">
                          清空
                        </el-button>
                      </div>
                      <template #reference>
                        <el-button style="width: 35px" v-if="form.icon">
                          <FontIcon :icon="form.icon" />
                        </el-button>
                        <el-button
                          style="width: 35px"
                          v-else
                          :icon="Plus"
                        ></el-button>
                      </template>
                    </el-popover>
                  </el-form-item>
                  <el-form-item label="排序" prop="sortNum">
                    <div>
                      <el-input-number
                        v-model="form.sortNum"
                        :min="0"
                        controls-position="right"
                        clearable
                        placeholder="请输入"
                      />
                      <div class="el-form-item-msg">
                        数值越大，排名越靠前；不填则排在后面
                      </div>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
            </el-scrollbar>
          </el-col>
          <el-col :lg="12">
            <el-scrollbar style="max-height: calc(100vh - 250px)">
              <div class="right-box">
                <div class="api-list">
                  <h2>接口列表</h2>
                  <el-form class="list-form">
                    <el-form-item
                      v-for="(api, index) in form.apiList"
                      :key="index"
                    >
                      <div class="interface-list">
                        <el-input
                          v-model.trim="api.path"
                          placeholder="请输入接口地址"
                          style="flex: 1; max-width: 360px"
                        />
                        <el-select
                          v-model="api.method"
                          placeholder="请求方式"
                          clearable
                          style="width: 120px"
                        >
                          <el-option
                            v-for="item in requestMethod"
                            :label="item"
                            :value="item"
                          ></el-option>
                        </el-select>
                        <div style="display: flex; width: 65px">
                          <el-button
                            circle
                            size="small"
                            v-if="index == 0"
                            @click="addUrlList(form.apiList)"
                            :icon="Plus"
                          />
                          <el-button
                            circle
                            size="small"
                            v-if="form.apiList.length > 1"
                            @click="form.apiList.splice(index, 1)"
                            :icon="Minus"
                          />
                        </div>
                      </div>
                    </el-form-item>
                  </el-form>
                </div>
              </div>
            </el-scrollbar>
          </el-col>
        </el-row>
      </el-main>
      <el-footer v-if="form.menuId" class="text-center">
        <el-button type="primary" @click="save" :loading="loading.submit"
          >保存设置</el-button
        >
      </el-footer>
    </el-container>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import { Plus, Minus } from "@element-plus/icons-vue";
import MenuApi from "@/apis/usermanage/menu";

import Nodata from "@/assets/imgs/usermanage/noMenuData.png";

// 图标选择器
const iconList = [
  { label: "", value: "icon-jiaoseguanli" },
  { label: "", value: "icon-yonghuguanli" },
  { label: "", value: "icon-huoyuegaojing" },
  { label: "", value: "icon-yewuzuguanli" },
  { label: "", value: "icon-tongzhimoban" },
  { label: "", value: "icon-tongzhiguize" },
  { label: "", value: "icon-gaojingzu" },
  { label: "", value: "icon-gaojingguize" },
  { label: "", value: "icon-lishigaojing" },
  { label: "", value: "icon-neizhiguize" },
  { label: "", value: "icon-dingyueguize" },
  { label: "", value: "icon-gaojinggailan" },
  { label: "", value: "icon-shixuzhibiao3" },
  { label: "", value: "icon-lianluzhuizong1" },
  { label: "", value: "icon-jichusheshijiance1" },
  { label: "", value: "icon-yingyongxingnengjiance1" },
  { label: "", value: "icon-yibiaopan1" },
  { label: "", value: "icon-rizhijiansuo" },
  { label: "", value: "icon-shujujieru" },
  { label: "", value: "icon-anzhuangheguanli" },
  { label: "", value: "icon-Proxyguanli" },
  { label: "", value: "icon-renwulishi" },
  { label: "", value: "icon-chajiancaozuo" },
  { label: "", value: "icon-Agenthuanjingguanli" },
  { label: "", value: "icon-chajianguanli" },
  { label: "", value: "icon-agentguanli" },
  { label: "", value: "icon-kongjian" },
  { label: "", value: "icon-caidan" },
  { label: "", value: "icon-yonghuchi" },

  { label: "", value: "icon-a-wodegaojingheise" },
  { label: "", value: "icon-suoyougaojing" },
  { label: "", value: "icon-weifenpaigaojing" },
  { label: "", value: "icon-pingbigaojing" },
  { label: "", value: "icon-suoyoutongzhi" },
  { label: "", value: "icon-fenpaicelve" },
  { label: "", value: "icon-pingbiguize" },
  { label: "", value: "icon-tongzhicelve" },
  { label: "", value: "icon-tongzhifangshi" },
  { label: "", value: "icon-tongzhizu" },
  { label: "", value: "icon-jiankongxitong" },
  { label: "", value: "icon-keyongxingjiance" },
  { label: "", value: "icon-gaojingshijian" },
  { label: "", value: "icon-yewufangwenbaobiao" },
  { label: "", value: "icon-Grafanakeshihua" },
  { label: "", value: "icon-gaojinggailan1" },
  { label: "", value: "icon-zidingyibiaoqian" },
  { label: "", value: "icon-gaojingziyu1" },
  { label: "", value: "icon-zuoyeguanli" },
  { label: "", value: "icon-zidongxunjian" },
  { label: "", value: "icon-tuisongcelve" },

  { label: "", value: "icon-yonghufangwenjiankong" },
];

const props = defineProps({
  menu: { type: Object, default: () => {} },
  platform: { type: String, default: "" },
});

const ruleFormRef = ref<FormInstance>();
const form = ref({
  menuId: "",
  parentId: "",
  menuPath: "",
  menuName: "",
  icon: "",
  ismenu: true,
  isgroup: true,
  isoutlink: true,
  // buttonList: [],
  apiList: [],
  sortNum: 0,
  moduleType: "",
});
const menuOptions = ref([]);
const menuProps = reactive({
  value: "menuId",
  label: "menuName",
  checkStrictly: true,
});
const rules = reactive<FormRules>({
  menuName: [{ required: true, message: "请输入显示名称", trigger: "blur" }],
  menuPath: [
    { required: true, message: "请输入路由地址", trigger: "blur" },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value === "") {
          return callback(new Error("请输入路由地址"));
        }
        if (form.value.isoutlink) {
          if (!value.startsWith("http")) {
            callback(new Error("外链请以http开头"));
          } else {
            callback();
          }
        } else {
          if (!value.startsWith("/")) {
            callback(new Error("非外链下路径请以'/'开头"));
          } else {
            callback();
          }
        }
      },
      trigger: "blur",
    },
  ],
  isoutlink: [{ required: true, message: "请选择是否为外链", trigger: "blur" }],
});

const requestMethod = ["GET", "POST", "DELETE", "PUT", "PATCH"];

const loading = reactive({
  form: false,
  submit: false,
});

watch(
  () => props.menu,
  () => {
    menuOptions.value = props.menu[0].children;
  },
  { deep: true }
);

const isGroupChange = () => {
  console.log(form, "form");
};

//保存
const emit = defineEmits(["save"]);
const save = async () => {
  await ruleFormRef.value.validate((valid, fields) => {
    const tempIsoutlink = form.value.isgroup || !form.value.ismenu;
    console.log(form.value);

    if (valid) {
      loading.submit = true;
      MenuApi.update({
        ...form.value,
        // buttonList: form.value.buttonList.filter(
        //   (i) => i.buttonName && i.buttonFlag
        // ),
        // apiList: form.value.apiList.filter((i) => i.url).map((i) => i.url),
        moduleType: props.platform,
        ismenu: form.value.isgroup ? false : form.value.ismenu,
        isoutlink: tempIsoutlink ? false : form.value.isoutlink,
      })
        .then(() => {
          emit("save", form.value);
          ElMessage({
            message: "操作成功",
            type: "success",
            showClose: true,
          });
        })
        .finally(() => {
          loading.submit = false;
        });
    } else {
      console.log("error submit!", fields);
    }
  });
};
//表单注入数据
const setData = (id) => {
  ruleFormRef.value && ruleFormRef.value.clearValidate();
  if (id == "0" || !id) {
    form.value.menuId = "";
  } else {
    // form = data;
    loading.form = true;
    MenuApi.getDetail(id)
      .then((res: any) => {
        // for (const key in res) {
        // if (key == "buttonList") {
        //   res[key] = res[key] || [];
        //   if (!res[key].length) {
        //     addBtnList(res[key]);
        //   }
        // } else if (key == "apiList") {
        //   res[key] = res[key] || [];
        //   if (!res[key].length) {
        //     addUrlList(res[key]);
        //   } else {
        //     res[key] = res[key].map((i) => ({ url: i }));
        //   }
        // }
        // }
        if (!res["apiList"].length) {
          addUrlList(res["apiList"]);
        }
        form.value = res;
      })
      .finally(() => {
        loading.form = false;
      });
  }
};

const addBtnList = (obj) => {
  obj.unshift({
    buttonName: "",
    buttonFlag: "",
  });
};
const addUrlList = (obj) => {
  obj.unshift({
    path: "",
    method: "",
  });
};
// 选择图标
const iconListVisible = ref(false);
const iconSelect = (icon) => {
  form.value.icon = icon;
  iconListVisible.value = false;
};
defineExpose({
  setData,
});
</script>

<style scoped lang="scss">
.save-card {
  border: 0;
  height: 100%;
  :deep(.el-card__body) {
    padding: 0;
    height: 100%;
    .el-container {
      height: 100%;
      .el-footer {
        border-top: 1px solid #eee;
        height: 50px;
        padding-top: 9px;
      }
    }
  }
  .el-form-item-msg {
    font-size: 12px;
    color: var(--el-text-color-regular);
    line-height: 20px;
    margin-top: 2px;
  }
}
h2 {
  font-size: 17px;
  color: #3c4a54;
  padding: 0 0 15px 0;
}
.menu-settings-box {
  padding-right: 20px;
  border-right: 1px solid #eee;
}
.right-box {
  padding-left: 20px;
}

[data-theme="dark"] h2 {
  color: #fff;
}
[data-theme="dark"] .right-box {
  border-color: #434343;
}
.list-form {
  background: var(--el-fill-color-light);
  padding: 12px 12px 5px;
  margin-bottom: 20px;
  .interface-list {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
  }
}
</style>
