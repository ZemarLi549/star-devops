<template>
  <main class="shell">
    <header class="topbar">
      <div class="brand">
        <div class="brand-mark" aria-hidden="true"><i></i><i></i><i></i></div>
        <div>
          <strong>鑫图平台</strong>
          <small>AI 人效自动化系统</small>
        </div>
      </div>
      <nav class="subnav" aria-label="AI 人效子菜单">
        <button :class="{ active: section === 'overview' }" type="button" @click="section = 'overview'">
          人效概览
        </button>
        <button :class="{ active: section === 'collector' }" type="button" @click="section = 'collector'">
          采集配置
        </button>
      </nav>
      <div class="toolbar">
        <label for="user-id">用户</label>
        <input id="user-id" v-model="userId" @keyup.enter="loadDashboard" />
        <label for="range">窗口</label>
        <select id="range" v-model="range" @change="loadDashboard">
          <option value="1d">最近 1 天</option>
          <option value="2d">最近 2 天</option>
          <option value="7d">最近 7 天</option>
          <option value="30d">最近 30 天</option>
        </select>
        <button class="refresh" type="button" :disabled="loading" aria-label="刷新数据" @click="loadDashboard">
          <span :class="{ spin: loading }">↻</span>
        </button>
      </div>
    </header>

    <template v-if="section === 'overview'">
      <section class="heading">
        <div>
          <span class="eyebrow">PERSONAL OPERATIONS / {{ userId }}</span>
          <h1>把工作事实，变成可行动的反馈。</h1>
          <p>聚合飞书工作沟通、工单、会议和文档贡献，先展示真实数据，再交给 AstrBot 生成日报。</p>
        </div>
        <div class="sync-state">
          <span class="dot" :class="connectionClass"></span>
          <span>{{ connectionLabel }}</span>
          <small>最近同步 {{ lastSync }}</small>
        </div>
      </section>

      <div v-if="loadError" class="error-banner" role="alert">
        <strong>数据服务暂时不可用</strong>
        <span>{{ loadError }}</span>
        <button type="button" @click="loadDashboard">重新连接</button>
      </div>

      <section class="metric-grid" aria-label="人效指标">
        <article v-for="metric in metrics" :key="metric.label" class="metric" :class="`tone-${metric.tone}`">
          <div class="metric-label"><span>{{ metric.label }}</span><b>{{ metric.icon }}</b></div>
          <strong>{{ metric.value }}</strong>
          <small>{{ metric.foot }}</small>
        </article>
      </section>

      <section class="content-grid">
        <div class="main-column">
          <article class="panel report-panel">
            <div class="panel-heading">
              <div><span class="kicker">DAILY REPORT</span><h2>AI 日报</h2></div>
              <button class="primary" type="button" :disabled="generating || !reportExists" @click="generateSummary">
                <span v-if="generating" class="loader"></span>{{ generating ? "生成中" : "生成日报" }}
              </button>
            </div>
            <div v-if="summaryText" class="summary">
              <div class="meta"><span class="pill blue">{{ summarySource }}</span><span>生成于 {{ summaryGeneratedAt }}</span></div>
              <pre>{{ summaryText }}</pre>
            </div>
            <div v-else class="empty">
              <div class="empty-mark">AI</div>
              <div>
                <h3>{{ reportExists ? "日报还没有生成" : "等待采集数据" }}</h3>
                <p>{{ reportExists ? "当前已有采集报告，可以生成一份日报。" : "先完成采集器授权并上传数据，日报会在这里出现。" }}</p>
              </div>
            </div>
            <div v-if="actionError" class="inline-error" role="alert">{{ actionError }}</div>
          </article>

          <article class="panel">
            <div class="panel-heading compact">
              <div><span class="kicker">COLLECTED SIGNALS</span><h2>采集摘要</h2></div>
              <span class="muted">{{ reportRange }}</span>
            </div>
            <div v-if="reportExists" class="signal-list">
              <div class="signal"><span><i class="blue"></i>工作沟通</span><b>{{ communicationText }}</b><small>{{ report?.communication?.peakHour || "暂无高峰时段" }}</small></div>
              <div class="signal"><span><i class="orange"></i>会议</span><b>{{ report?.meeting?.meetingCount ?? 0 }} 场</b><small>{{ report?.meeting?.meetingMinutes ?? 0 }} 分钟</small></div>
              <div class="signal"><span><i class="green"></i>文档</span><b>{{ report?.document?.editCount ?? 0 }} 次编辑</b><small>{{ report?.document?.createCount ?? 0 }} 次新建</small></div>
              <div class="signal"><span><i class="ink"></i>工作话题</span><b>{{ report?.communication?.keywordHitCount ?? 0 }} 次命中</b><small>{{ topicText }}</small></div>
            </div>
            <div v-else class="empty-line">暂无采集报告，聊天、会议和文档数据将在上传后显示。</div>
          </article>
        </div>

        <aside class="side-column">
          <article class="panel">
            <div class="panel-heading compact"><div><span class="kicker">PIPELINE</span><h2>链路状态</h2></div></div>
            <div class="pipeline">
              <div class="pipeline-row"><i class="node" :class="reportExists ? 'on' : 'wait'"></i><span><b>飞书采集器</b><small>{{ reportExists ? "已收到最新报告" : "等待采集器上传" }}</small></span><em>{{ reportExists ? "ON" : "WAIT" }}</em></div>
              <div class="pipeline-row"><i class="node" :class="astrbotReady ? 'on' : 'off'"></i><span><b>AstrBot</b><small>{{ astrbotText }}</small></span><em>{{ astrbotReady ? "ON" : "OFF" }}</em></div>
              <div class="pipeline-row"><i class="node muted-node"></i><span><b>会议 / 文档</b><small>独立采集接口已预留</small></span><em>NEXT</em></div>
              <div class="pipeline-row"><i class="node muted-node"></i><span><b>空间 ES</b><small>按空间隔离统计数据</small></span><em>{{ esState }}</em></div>
            </div>
            <div class="space-line">当前空间 <b>builtin</b></div>
          </article>

          <article class="panel">
            <div class="panel-heading compact"><div><span class="kicker">NOTIFICATIONS</span><h2>采集通知</h2></div><span class="count">{{ notifications.length }}</span></div>
            <div v-if="notifications.length" class="notifications">
              <div v-for="item in notifications" :key="item.id || item.createdAt" class="notification">
                <i :class="{ danger: item.severity === 'error' }">!</i>
                <span><b>{{ item.title || "采集器通知" }}</b><small>{{ item.detail || item.message || "暂无详情" }}</small><em>{{ item.createdAt || item.time || "-" }}</em></span>
              </div>
            </div>
            <div v-else class="empty-line">没有新的授权、采集或日报失败通知。</div>
          </article>
        </aside>
      </section>
    </template>

    <template v-else>
      <section class="heading collector-heading">
        <div>
          <span class="eyebrow">COLLECTOR SETUP / USER GUIDE</span>
          <h1>采集器配置与用户操作</h1>
          <p>这里提供操作说明，不保存用户的机器人密钥、App Secret 或 user token；具体 YAML 由用户在本地采集器目录维护。</p>
        </div>
        <span class="guide-badge">用户自带机器人</span>
      </section>

      <section class="guide-grid">
        <article v-for="(step, index) in guideSteps" :key="step.title" class="guide-card">
          <span class="step-no">0{{ index + 1 }}</span>
          <h2>{{ step.title }}</h2>
          <p>{{ step.description }}</p>
          <pre>{{ step.command }}</pre>
          <button v-if="step.command" type="button" class="copy-button" @click="copy(step.command)">复制命令</button>
        </article>
      </section>

      <section class="guide-bottom">
        <article class="panel">
          <div class="panel-heading compact"><div><span class="kicker">LOCAL YAML</span><h2>本地配置重点</h2></div></div>
          <pre class="config-code">{{ configSnippet }}</pre>
        </article>
        <article class="panel">
          <div class="panel-heading compact"><div><span class="kicker">FILTER POLICY</span><h2>默认过滤与安全边界</h2></div></div>
          <div class="policy-list">
            <p><b>默认排除：</b>机器人、审批流、工作流、应用通知和常见闲聊。</p>
            <p><b>可自定义：</b>群聊名称正则、群聊白名单、关键词过滤和采集时间。</p>
            <p><b>默认调度：</b>每天 17:30 采集最近 1 天；可在本地 YAML 中调整。</p>
            <p><b>本地回调：</b><code>127.0.0.1:17891</code>，只监听本机，不要求对外开放。</p>
            <p><b>数据保留：</b>原始聊天默认 15 天，统计和日报默认 180 天。</p>
          </div>
        </article>
      </section>
    </template>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import {
  generateDailySummary,
  getAstrBotStatus,
  getLatestReport,
  getLatestSummary,
  getNotifications,
  getOverview,
  type ApiRecord,
} from "./api";

