<template>
  <div class="control-card personal-overview-card" id="overview-card">
    <div class="card-header">
      <span class="card-header-title">
        <span>个人运营</span>
        <el-tooltip
          content="这里聚合个人工单、飞书、文档会议、资源和 AI 待办等纵览指标。"
          placement="right"
        >
          <span class="tooltip-icon">
            <FontIcon icon="icon-yiwen" />
          </span>
        </el-tooltip>
      </span>
      <div class="header-tools">
        <div class="range-tabs" role="tablist" aria-label="统计周期">
          <button
            v-for="item in rangeTabs"
            :key="item.key"
            class="range-tab"
            :class="{ active: activeRange === item.key }"
            type="button"
            @click="activeRange = item.key"
          >
            {{ item.label }}
          </button>
        </div>
        <div class="handle">
          <el-tooltip
            content="按住拖拽可移动"
            placement="top"
            :disabled="isDragging"
          >
            <span>
              <FontIcon icon="icon-tuozhuai" />
            </span>
          </el-tooltip>
        </div>
      </div>
    </div>

    <div class="card-content">
      <div class="overview-banner">
        <img class="hero-mark" :src="brandMark" alt="鑫图平台" />
        <div class="banner-copy">
          <div class="banner-title">个人工作统计会围绕统一时间窗口持续扩展</div>
          <div class="banner-desc">
            首页先预留个人工单、飞书协同、文档会议、应用系统、服务器资源和 AI
            待办看板的纵览位。聊天采集先落地，会议和文档保持独立接入，不互相阻塞。
          </div>
          <div class="banner-meta">
            <span>统计周期：{{ activeRangeLabel }}</span>
            <span>数据源：飞书 / PG / CMDB / Zabbix / AI 待办</span>
          </div>
        </div>
      </div>

      <div class="metric-grid">
        <div
          v-for="metric in overviewMetrics"
          :key="metric.title"
          class="metric-card"
        >
          <div class="metric-card-header">
            <div class="metric-card-title">{{ metric.title }}</div>
            <span class="metric-card-badge">{{ metric.source }}</span>
          </div>
          <div class="metric-value">{{ metric.value }}</div>
          <div class="metric-desc">{{ metric.desc }}</div>
          <div class="metric-links">
            <span
              v-for="entry in metric.entries"
              :key="entry"
              class="metric-link"
              @click="showComingSoon(entry)"
            >
              {{ entry }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="card-cover"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import brandMark from "@/assets/brand/mark.svg";

defineProps(["isDragging"]);

const rangeTabs = [
  { key: "today", label: "今日" },
  { key: "7d", label: "近7天" },
  { key: "30d", label: "近30天" },
];

const activeRange = ref("7d");
const activeRangeLabel = computed(
  () => rangeTabs.find((item) => item.key === activeRange.value)?.label || "近7天"
);

const overviewMetrics = computed(() => [
  {
    title: "处理工单",
    source: "PG 工单库",
    value: "待接入",
    desc: "展示个人处理工单数、已办、处理中和协办情况，统一归入 AI 人效自动化系统子页。",
    entries: ["工单运营子页", "已办工单详情"],
  },
  {
    title: "飞书协同",
    source: "Feishu",
    value: "待接入",
    desc: "展示单聊、群聊、被 @ 次数、话题聚合，以及后续自动生成的日报与待办草稿。",
    entries: ["单聊/群聊统计", "消息详情"],
  },
  {
    title: "文档与会议",
    source: "Feishu",
    value: "待接入",
    desc: "展示参与文档、会议数量和时长等协同信号，作为聊天统计之外的独立采集源。",
    entries: ["文档贡献详情", "会议记录详情"],
  },
  {
    title: "AI 待办看板",
    source: "AI Productivity",
    value: "规划中",
    desc: "基于聊天、工单和人工编辑生成个人待办，支持状态流转、日期调整、删除与补录。",
    entries: ["待办看板", "日程建议"],
  },
  {
    title: "应用与资源",
    source: "CMDB / Zabbix",
    value: "待接入",
    desc: "展示个人负责应用系统、服务器和资源总量，以及个人机器的基础使用情况。",
    entries: ["CMDB 资源详情", "个人机器详情"],
  },
  {
    title: "日报与周报",
    source: "AstrBot / LLM",
    value: "待接入",
    desc: "聊天、工单、会议、文档汇总后生成日报与周报草稿，保留人工审阅确认链路。",
    entries: ["日报草稿", "周报草稿"],
  },
]);

const showComingSoon = (entry: string) => {
  ElMessage.info(`${entry} 接口和数据口径待接入，当前为占位入口`);
};
</script>

<style scoped lang="scss">
.personal-overview-card {
  min-height: 520px;
}

.card-content {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.overview-banner {
  display: flex;
  gap: 18px;
  align-items: center;
  padding: 18px 20px;
  border-radius: 18px;
  background:
    radial-gradient(circle at 12% 18%, rgba(255, 255, 255, 0.9) 0, rgba(255, 255, 255, 0) 30%),
    linear-gradient(120deg, #edf5ff 0%, #f3fbf7 100%);
  border: 1px solid #e1ecfa;
}

.hero-mark {
  width: 62px;
  height: 62px;
  flex: none;
}

.banner-copy {
  flex: 1;
}

.banner-title {
  font-size: 18px;
  font-weight: 600;
  color: #162033;
}

.banner-desc {
  margin-top: 6px;
  color: #56657b;
  line-height: 1.75;
  font-size: 13px;
}

.banner-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  color: #4b5971;
  font-size: 12px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  padding: 15px;
  border-radius: 16px;
  border: 1px solid #e6ecf5;
  background: #fcfdff;
}

.metric-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.metric-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #182235;
}

.metric-card-badge {
  padding: 4px 9px;
  border-radius: 999px;
  background: #f2f6fc;
  color: #5d6d83;
  font-size: 11px;
}

.metric-value {
  margin-top: 14px;
  font-size: 24px;
  font-weight: 700;
  color: #0f1f33;
}

.metric-desc {
  margin-top: 10px;
  min-height: 62px;
  color: #5d6d83;
  font-size: 12px;
  line-height: 1.75;
}

.metric-links {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.metric-link {
  color: #1f69ff;
  font-size: 12px;
  cursor: pointer;
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 12px;
}

.range-tabs {
  display: inline-flex;
  padding: 4px;
  border-radius: 999px;
  background: #f1f5fb;
}

.range-tab {
  border: none;
  background: transparent;
  color: #607089;
  font-size: 12px;
  padding: 7px 14px;
  border-radius: 999px;
  cursor: pointer;

  &.active {
    background: #fff;
    color: #1f69ff;
    box-shadow: 0 4px 10px rgba(31, 105, 255, 0.12);
  }
}

.tooltip-icon {
  display: inline-flex;
  align-items: center;
  margin-left: 8px;
  color: #8a95a7;
  cursor: pointer;
}

@media (max-width: 1200px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .overview-banner {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }

  .metric-desc {
    min-height: auto;
  }
}
</style>
