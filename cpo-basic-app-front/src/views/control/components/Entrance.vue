<template>
  <div class="control-card service-portal-card" id="entrance-card">
    <div class="card-header">
      <span class="card-header-title">
        <span>服务入口</span>
        <el-tooltip
          content="与个人运营总览同级展示，便于后续微服务独立接入与下线。"
          placement="right"
        >
          <span class="tooltip-icon">
            <FontIcon icon="icon-yiwen" />
          </span>
        </el-tooltip>
      </span>
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

    <div class="card-content">
      <div class="portal-grid">
        <button
          v-for="service in pagedServices"
          :key="service.title"
          class="portal-card"
          type="button"
          @click="openService(service)"
        >
          <div class="portal-top">
            <span class="portal-badge">{{ service.badge }}</span>
            <span class="portal-state">{{ service.state }}</span>
          </div>
          <div class="portal-title">{{ service.title }}</div>
          <div class="portal-subtitle">{{ service.subtitle }}</div>
          <div class="portal-desc">{{ service.desc }}</div>
          <div class="portal-bottom">
            <span class="portal-action">{{ service.actionLabel }}</span>
            <span class="portal-hint">{{ service.hint }}</span>
          </div>
        </button>
      </div>

      <div class="pager-bar">
        <div class="pager-meta">
          <span class="pager-title">服务分页</span>
          <span>{{ servicePage }} / {{ servicePageCount }}</span>
        </div>
        <div class="pager-actions">
          <button
            class="pager-btn"
            type="button"
            :disabled="servicePage === 1"
            @click="servicePage -= 1"
          >
            上一页
          </button>
          <button
            class="pager-btn primary"
            type="button"
            :disabled="servicePage === servicePageCount"
            @click="servicePage += 1"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <div class="card-cover"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

defineProps(["isDragging"]);

type ServiceCard = {
  title: string;
  subtitle: string;
  badge: string;
  state: string;
  desc: string;
  hint: string;
  actionLabel: string;
  route?: string;
};

const router = useRouter();

const services: ServiceCard[] = [
  {
    title: "AI 人效自动化系统",
    subtitle: "日报 / 周报 / 待办看板",
    badge: "Core",
    state: "建设中",
    desc: "统一承接聊天统计、工单运营、日报周报和个人待办看板，是后续核心子系统。",
    hint: "独立前后端微服务。",
    actionLabel: "查看子页",
    route: "/ai-productivity",
  },
  {
    title: "智能助手平台",
    subtitle: "AstrBot 前后端",
    badge: "AstrBot",
    state: "保留",
    desc: "用于后续接入插件托管、会话编排、知识接入和模型能力运维。",
    hint: "后续逐步开放。",
    actionLabel: "查看入口",
  },
  {
    title: "工单运营",
    subtitle: "处理中 / 已办 / 详情",
    badge: "Ticket",
    state: "规划中",
    desc: "工程师工单和已办工单统一纳入 AI 人效自动化系统子模块。",
    hint: "等待 PG 查询接入。",
    actionLabel: "查看规划",
  },
  {
    title: "CMDB 资源运维",
    subtitle: "应用 / 服务器 / 资源",
    badge: "CMDB",
    state: "规划中",
    desc: "展示个人负责的应用系统、相关服务器和运维资源。",
    hint: "等待 CMDB 接口。",
    actionLabel: "详情入口",
  },
  {
    title: "Zabbix 集中管理",
    subtitle: "整合 zabbix-watcher",
    badge: "ZBX",
    state: "规划中",
    desc: "整合多套 Zabbix 数据中心，承接监控、告警与个人机器详情。",
    hint: "等待后端适配。",
    actionLabel: "详情入口",
  },
  {
    title: "AI 待办看板",
    subtitle: "编辑 / 删除 / 状态 / 日期",
    badge: "Todo",
    state: "规划中",
    desc: "作为独立微服务子项目，支持个人手工维护与 AI 自动建议。",
    hint: "聊天与工单联动。",
    actionLabel: "查看规划",
  },
];

const servicePage = ref(1);
const servicePageSize = 6;
const servicePageCount = computed(() =>
  Math.max(1, Math.ceil(services.length / servicePageSize))
);
const pagedServices = computed(() => {
  const start = (servicePage.value - 1) * servicePageSize;
  return services.slice(start, start + servicePageSize);
});

const showComingSoon = (entry: string) => {
  ElMessage.info(`${entry} 接口和接入方案仍在建设中`);
};

const openService = (service: ServiceCard) => {
  if (service.route) {
    router.push(service.route);
    return;
  }
  showComingSoon(service.title);
};
</script>

<style scoped lang="scss">
.service-portal-card {
  min-height: 460px;
}

.card-content {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.portal-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.portal-card {
  min-height: 168px;
  padding: 14px;
  border: 1px solid #e5ebf5;
  border-radius: 16px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(247, 250, 255, 0.98) 100%);
  text-align: left;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    border-color 0.18s ease,
    box-shadow 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: #cddcf7;
    box-shadow: 0 12px 22px rgba(34, 76, 145, 0.08);
  }
}

.portal-top,
.portal-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.portal-badge,
.portal-state {
  border-radius: 999px;
  padding: 4px 9px;
  font-size: 11px;
  line-height: 1;
}

.portal-badge {
  background: #eef4ff;
  color: #2a5fd6;
}

.portal-state {
  background: #eff7f1;
  color: #2d7f48;
}

.portal-title {
  margin-top: 14px;
  font-size: 15px;
  line-height: 1.35;
  font-weight: 600;
  color: #182335;
}

.portal-subtitle {
  margin-top: 6px;
  color: #5f6d81;
  font-size: 12px;
}

.portal-desc {
  margin-top: 10px;
  min-height: 54px;
  color: #6b7788;
  font-size: 12px;
  line-height: 1.7;
}

.portal-action {
  color: #1f69ff;
  font-size: 12px;
  font-weight: 600;
}

.portal-hint {
  color: #7d8798;
  font-size: 11px;
}

.pager-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 14px;
  margin-top: 4px;
}

.pager-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5e6c80;
  font-size: 12px;
}

.pager-title {
  color: #8692a4;
}

.pager-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pager-btn {
  min-width: 72px;
  padding: 7px 14px;
  border-radius: 999px;
  border: 1px solid #d6deec;
  background: #fff;
  color: #304053;
  cursor: pointer;
  font-size: 12px;

  &.primary {
    border-color: #d8e5ff;
    background: #eef4ff;
    color: #2159cf;
  }

  &:disabled {
    cursor: not-allowed;
    color: #9aa7b8;
    background: #f5f7fb;
    border-color: #e2e8f2;
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
  .portal-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .portal-grid {
    grid-template-columns: 1fr;
  }

  .pager-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .pager-actions {
    justify-content: flex-end;
  }
}
</style>