defineProps<{ qiankunProps?: Record<string, unknown> }>();

const section = ref<"overview" | "collector">("overview");
const userId = ref("E0028517");
const range = ref("2d");
const loading = ref(false);
const generating = ref(false);
const loadError = ref("");
const actionError = ref("");
const overview = ref<ApiRecord>({});
const report = ref<ApiRecord | null>(null);
const reportExists = ref(false);
const summary = ref<ApiRecord | null>(null);
const astrbot = ref<ApiRecord>({});
const notifications = ref<ApiRecord[]>([]);
const lastSync = ref("未同步");

const value = (item: unknown) => item === undefined || item === null || item === "" || item === "待接入" ? "-" : String(item);
const communicationCount = computed(() => Number(report.value?.communication?.singleChatCount || 0) + Number(report.value?.communication?.groupChatCount || 0));
const metrics = computed(() => [
  { label: "工作沟通", value: value(overview.value.communicationCount ?? communicationCount.value), foot: `${value(overview.value.mentionCount ?? report.value?.communication?.mentionCount)} 次被 @`, icon: "↗", tone: "blue" },
  { label: "处理工单", value: value(overview.value.ticketDoneCount), foot: "三方工单库待接入", icon: "◫", tone: "orange" },
  { label: "会议参与", value: value(overview.value.meetingCount ?? report.value?.meeting?.meetingCount), foot: `${value(report.value?.meeting?.meetingMinutes)} 分钟`, icon: "◷", tone: "green" },
  { label: "文档贡献", value: value(overview.value.documentCount ?? report.value?.document?.editCount), foot: "编辑 / 新建 / 评论", icon: "⌁", tone: "ink" },
]);
const reportRange = computed(() => report.value?.range ? `${report.value.range.startAt || "-"} 至 ${report.value.range.endAt || "-"}` : `最近 ${range.value.replace("d", "")} 天`);
const communicationText = computed(() => `${report.value?.communication?.singleChatCount || 0} 单聊 · ${report.value?.communication?.groupChatCount || 0} 群聊`);
const topicText = computed(() => report.value?.topics?.[0]?.title || "暂无识别话题");
const summaryText = computed(() => summary.value?.summary?.summaryText || "");
const summaryGeneratedAt = computed(() => summary.value?.summary?.generatedAt || "暂无");
const summarySource = computed(() => summary.value?.summary?.source || "待生成");
const astrbotReady = computed(() => Boolean(astrbot.value.summaryGenerationReady));
const astrbotText = computed(() => astrbotReady.value ? `${astrbot.value.provider || "模型"} / ${astrbot.value.model || "已配置"}` : "未配置可用 API");
const esState = computed(() => overview.value.source === "collector-report" ? "READY" : "WAIT");
const connectionClass = computed(() => loadError.value ? "error" : reportExists.value ? "ready" : "wait");
const connectionLabel = computed(() => loadError.value ? "服务离线" : reportExists.value ? "数据已接入" : "等待数据");

