<template>
  <el-tabs v-model="activeName" class="monitor-tabs">
    <el-tab-pane label="配置步骤" name="first">
      <div class="step-content">
        <h3><span>一、配置步骤</span></h3>
        <ul>
          <li>
            1.修改alertmanager模块的配置文件，通过webhook方式通知Cloud
            Alert，编辑告警的配置文件，新增 webhook_configs:及以下内容。
          </li>
        </ul>
        <pre>
      <code class="language-yaml">
receivers:
  - name: 'team-X-pager'
    webhook_configs:
      - url: 'http://api.aiops.com/alarm/api/v1/collect/adapter/prometheus/event/{token}'（保存当前应用，即可获取完整webhook地址信息）
</code>
    </pre>
        <ul>
          <li>2.重新启动 alertmanager 模块，并加载该配置文件。</li>
        </ul>
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
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { ArrowRight } from "@element-plus/icons-vue";
import monitorSysApi from "@/request/api/monitor-system/index";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
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

h3 {
  span {
    font-weight: 500;
  }
}
ul {
  margin: 14px 0;
}
</style>
