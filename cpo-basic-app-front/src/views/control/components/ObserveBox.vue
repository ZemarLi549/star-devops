<template>
  <div class="control-card">
    <div class="card-header card-header__adjust">
      <span class="card-header-title">
        应用性能观测
        <!-- <el-tooltip
          content="可以实时监控分布式应用性能，帮助用户快速定位并解决性能问题。"
          placement="right"
        >
          <span style="margin-left: 8px; font-weight: 400">
            <FontIcon icon="icon-yiwen" />
          </span>
        </el-tooltip> -->
      </span>
      <div class="handle-group">
        <div
          style="position: relative; height: 32px"
          @mouseenter="shorttemp.show = false"
          @mouseleave="shorttemp.show = true"
        >
          <el-date-picker
            format="YYYY-MM-DD HH:mm"
            v-model="observeDate"
            type="datetimerange"
            range-separator="至"
            placeholder="选择日期"
            start-placeholder="选择开始时间"
            end-placeholder="选择结束时间"
            :disabled-date="disabledDate"
            :shortcuts="observerShortCuts"
            :prefix-icon="Calendar"
            @change="changeDate"
            popper-class="data-picker-console"
          />
          <div v-if="shorttemp.show && shorttemp.text" class="short-text">
            {{ shorttemp.text }}
          </div>
        </div>
        <el-tooltip
          content="按住拖拽可移动"
          placement="top"
          :disabled="isDragging"
        >
          <div class="handle">
            <FontIcon icon="icon-tuozhuai" />
          </div>
        </el-tooltip>
      </div>
    </div>

    <div class="card-content">
      <div class="card-content-title">
        <span class="title">
          <img
            src="@img/control/TOP_10.png"
            style="width: 44px; height: 12px"
            alt=""
          />
          <div class="divider"></div>
          <span class="tag">{{ topTitle }}排行</span>
        </span>

        <div class="link" @click="goSkywalking">
          查看更多
          <FontIcon style="font-size: 18px" icon="icon-you" />
        </div>
      </div>
      <div class="observe-box-table" style="width: 100%">
        <ServiceList
          ref="listRef"
          @changeTitle="changeTopTitle"
          :observeDate="observeDate"
        ></ServiceList>
      </div>
    </div>

    <div class="card-cover"></div>
  </div>
</template>

<script setup lang="ts">
import { Calendar } from "@element-plus/icons-vue";
import { ref } from "vue";
import ServiceList from "../skywalking/ServiceList.vue";
import { useAppStoreWithOut } from "@/stores/modules/app";
import timeFormat from "@/utils/timeFormat";
import { observerShortCuts } from "../constant";
import { useUserStore } from "@/stores/modules/user";
import { ElMessage } from "element-plus";
import { ElDatePicker } from "element-plus";
defineProps(["isDragging"]);

const userStore = useUserStore();
const topTitle = ref("错误数");
const changeTopTitle = (title: string) => {
  topTitle.value = title.trim();
};
const appStore = useAppStoreWithOut();
const goSkywalking = () => {
  const path = "/business-observe/observe-trace/general";
  if (!userStore.hasPathAuth(path))
    return ElMessage.warning("暂无使用权限，请联系相关管理员");
  window.open(
    `${path}?start=${appStore.durationRow.start.getTime()}&end=${appStore.durationRow.end.getTime()}`
  );
};
const observeDate = ref([appStore.durationRow.start, appStore.durationRow.end]);
const listRef = ref(null);

const changeDate = () => {
  if (!(observeDate.value[0] && observeDate.value[1])) return;

  if (window.__isshort_console_sw__) {
    shorttemp.value.text = window.__isshort_console_sw__;
  } else {
    shorttemp.value.text = "";
  }
  window.__isshort_console_sw__ = null;
  if (
    observeDate.value[1].getTime() - observeDate.value[0].getTime() >
    60 * 24 * 60 * 60 * 1000
  ) {
    return;
  }
  appStore.setDuration(timeFormat(observeDate.value));
};
const disabledDate = (time: Date) => {
  return time.getTime() > Date.now();
};

window.__isshort_console_sw__ = null;

const shorttemp = ref({
  show: true,
  text: "",
});
</script>
<style scoped lang="scss">
:deep(.el-date-editor--datetimerange) {
  width: 370px;
  .el-range-input {
    width: 164px !important;
  }
}

.handle-group {
  height: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.control-card {
  background: url("@/assets/imgs/control/observe_bg.png") no-repeat top/960px
    118px;
}

.card-content {
  &-title {
    display: flex;
    justify-content: space-between;
    margin: 13px 0 16px;
    height: 20px;

    .title {
      padding: 0 5px;
      display: flex;
      align-items: center;
      background: linear-gradient(273deg, #3aa6ff 2%, #3a78f2 97%);
      border-radius: 2px;
      overflow: hidden;
      .divider {
        width: 1px;
        height: 20px;
        opacity: 0.2;
        background: #ffffff;
        margin: 0 6px;
      }
    }

    .tag {
      font-size: 12px;
      color: #ffffff;
    }
  }
}

.short-text {
  position: absolute;
  top: 1px;
  left: 36px;
  background: white;
  width: 320px;
  height: 28px;
  line-height: 30px;
}
</style>