const guideSteps = computed(() => [
  { title: "下载插件包", description: "由管理员发布对应平台的 Go 二进制包，解压到独立目录。正式下载地址接入后可配置为平台下载入口。", command: "cpo-feishu-collector-plugin/\n├─ bin/\n├─ config/\n├─ data/\n└─ docs/" },
  { title: "安装与初始化", description: "初始化当前用户目录、日志目录和本地配置文件。不要把 App Secret 提交到 Git 或上传到平台。", command: `cd cpo-feishu-collector-plugin\ncp config/application.example.yaml config/application.yaml\nbash bin/install.sh ${userId.value}` },
  { title: "用户授权", description: "使用用户自己配置的飞书应用完成 OAuth 授权。授权后采集器会在本地保存并自动刷新 user token。", command: `bash bin/authorize.sh ${userId.value}` },
  { title: "单次验证与长期运行", description: "先验证一次采集结果，再启动守护模式。需要关闭时使用 stop.sh，不需要用户自己写 crontab。", command: `bash bin/collect-once.sh --user ${userId.value}\nbash bin/start.sh\nbash bin/status.sh\nbash bin/stop.sh` },
]);

const configSnippet = `feishu:
  auth-base-url: https://accounts.feishu.cn
  open-base-url: https://open.feishu.cn/open-apis
  redirect-uri: http://127.0.0.1:17891/callback

collector:
  schedule-at: "17:30"
  lookback-days: 1

filters:
  exclude-bot-interactions: true
  include-group-regexes: []
  exclude-group-regexes:
    - "(?i).*机器人.*"
    - "(?i).*审批.*"
    - "(?i).*通知.*"
  include-keywords:
    - 发布
    - 工单
    - 告警
    - 联调`;

