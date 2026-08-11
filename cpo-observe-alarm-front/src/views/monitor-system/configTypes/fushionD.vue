<template>
  <el-tabs v-model="activeName" class="monitor-tabs">
    <el-tab-pane label="配置步骤" name="first">
      <div class="step-content">
        <h5 style="margin-bottom: 8px">一、配置步骤</h5>
        <ul>
          <li>1.点击添加订阅进行相关配置。</li>
          <li>
            2.发送订阅内容后可以通过手动获取订阅，获取后可以在下方即时查看。
          </li>
        </ul>
        <div class="handle-btns">
          <el-button type="primary" @click="addSubscribe">添加订阅</el-button>
          <!-- <el-button @click="getSubscribe">获取订阅</el-button> -->
        </div>
        <!-- <div class="subscribe-item-container">
          <div
            class="subscribe-item"
            v-for="item in subscribeList"
            :key="item.id"
          >
            <div class="card-header">
              <img class="icons" :src="subscribeIcon" alt="" />
              ID {{ item.id }}
            </div>
            <div class="card-body">
              <div class="card-body-item">
                <span class="key">端口:</span>
                <span class="value">{{ item.Port }}</span>
              </div>
              <div class="card-body-item">
                <span class="key">IP:</span>
                <span class="value">{{ item.IP }}</span>
              </div>
              <div class="card-body-item">
                <span class="key">用户名:</span>
                <span class="value">{{ item.UserName }}</span>
              </div>
              <div class="card-body-item">
                <span class="key">来源:</span>
                <span class="value">{{ item.Resource }}</span>
              </div>
            </div>
            <div class="card-footer">
              <div class="footer-handle">
                <span @click="editSubscribe(item)">
                  <FontIcon icon="icon-bianji" />
                  编辑
                </span>
              </div>
              <div class="footer-handle">
                <span @click="deleteSubscribe(item.id)">
                  <FontIcon icon="icon-shanchu1" />
                  删除
                </span>
              </div>
            </div>
          </div>
        </div> -->
      </div>
    </el-tab-pane>
    <el-tab-pane label="快捷上报" name="second">
      <div class="step-content">
        <div class="content-text">
          <div>
            针对第三方监控工具的上报，根据用户实际业务的需求，支持两种上报方式，任选其一即可。
          </div>
          <ul class="content-list">
            <div>1、直接使用星迹可观测平台原上报地址</div>
            <li>
              （1）在历史数据单元选择你所需要的数据单元，复制对应的Token后需要您手动复制到数据采集进行接入准备。
            </li>
            <li>（2）您也可以在「业务组」进行创建新的数据单元。</li>
          </ul>
          <ul class="content-list">
            <div>2、支持快捷创建上报数据单元</div>
            <li>
              （1）输入创建数据单元名称，点击快速新建，完成后会自动生成Token。
            </li>
            <li>
              （2）若重复快速创建仅会显示最新的数据单元，历史创建需要到「业务组」查询。
            </li>
            <li>
              （3）复制对应的Token后需要您手动复制到数据采集进行接入准备。
            </li>
          </ul>

          <el-form
            label-position="top"
            label-width="auto"
            :model="formData"
            style="margin-top: 44px"
          >
            <el-form-item label="历史数据单元上报">
              <el-tree-select
                v-model="formData.token"
                :data="data"
                :filter-method="filterMethod"
                filterable
                :render-after-expand="false"
                placeholder="请选择"
                class="content-width"
              >
                <template #default="{ data: { label, iselement } }">
                  <FontIcon :icon="getTreeIconName(iselement)" />
                  {{ label }}
                </template>
              </el-tree-select>
              <el-button type="primary" link @click="jumpTo">
                前往业务组
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </el-form-item>
            <div v-if="formData.token" style="margin-bottom: 29px">
              <el-input
                :value="formData.token"
                disabled
                class="content-width"
              />
              <el-button type="primary" link @click="copyToken(formData.token)">
                复制 Token
              </el-button>
            </div>

            <el-form-item label="快捷创建数据单元">
              <el-input
                v-model="formData.unit"
                placeholder="请输入"
                :maxlength="50"
                class="content-width"
              />
              <el-button type="primary" @click="createToken">
                快捷创建
              </el-button>
            </el-form-item>
            <div v-if="showcutId">
              <el-input :value="showcutId" disabled class="content-width" />
              <el-button type="primary" link @click="copyToken(showcutId)">
                复制 Token
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </el-tab-pane>
  </el-tabs>

  <el-dialog
    v-model="dialogVisible"
    :title="handleType"
    :width="560"
    draggable
    align-center
    :append-to-body="true"
    :destroy-on-close="true"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <!-- 表单内容 -->
    <el-form
      ref="subscribeFormRef"
      :model="subscribeForm"
      :rules="rules"
      label-width="80px"
      label-position="top"
    >
      <el-form-item label="订阅接口" prop="uri">
        <el-input
          v-model="subscribeForm.uri"
          placeholder="请输入"
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="Token" prop="token">
        <el-input v-model="subscribeForm.token" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="subscribeForm.userName" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="subscribeForm.password" placeholder="请输入" />
      </el-form-item>
      <el-form-item label="来源" prop="source">
        <el-input v-model="subscribeForm.source" placeholder="请输入" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="closeDialog">取消</el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="submitForm(subscribeFormRef)"
      >
        确定并发送
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from "vue";
import { ArrowRight } from "@element-plus/icons-vue";
import monitorSysApi from "@/request/api/monitor-system/index";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import { SubscribeType } from "../type";
import subscribeIcon from "@/assets/icons/subscribe.svg";

