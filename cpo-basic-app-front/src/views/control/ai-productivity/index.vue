<template>
  <div class="ai-productivity-page">
    <div class="hero-card">
      <div class="hero-copy">
        <div class="hero-eyebrow">AI 人效自动化系统</div>
        <h1>统一承接工单运营与个人协同分析</h1>
        <p class="hero-desc">
          当前子项目作为鑫图平台内的人效自动化入口，集中承接工单运营、飞书沟通统计、
          会议文档贡献、AI 日报周报和个人工作资源纵览。前端先以子页面方式落在工作台，
          后端对应独立微服务 `cpo-ai-productivity-service`，使用 Python 3 + Tornado，
          通过统一的 `cpo-api-gateway` 接入现有网关与 Nacos。当前统一网关采用 KrakenD CE。
        </p>
        <div class="hero-tags">
          <span>子页面先落地</span>
          <span>Python 3 / Tornado</span>
          <span>Nacos 统一配置</span>
          <span>后续接 PostgreSQL / Feishu / ES / Zabbix</span>
        </div>
      </div>

      <div class="hero-panel">
        <div class="panel-title">当前接入路径</div>
        <div class="panel-item">前端入口：`/control/ai-productivity`</div>
        <div class="panel-item">用户采集配置：`/control/productivity-config`</div>
        <div class="panel-item">后端服务：`cpo-ai-productivity-service`</div>
        <div class="panel-item">统一网关：`cpo-api-gateway`（KrakenD CE）</div>
        <div class="panel-item">配置中心：Nacos / `cpo-ai-productivity-common.yaml`</div>
        <div class="panel-actions">
          <el-button @click="goBack">返回首页</el-button>
          <el-button type="primary" plain @click="openCollectorConfig">
            飞书采集配置
          </el-button>
        </div>
      </div>
    </div>

    <div class="body-grid">
      <div class="main-column">
        <div class="section-card">
          <div class="section-title">子项目模块</div>
          <div class="module-grid">
            <div
              v-for="module in modules"
              :key="module.title"
              class="module-card"
            >
              <div class="module-head">
                <div>
                  <div class="module-title">{{ module.title }}</div>
                  <div class="module-subtitle">{{ module.subtitle }}</div>
                </div>
                <span class="module-badge">{{ module.badge }}</span>
              </div>
              <div class="module-desc">{{ module.desc }}</div>
              <div class="module-tags">
                <span v-for="tag in module.tags" :key="tag" class="module-tag">
                  {{ tag }}
                </span>
              </div>
              <div class="module-footer">
                <el-button
                  size="small"
                  type="primary"
                  plain
                  @click="handleModuleClick(module)"
                >
                  {{ module.route ? "进入子页" : "详情入口" }}
                </el-button>
                <span class="module-hint">{{ module.hint }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="section-card">
          <div class="section-title">微服务接入边界</div>
          <div class="boundary-list">
            <div v-for="item in boundaries" :key="item.title" class="boundary-item">
              <div class="boundary-title">{{ item.title }}</div>
              <div class="boundary-desc">{{ item.desc }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="side-column">
        <div class="summary-card">
          <div class="section-title">当前拆分约束</div>
          <div class="summary-list">
            <div class="summary-item">
              <span class="summary-label">工单运营</span>
              <span class="summary-value">归入子项目</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">前端形态</span>
              <span class="summary-value">工作台子页面</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">后端形态</span>
              <span class="summary-value">Python / Tornado</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">配置来源</span>
              <span class="summary-value">Nacos</span>
            </div>
            <div class="summary-item">
              <span class="summary-label">主要数据源</span>
              <span class="summary-value">PG / 飞书 / ES / CMDB / Zabbix</span>
            </div>
          </div>
        </div>

        <div class="summary-card note-card">
          <div class="section-title">当前阶段</div>
          <div class="stage-note">
            现阶段先把子页面、采集配置和后端服务骨架落地。真实工单 SQL、飞书授权、
            ES 存储、CMDB 与 Zabbix 聚合在后续微服务接入时补齐。
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();

const modules = [
  {
    title: "工单运营",
    subtitle: "工程师处理工单 / 已办工单 / SLA",
    badge: "Work Order",
    desc: "围绕三方 PostgreSQL 工单库构建个人处理工单、已办详情、协办与 SLA 纵览能力。",
    tags: ["个人工单", "已办详情", "SLA 视图"],
    hint: "后续按你提供的 SQL 口径接入。",
  },
  {
    title: "飞书采集配置",
    subtitle: "授权 / 过滤规则 / 每日 17:30",
    badge: "Feishu",
    desc: "维护用户自己的采集规则，默认排除机器人、工作流和应用通知交互，后续从前端本地存储切到后端与 Nacos。",
    tags: ["17:30", "最近1天", "按用户隔离"],
    route: "/control/productivity-config",
    hint: "当前可直接进入配置页。",
  },
  {
    title: "AI 日报与周报",
    subtitle: "工作话题 / 会议 / 文档总结",
    badge: "AI",
    desc: "基于飞书沟通、会议纪要和文档贡献生成可审阅日报草稿，后续接入 AstrBot 和模型编排。",
    tags: ["日报草稿", "周报汇总", "人工确认"],
    hint: "待接 AI 模型和摘要服务。",
  },
  {
    title: "资源与监控纵览",
    subtitle: "CMDB / Zabbix / 个人资源",
    badge: "Resource",
    desc: "聚合个人负责应用系统、服务器、监控对象和多数据中心资源指标，统一跳转到 CMDB 与 Zabbix 集中管理。",
    tags: ["CMDB", "Zabbix", "资源总览"],
    hint: "待接资源中心和 zabbix-watcher。",
  },
];

const boundaries = [
  {
    title: "前端边界",
    desc: "工作台只保留 AI 人效自动化系统总入口，工单运营不再以独立服务入口暴露。",
  },
  {
    title: "后端边界",
    desc: "cpo-ai-productivity-service 统一承接工单、飞书、会议、文档、日报聚合接口，并通过 cpo-api-gateway 对外提供 API。",
  },
  {
    title: "配置边界",
    desc: "通用配置统一放 Nacos，敏感凭据不进入前端源码、不进入普通用户下载包。",
  },
  {
    title: "数据边界",
    desc: "工单来自 PostgreSQL，协同来自飞书授权与采集插件，短期原始事件和摘要索引落 ES，长期报表沉淀到关系库。",
  },
];

const goBack = () => {
  router.push("/control");
};

const openCollectorConfig = () => {
  router.push("/control/productivity-config");
};

const handleModuleClick = (module: { title: string; route?: string }) => {
  if (module.route) {
    router.push(module.route);
    return;
  }
  ElMessage.info(`${module.title} 后续由独立微服务接入，当前先保留子项目入口`);
};
</script>

<style scoped lang="scss">
.ai-productivity-page {
  min-height: 100%;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(31, 105, 255, 0.08), transparent 28%),
    linear-gradient(180deg, #f4f7fc 0%, #eef3fb 100%);
  color: #1f2937;
}

.hero-card,
.section-card,
.summary-card {
  background: #fff;
  border: 1px solid #e3e8f2;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.04);
}

.hero-card {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(320px, 0.8fr);
  gap: 20px;
  padding: 28px;
}

.hero-eyebrow {
  color: #1f69ff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

h1 {
  margin: 10px 0 12px;
  font-size: 30px;
  line-height: 1.2;
  color: #122033;
}

.hero-desc {
  margin: 0;
  color: #556579;
  line-height: 1.8;
  font-size: 14px;
}

.hero-tags {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  span {
    padding: 6px 12px;
    border-radius: 999px;
    background: #eef4ff;
    color: #1f4fbe;
    font-size: 12px;
    border: 1px solid #dce7ff;
  }
}

.hero-panel {
  padding: 22px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f8fbff 0%, #eef6ff 100%);
  border: 1px solid #dce7ff;
}

.panel-title {
  font-size: 14px;
  font-weight: 700;
  color: #122033;
  margin-bottom: 12px;
}

.panel-item {
  font-size: 13px;
  line-height: 1.8;
  color: #556579;
}

.panel-actions {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.body-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: minmax(0, 1.75fr) minmax(280px, 0.85fr);
  gap: 20px;
  align-items: start;
}

.main-column,
.side-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card,
.summary-card {
  padding: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #122033;
  margin-bottom: 18px;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.module-card {
  border: 1px solid #e5ebf5;
  border-radius: 14px;
  background: #fbfcff;
  padding: 18px;
}

.module-head,
.module-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.module-title,
.boundary-title {
  color: #122033;
  font-size: 15px;
  font-weight: 700;
}

.module-subtitle {
  margin-top: 6px;
  color: #64748b;
  font-size: 12px;
}

.module-badge {
  flex: none;
  height: fit-content;
  padding: 4px 10px;
  border-radius: 999px;
  background: #eef4ff;
  color: #1f4fbe;
  font-size: 11px;
  font-weight: 700;
}

.module-desc,
.boundary-desc,
.module-hint,
.stage-note {
  color: #556579;
  line-height: 1.7;
  font-size: 13px;
}

.module-desc {
  margin-top: 12px;
}

.module-tags {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.module-tag {
  padding: 5px 10px;
  border-radius: 999px;
  background: #f2f6fc;
  color: #42556f;
  font-size: 12px;
}

.module-footer {
  align-items: center;
  margin-top: 16px;
}

.boundary-list,
.summary-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.boundary-item,
.summary-item {
  padding-bottom: 14px;
  border-bottom: 1px solid #edf1f7;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.summary-label {
  color: #64748b;
  font-size: 13px;
}

.summary-value {
  color: #122033;
  font-size: 13px;
  font-weight: 600;
  text-align: right;
}

.note-card {
  background: linear-gradient(180deg, #eef4ff 0%, #f7faff 100%);
}

@media (max-width: 1080px) {
  .hero-card,
  .body-grid,
  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
