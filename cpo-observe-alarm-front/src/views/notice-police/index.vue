<template>
  <div class="card">
    <div class="header">
      <div class="form">
        <el-input
          v-model="form.policyName"
          style="width: 240px"
          placeholder="搜索通知策略名称"
          :prefix-icon="Search"
          @input="debounceFunc"
          clearable
        />

        <SelectorWithLabel
          placeholder="请选择"
          :options="channelOptions"
          v-model="form.notifyChannel"
          @change="searchChannel"
          clearable
          defaultLabelWidth
        >
          <template #label>通知渠道</template>
        </SelectorWithLabel>
      </div>
      <el-button type="primary" class="btn" @click="createNoticePolice"
        >新建通知策略</el-button
      >
    </div>
    <el-table :data="tableData" style="width: 100%" :loading="loading">
      <template #empty><Empty /></template>
      <el-table-column
        prop="notifyName"
        label="通知策略名称"
        min-width="216px"
        show-overflow-tooltip
      >
        <template #default="scope">
          <span
            class="high-light-text"
            @click="detailNoticePolice(scope.row)"
            >{{ scope.row.notifyName }}</span
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="remark"
        min-width="216px"
        label="描述"
        show-overflow-tooltip
        :formatter="
          (row) => {
            return row.remark || '-';
          }
        "
      />
      <el-table-column
        prop="notifyTime"
        label="通知时间"
        width="110"
        :formatter="formatTime"
      ></el-table-column>

      <el-table-column
        prop="notifyChannelNames"
        label="通知方式"
        width="160"
        show-overflow-tooltip
        :formatter="
          (row) => {
            return row.notifyChannelNames || '-';
          }
        "
      />
      <el-table-column prop="status" label="状态" width="104">
        <template #default="scope">
          <div class="card-status">
            <i :class="{ enable: scope.row.status % 2 == 1 }"></i>
            <span>{{ scope.row.status % 2 == 0 ? "已停用" : "已启用" }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="updateTime"
        label="更新时间"
        show-overflow-tooltip
        width="166"
      />
      <el-table-column
        prop="updater"
        label="更新人"
        width="120"
        show-overflow-tooltip
      />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="handleNoticePoliceList(scope.row)"
            >{{ scope.row.status ? "停用" : "启用" }}</el-button
          >
          <el-button link type="primary" @click="editNoticePolice(scope.row)"
            >编辑</el-button
          >
          <el-button link type="primary" @click="cloneNoticePolice(scope.row)"
            >克隆</el-button
          >
          <el-button link type="primary" @click="delNotice(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="tableData.length !== 0"
      class="pagination mt-16"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      layout="slot, ->,prev, pager, next, sizes"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    >
      <div>
        {{ `共 ${total} 条数据` }}
      </div>
    </el-pagination>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import SelectorWithLabel from "@/components/SelectorWithLabel.vue";
import { useRouter } from "vue-router";
import noticePoliceApi from "@/request/api/notice-police/index";
import { ElMessage, ElMessageBox } from "element-plus";

import noticeChannelApi from "@/request/api/notice-channel/index";
import { debounce } from "@/utils";
import { useDebounceFn } from "@vueuse/core";
import Empty from "@/components/empty/Empty.vue";

const router = useRouter();
const loading = ref(false);

const levelOptions = [
  {
    label: "严重",
    value: "严重",
  },
  {
    label: "警告",
    value: "警告",
  },
  {
    label: "信息",
    value: "信息",
  },
];

const channelOptions = ref([]);

const form = reactive({
  policyName: "",
  notifyChannel: "",
});
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  getNoticePoliceList();
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  getNoticePoliceList();
};

const createNoticePolice = () => {
  router.push("/notice-police/create");
};
const editNoticePolice = (row) => {
  router.push(`/notice-police/edit/${row.notifyId}`);
};
const cloneNoticePolice = (row) => {
  router.push(`/notice-police/clone/${row.notifyId}`);
};

const detailNoticePolice = (row) => {
  router.push(`/notice-police/detail/${row.notifyId}`);
};

const searchAlarm = (alarm) => {
  getNoticePoliceList();
};
const searchChannel = (channel) => {
  currentPage.value = 1;
  getNoticePoliceList();
};

const getNoticePoliceList = () => {
  loading.value = true;
  const params = {};
  if (form.notifyChannel) {
    params.notifyChannel = form.notifyChannel;
    params.policyName = form.policyName;
  } else {
    params.policyName = form.policyName;
  }
  noticePoliceApi
    .getNoticePoliceList({
      pageNo: currentPage.value,
      pageSize: pageSize.value,
      ...params,
    })
    .then((res) => {
      total.value = res.total;
      tableData.value = res.records;
    })
    .finally(() => {
      loading.value = false;
    });
};

const debounceFunc = useDebounceFn(() => {
  currentPage.value = 1;
  getNoticePoliceList();
}, 500);

const getNoticeChannel = () => {
  noticeChannelApi.getNoticeChannel().then((res) => {
    channelOptions.value = res.map((item) => {
      return {
        label: item.channelName,
        value: item.channel,
      };
    });
  });
};

const delNotice = (row) => {
  ElMessageBox.confirm(`确认要删除${row.notifyName}吗？`, "提示", {
    type: "warning",
    center: true,
    autofocus: false,
  }).then(async () => {
    try {
      noticePoliceApi
        .delNotice({
          policyId: row.notifyId,
        })
        .then(() => {
          ElMessage.success("删除成功");
          currentPage.value = 1;
          getNoticePoliceList();
        });
    } catch (error) {}
  });
};

const handleNoticePoliceListRequest = (row) => {
  if (row.status) {
    noticePoliceApi.disableNoticePolice({ notifyId: row.notifyId }).then(() => {
      ElMessage.success("停用成功");
      getNoticePoliceList();
    });
  } else {
    noticePoliceApi.enableNoticePolice({ notifyId: row.notifyId }).then(() => {
      ElMessage.success("启用成功");
      getNoticePoliceList();
    });
  }
};
const handleNoticePoliceList = (row) => {
  if (row.status) {
    ElMessageBox.confirm(
      `确定要${row.status ? "停用" : "启用"}${row.notifyName}吗？`,
      "提示",
      {
        type: "warning",
        center: true,
        autofocus: false,
      }
    )
      .then(async () => {
        handleNoticePoliceListRequest(row);
      })
      .catch(() => {});
  } else {
    handleNoticePoliceListRequest(row);
  }
};

const formatTime = (row) => {
  if (row.notifyTime == 0) {
    return "任何时间";
  } else if (row.notifyTime == 1) {
    return "工作日";
  } else if (row.notifyTime == 2) {
    return "自定义";
  } else {
    return "-";
  }
};
onMounted(() => {
  getNoticeChannel();
  getNoticePoliceList();
});
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  justify-content: space-between;
  .form {
    display: flex;
    gap: 8px;
  }
}
.pagination {
  margin-top: 20px;
  justify-content: end;
  position: relative;
}
.card-status {
  display: flex;
  align-items: center;
  position: relative;

  i {
    display: inline-block;
    width: 6px;
    height: 6px;
    margin-right: 6px;
    background: #bfbfbf;
    border-radius: 50%;
  }
  span {
    font-size: 14px;
    color: #262626;
  }

  .enable {
    background: #12b312;
  }
}
.high-light-text {
  cursor: pointer;
}
.high-light-text:hover {
  color: #1f69ff;
}
</style>