const { userInfo } = useUserStore();
const { workspace_id } = userInfo;

const dataGroupTree = ref([]);
const getDataGroupTree = async () => {
  const res = await monitorSysApi.getGroupList({
    workSpaceId: workspace_id,
  });
  // 处理业务组树
  handleDataGroupTree(res);
  dataGroupTree.value = res;
};
const handleDataGroupTree = (res) => {
  res.forEach((item) => {
    item.label = item.dataGroupName;
    item.value = item.dataGroupToken;
    item.children = item.child;
    if (item.children.length === 0 && !item.iselement) {
      item.disabled = true;
    }
    if (item.children.length) {
      handleDataGroupTree(item.children);
    }
  });
};
const data = ref([]);
const filterMethod = (value) => {
  data.value = [...dataGroupTree.value].filter((item) =>
    item.label.includes(value)
  );
};
const getTreeIconName = (iselement) => {
  return iselement ? "icon-shujvdanyuan" : "icon-yewuzu";
};

const formData = ref({
  token: undefined,
  unit: "",
});

// 用util里的

const copyToken = (token) => {
  const textarea = document.createElement("textarea");
  textarea.value = token;
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand("copy");
  document.body.removeChild(textarea);
  ElMessage.success("复制成功");
};

const showcutId = ref(null);
const createToken = () => {
  if (!formData.value.unit) {
    return ElMessage.warning("请输入数据单元名称");
  }
  monitorSysApi
    .createToken({
      workSpaceId: workspace_id,
      dataGroupName: formData.value.unit,
    })
    .then((res) => {
      ElMessage.success("创建成功");
      // showcutId.value = res.dataGroupToken || "";
      getDataGroupTree();
    });
};

const jumpTo = () => {
  window.open(location.origin + "/usermanage/space", "_blank");
};

// 订阅逻辑
const dialogVisible = ref(false);
const subscribeFormRef = ref(null);
const loading = ref(false);
const subscribeForm = reactive({
  uri: "",
  token: "",
  userName: "",
  password: "",
  source: "",
});
let currentSubscribeId = "";
const subscribeList = ref([]);
const handleType = ref(SubscribeType.ADD);
const getSubscribe = () => {
  // monitorSysApi.getFdSubscribeList().then((res) => {
  //   subscribeList.value = res
  // });
};
const addSubscribe = () => {
  currentSubscribeId = "";
  handleType.value = SubscribeType.ADD;
  dialogVisible.value = true;
};
const editSubscribe = (item) => {
  currentSubscribeId = item.id;
  subscribeForm.userName = item.UserName;
  subscribeForm.source = item.Resource;
  handleType.value = SubscribeType.EDIT;
  dialogVisible.value = true;
};
const deleteSubscribe = (id) => {
  ElMessageBox.confirm("是否确认删除该订阅？", "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      const res = await monitorSysApi.deleteFdSubscribe(id);
      ElMessage(res);
      // const resParse = JSON.parse(res)
      // ElMessage.success("删除订阅成功");
      // console.log(resParse);
      // if (resParse.code === 0) {
      //   ElMessage.success("删除订阅成功");
      // }
      getSubscribe();
    } catch (e) {
      console.log("删除订阅失败", e);
    }
  });
};
const closeDialog = () => {
  dialogVisible.value = false;
  loading.value = false;
  subscribeFormRef.value.resetFields();
};
const submitForm = (formRef) => {
  formRef.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      const res = await monitorSysApi.fdSubscribe(
        subscribeForm,
        currentSubscribeId
      );
      ElMessage(res);
      // const resParse = JSON.parse(res)
      // console.log(resParse);
      // if (resParse.code === 0) {
      //   currentSubscribeId ? ElMessage.success("编辑订阅成功") : ElMessage.success("添加订阅成功");
      // }
      getSubscribe();
    } finally {
      loading.value = false;
      dialogVisible.value = false;
    }
  });
};
const rules = {
  uri: [{ required: true, message: "请输入订阅接口", trigger: "blur" }],
  token: [{ required: true, message: "请输入Token", trigger: "blur" }],
  userName: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  source: [{ required: true, message: "请输入来源", trigger: "blur" }],
};

const activeName = ref("first");
onMounted(() => {
  activeName.value = "first";
  getDataGroupTree();
  getSubscribe();
});
</script>

<style scoped lang="scss">
.step-content {
  overflow-y: auto; 
  height: calc(100vh - 248px);
  white-space: pre-wrap;
}
.content-text {
  color: #595959;
  max-width: 800px;
}

.content-list {
  margin: 12px 0;
}

.content-width {
  width: 552px;
  margin-right: 16px;
}
.handle-btns {
  margin: 20px 0;
}
.subscribe-item {
  // height: 147px;
  width: 461px;
  display: inline-block;
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 19px 16px 13px;
  &:hover {
    border: 1px solid #1f69ff;
  }
  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    font-weight: 600;
    .icons {
      width: 18px;
      height: 18px;
      margin-right: 10px;
    }
  }
  .card-body {
    font-size: 12px;
    .card-body-item {
      width: 50%;
      display: inline-block;
      margin-bottom: 12px;
      .key {
        color: #8c8c8c;
      }
      .value {
        color: #595959;
        margin-left: 3px;
      }
    }
  }
  .card-footer {
    .footer-handle {
      width: 50%;
      display: inline-block;
      text-align: center;
      color: #595959;
      line-height: 1;
      span {
        cursor: pointer;
      }
      &:hover {
        color: #1f69ff;
      }
      &:first-child {
        border-right: 1px solid #ececec;
      }
    }
  }
}
</style>
