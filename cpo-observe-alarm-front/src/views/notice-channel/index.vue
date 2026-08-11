<template>
  <div class="main-container">
    <el-container>
      <el-aside width="298px" :class="['aside']">
        <div class="aside-wrapper">
          <div class="aside-inner">
            <div class="aside-title">
              <span style="font-weight: bold; font-size: 16px">协作集成</span>
            </div>
            <div class="aside-btn">
              <div>
                通过通知渠道集成帮助您的告警与第三方协作工具进行快速对接，实现告警团队与协作团队的及时处理。
              </div>
            </div>
            <div class="aside-cards">
              <Card
                v-for="item in channelOptions"
                :key="item.channelId"
                @click.stop="selectCard(item)"
                :current="current"
                :card="item"
                @create-config="handleEditMerchant(item)"
                @edit-config="handleEditMerchant(item)"
              />
            </div>
          </div>
        </div>
      </el-aside>
      <el-main :class="['main-wrapper', { 'wrapper-bg': showTip }]">
        <div v-if="showTip != null && showTip" class="unselect">
          <div class="unselect-content">
            <div class="unselect-tips"></div>
          </div>
        </div>
        <div v-if="showTip != null && !showTip">
          <div>
            <div class="main-title">
              <div>通知模板</div>
            </div>
            <el-table ref="multipleTableRef" :data="tableData">
              <el-table-column
                property="templateContent"
                label="模板内容"
                minWidth="632"
              >
                <template #default="scope">
                  <div class="ellipsis">{{ scope.row.templateContent }}</div>
                </template>
              </el-table-column>
              <el-table-column
                property="channelName"
                label="通知方式"
                minWidth="152"
              />

              <el-table-column label="操作" fixed="right" width="96">
                <template #default="scope">
                  <el-button
                    type="primary"
                    link
                    @click="handleEditTemplate(scope.row)"
                    >编辑模板</el-button
                  >
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <DialogForm ref="dialogRef" @refreshList="getNoticeChannel()" />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from "vue";
import Card from "./card.vue";
import DialogForm from "./dialogform.vue";
import { Base64 } from 'js-base64';
import noticeChannelApi from "@/request/api/notice-channel/index";

const cards = ref([]);
const current = ref(null);
const selectCard = (item) => {};

const showTip = ref(null);
const tableData = ref([]);

const channelOptions = ref([]);

const getNoticeChannel = () => {
  noticeChannelApi.getNoticeChannel().then((res) => {
    channelOptions.value = res || [];
    showTip.value = !(res || []).find((item) => {
      return item.hasConfig;
    });

    tableData.value = (res || []).filter((item) => {
      return item.hasConfig;
    });
  });
};

const dialogRef = ref();
const handleEditMerchant = (item) => {
  let params = {};
  if (!item.hasConfig) {
    params = {
      title: item.channelName + "  渠道配置",
      rowData: {
        content: Base64.decode(item.config),
        name: item.channelName,
        channel: item.channel,
        hasConfig: item.hasConfig,
      },
    };
  } else {
    params = {
      title: item.channelName + "  配置修改",
      rowData: {
        content: Base64.decode(item.config),
        name: item.channelName,
        channel: item.channel,
        hasConfig: item.hasConfig,
      },
    };
  }

  dialogRef.value.open(params);
};

const handleEditTemplate = (item) => {
  const params = {
    title: "编辑模板",
    rowData: {
      content: item.templateContent,
      channel: item.channel,
    },
  };

  dialogRef.value.open(params);
};

onMounted(() => {
  getNoticeChannel();
});
</script>

<style lang="scss" scoped>
.main-container {
  width: 100%;
  height: 100%;

  .observer-alarm-el-container {
    height: 100%;
  }

  .aside {
    position: relative;
    overflow: visible;

    .aside-wrapper {
      height: 100%;
      background-color: white;
      border-right: 1px solid #f0f0f0;

      .aside-inner {
        display: flex;
        flex-direction: column;
        height: 100%;
        padding: 20px;

        .aside-title {
          display: flex;
          justify-content: space-between;
        }
        .aside-btn {
          margin: 16px 0;
          color: #8c8f9e;
        }

        .aside-cards {
          flex: 1;
          overflow: auto;
        }
      }
    }

    .icon-fold-btn {
      position: absolute;
      top: 0;
      bottom: 0;
      right: -10px;
      margin: auto;
      cursor: pointer;
    }
  }

  .aside-hide {
    border-right: none;
    padding: 0;
  }

  .main-wrapper {
    background-color: white;
    padding: 20px;

    .main-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      div:first-child {
        font-weight: bold;
        font-size: 16px;
      }
    }
  }
}

.unselect {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100% - 40px);

  .unselect-content {
    text-align: center;

    .unselect-tips {
      display: flex;
      width: 584px;
      height: 383px;
      background-image: url("@/assets/images/channel-tip.png");
      background-repeat: no-repeat;
      background-size: cover;

      div:nth-child(2) {
        margin-left: 20px;
      }
    }

    .unselect-bottom {
      margin-top: 20px;
    }
  }
}

.wrapper-bg {
  background-image: url("@/assets/images/channel-bg.png");
  background-repeat: no-repeat;
  background-size: cover;
}

.ellipsis {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
</style>