function resolveSectionFromLocation() {
  const query = new URLSearchParams(window.location.search);
  return query.get("tab") === "collector" ? "collector" : "overview";
}

function syncSectionToLocation(current: "overview" | "collector") {
  const url = new URL(window.location.href);
  if (current === "collector") {
    url.searchParams.set("tab", "collector");
  } else {
    url.searchParams.delete("tab");
  }
  window.history.replaceState({}, "", `${url.pathname}${url.search}${url.hash}`);
}

function formatTime() {
  return new Date().toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" });
}

async function loadDashboard() {
  loading.value = true;
  loadError.value = "";
  try {
    const results = await Promise.allSettled([
      getOverview(userId.value, range.value),
      getLatestReport(userId.value),
      getLatestSummary(userId.value),
      getAstrBotStatus(),
      getNotifications(userId.value),
    ]);
    const [overviewResult, reportResult, summaryResult, astrbotResult, notificationResult] = results;
    if (overviewResult.status === "fulfilled") overview.value = overviewResult.value || {};
    if (reportResult.status === "fulfilled") {
      reportExists.value = Boolean(reportResult.value?.exists);
      report.value = reportResult.value?.report || null;
    }
    if (summaryResult.status === "fulfilled") summary.value = summaryResult.value?.exists ? summaryResult.value : null;
    if (astrbotResult.status === "fulfilled") astrbot.value = astrbotResult.value || {};
    if (notificationResult.status === "fulfilled") notifications.value = notificationResult.value?.records || [];
    if (results.every((item) => item.status === "rejected")) {
      throw (results[0] as PromiseRejectedResult).reason;
    }
    lastSync.value = formatTime();
  } catch (error) {
    loadError.value = error instanceof Error ? error.message : "无法连接 AI 人效服务";
  } finally {
    loading.value = false;
  }
}

async function generateSummary() {
  if (!reportExists.value) return;
  generating.value = true;
  actionError.value = "";
  try {
    await generateDailySummary(userId.value, "突出工作产出、协同推进、风险和下一步建议");
    summary.value = await getLatestSummary(userId.value);
  } catch (error) {
    actionError.value = error instanceof Error ? error.message : "日报生成失败，请查看通知";
  } finally {
    generating.value = false;
  }
}

async function copy(text: string) {
  try {
    await navigator.clipboard.writeText(text);
  } catch {
    actionError.value = "当前浏览器不支持自动复制，请手动复制代码块内容";
  }
}

watch(section, (current) => {
  syncSectionToLocation(current);
});

onMounted(() => {
  section.value = resolveSectionFromLocation();
  loadDashboard();
});
</script>
