<template>
  <el-tabs v-model="activeName" class="monitor-tabs">
    <el-tab-pane label="配置步骤" name="first">
      <div class="step-content">
        <h5 style="margin-bottom: 8px">一、配置步骤</h5>
        <ul>
          <li>1.点击添加订阅进行相关配置，提交成功后会自动进行订阅。</li>
          <li>2.点击取消订阅进行相关配置，能够取消订阅。</li>
        </ul>
        <div class="handle-btns">
          <el-button type="primary" @click="dialogVisible = true">
            添加订阅
          </el-button>
          <el-button @click="cancelSubscribe">取消订阅</el-button>
        </div>
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
    :title="SubscribeType.ADD"
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
});

const cancelSubscribe = () => {
  ElMessageBox.confirm("是否确认取消该订阅？", "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    monitorSysApi.esightCancelSubscribe().then((res) => {
      ElMessage(res);
      // const resParse = JSON.parse(res)
      // console.log(resParse);

      // if (resParse.code === 0) {
      //   ElMessage.success("取消订阅成功");
      // }
    });
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
    monitorSysApi
      .esightSubscribe(subscribeForm)
      .then((res) => {
        ElMessage(res);
        // const resParse = JSON.parse(res)
        // console.log(resParse);
        // if (resParse.code === 0) {
        //   ElMessage.success("订阅成功")
        //   dialogVisible.value = false;
        // }
      })
      .finally(() => {
        loading.value = false;
        dialogVisible.value = false;
      });
  });
};
const rules = {
  uri: [{ required: true, message: "请输入订阅接口", trigger: "blur" }],
  token: [{ required: true, message: "请输入Token", trigger: "blur" }],
};
const activeName = ref("first");
onMounted(() => {
  activeName.value = "first";
  getDataGroupTree();
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
  margin-top: 20px;
}
</style>
