<template>
  <div class="collector-guide-page">
    <section class="hero-card">
      <div class="hero-copy">
        <div class="hero-eyebrow">个人飞书采集指导</div>
        <h1>页面只负责指导，真实配置请在本地采集器中维护</h1>
        <p class="hero-desc">
          当前策略已经收敛为“前端看说明、用户改本地 YAML、采集器独立授权”。这样能避免把个人机器人密钥、过滤规则和回调信息留在浏览器侧，也更方便后续迁移到 Nacos。
        </p>
        <div class="hero-tags">
          <span>一键部署</span>
          <span>本地 YAML</span>
          <span>用户独立授权</span>
          <span>自动刷新 Token</span>
        </div>
      </div>

      <div class="hero-panel">
        <img class="brand-mark" :src="brandMark" alt="鑫图平台" />
        <div class="panel-title">当前用户</div>
        <div class="panel-name">{{ userLabel }}</div>
        <div class="panel-meta">平台账号：{{ userIdentity.userId || "-" }}</div>
        <div class="panel-meta">飞书 ID：{{ userIdentity.feishuUserId || "-" }}</div>
        <div class="panel-meta">默认采集目标：{{ collectorTargetUserId }}</div>
        <div class="panel-actions">
          <el-button @click="goBack">返回首页</el-button>
          <el-button type="primary" plain @click="copyText(configFilePath, '配置文件路径已复制')">
            复制配置路径
          </el-button>
        </div>
      </div>
    </section>

    <section class="guide-grid">
      <article class="guide-card">
        <div class="section-title">1. 先修改飞书应用安全设置</div>
        <div class="section-copy">
          你在飞书开放平台里必须先放行 OAuth 回调地址，否则授权页会直接报 `20029`。本地默认回调地址如下：
        </div>
        <pre class="code-block">{{ redirectUri }}</pre>
        <div class="tips-list">
          <div>重定向 URL 必须与授权链接里的 `redirect_uri` 完全一致，包括协议、IP、端口和路径。</div>
          <div>如果你们后续切公司内网飞书域名，直接同步修改本地 `feishu.auth-base-url / feishu.open-base-url` 即可。</div>
          <div>如果应用后台有“刷新 user_access_token”开关，需要开启并发布生效。</div>
        </div>
      </article>

      <article class="guide-card">
        <div class="section-title">2. 编辑本地配置文件</div>
        <div class="section-copy">
          前端不再代替用户保存本地采集配置。你只需要在采集器目录内编辑这一份 YAML：
        </div>
        <pre class="code-block">{{ configFilePath }}</pre>
        <div class="config-highlight">
          <div class="highlight-title">关键配置项</div>
          <pre class="code-block">{{ configSnippet }}</pre>
        </div>
      </article>

      <article class="guide-card">
        <div class="section-title">3. 授权与单次采集</div>
        <div class="command-list">
          <div class="command-item">
            <div class="command-title">一键部署</div>
            <pre class="code-block">{{ quickDeployCommand }}</pre>
          </div>
          <div class="command-item">
            <div class="command-title">启动授权</div>
            <pre class="code-block">{{ authorizeCommand }}</pre>
          </div>
          <div class="command-item">
            <div class="command-title">单次采集验证</div>
            <pre class="code-block">{{ collectCommand }}</pre>
          </div>
          <div class="command-item">
            <div class="command-title">查看结果</div>
            <pre class="code-block">{{ showReportCommand }}</pre>
          </div>
          <div class="command-item">
            <div class="command-title">查看运行状态</div>
            <pre class="code-block">{{ statusCommand }}</pre>
          </div>
        </div>
      </article>

      <article class="guide-card">
        <div class="section-title">4. 自动运行与端口要求</div>
        <div class="section-copy">
          默认按最近 `1` 天窗口、每天下午 `17:30` 运行一次。授权成功后，采集器会自动在令牌快过期时刷新；只有 `refresh_token` 过期、撤销或超过飞书重授权窗口时，才需要重新授权。
        </div>
        <pre class="code-block">{{ daemonCommand }}</pre>
        <div class="tips-list">
          <div>需要手动停止时，直接执行 `bash bin/stop.sh`。</div>
          <div>授权失败、令牌刷新失败、采集失败、回传失败都会上报到平台通知接口。</div>
          <div>如果后端已配置服务端通知 webhook，平台会继续转发到统一通知渠道。</div>
        </div>
        <div class="port-table">
          <div v-for="port in firewallPorts" :key="port.name" class="port-row">
            <span>{{ port.name }}</span>
            <span>{{ port.port }}</span>
            <span>{{ port.desc }}</span>
          </div>
        </div>
      </article>

      <article class="guide-card wide-card">
        <div class="section-title">5. 采集边界与后续产物</div>
        <div class="guide-columns">
          <div class="guide-column">
            <div class="mini-title">当前已纳入</div>
            <div class="bullet-list">
              <div>聊天采集只统计工作相关消息，统一排除机器人、审批流、通知类交互。</div>
              <div>支持群聊白名单、黑名单、群聊正则过滤、关键词过滤。</div>
              <div>聊天结果会作为日报总结和 AI 待办建议的上游输入。</div>
            </div>
          </div>
          <div class="guide-column">
            <div class="mini-title">后续独立接入</div>
            <div class="bullet-list">
              <div>会议采集失败时不影响聊天采集，后续可切你提供的会议数据库。</div>
              <div>文档贡献采集后续按独立接口接入，也不阻塞聊天链路。</div>
              <div>个人待办看板会作为独立微服务子项目，支持编辑、删除、状态修改、日期调整。</div>
            </div>
          </div>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { ElMessage } from "element-plus";
import brandMark from "@/assets/brand/mark.svg";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";

const router = useRouter();
const userStore = useUserStore();

const userIdentity = computed(() => ({
  feishuUserId:
    userStore.userInfo.feishuUserId || userStore.userInfo.feishu_user_id || "",
  userId: userStore.userInfo.user_id || "",
  account: userStore.userInfo.user_name || "",
  nickName: userStore.userInfo.nickName || "",
}));

const userLabel = computed(() => {
  return (
    userIdentity.value.nickName ||
    userIdentity.value.account ||
    userIdentity.value.feishuUserId ||
    "未命名用户"
  );
});

const collectorTargetUserId = computed(() => {
  return userIdentity.value.userId || userIdentity.value.feishuUserId || "E0028517";
});

const configFilePath = "cpo-feishu-collector-plugin/config/application.yaml";
const redirectUri = "http://127.0.0.1:17891/callback";

const configSnippet = `feishu:
  auth-base-url: https://accounts.feishu.cn
  open-base-url: https://open.feishu.cn/open-apis
  redirect-uri: http://127.0.0.1:17891/callback
  callback-addr: 127.0.0.1:17891
  auth-scopes:
    - offline_access
    - im:message:readonly
    - im:message.p2p_msg:get_as_user
    - im:message.group_msg:get_as_user

filters:
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

const quickDeployCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    `bash bin/quick-deploy.sh ${collectorTargetUserId.value}`,
  ].join("\n");
});

const authorizeCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    `bash bin/authorize.sh ${collectorTargetUserId.value}`,
  ].join("\n");
});

const collectCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    `PLUGIN_USER_ID=${collectorTargetUserId.value} bash bin/collect-e0028517.sh`,
  ].join("\n");
});

const showReportCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    `bash bin/show-report.sh ${collectorTargetUserId.value}`,
  ].join("\n");
});

const daemonCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    "bash bin/start.sh",
    "bash bin/status.sh",
    "bash bin/stop.sh",
  ].join("\n");
});

const statusCommand = computed(() => {
  return [
    "cd cpo-feishu-collector-plugin",
    "bash bin/status.sh",
  ].join("\n");
});

const firewallPorts = [
  { name: "飞书开放平台", port: "443/tcp", desc: "OAuth 授权、令牌刷新、IM 接口访问" },
  { name: "本地回调监听", port: "17891/tcp", desc: "本机浏览器回调给采集器，默认只在本地环回地址监听" },
  { name: "Nacos", port: "8848/tcp", desc: "后续接统一配置中心时读取公共配置" },
  { name: "平台回传", port: "9004/tcp", desc: "后续把统计结果回传服务端时使用" },
];

const copyText = async (text: string, successMessage: string) => {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage.success(successMessage);
  } catch (error) {
    ElMessage.warning("当前浏览器不支持直接复制");
  }
};

const goBack = () => {
  router.push("/control");
};
</script>

<style scoped lang="scss">
.collector-guide-page {
  min-height: 100%;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(31, 105, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #f4f7fc 0%, #eef3fb 100%);
  color: #162235;
}

.hero-card {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) minmax(320px, 0.8fr);
  gap: 20px;
  margin-bottom: 20px;
}

.hero-copy,
.hero-panel,
.guide-card {
  background: #fff;
  border-radius: 18px;
  border: 1px solid #e3eaf4;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.04);
}

.hero-copy {
  padding: 32px;
}

.hero-eyebrow {
  color: #1f69ff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

h1 {
  margin: 12px 0 14px;
  font-size: 30px;
  line-height: 1.2;
}

.hero-desc {
  margin: 0;
  color: #57667a;
  line-height: 1.8;
  font-size: 14px;
}

.hero-tags {
  margin-top: 20px;
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
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.brand-mark {
  width: 52px;
  height: 52px;
}

.panel-title {
  font-size: 13px;
  color: #64748b;
}

.panel-name {
  font-size: 22px;
  font-weight: 700;
}

.panel-meta {
  color: #556579;
  font-size: 13px;
}

.panel-actions {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.guide-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.guide-card {
  padding: 24px;
}

.wide-card {
  grid-column: 1 / -1;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.section-copy {
  margin-top: 10px;
  color: #5f7085;
  line-height: 1.8;
  font-size: 14px;
}

.code-block {
  margin: 14px 0 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #0f172a;
  color: #dbe7ff;
  font-size: 12px;
  line-height: 1.7;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-word;
}

.tips-list,
.bullet-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #596b82;
  font-size: 13px;
  line-height: 1.8;
}

.highlight-title,
.command-title,
.mini-title {
  font-size: 14px;
  font-weight: 600;
  color: #19263a;
}

.config-highlight,
.command-item {
  margin-top: 16px;
}

.command-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.port-table {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.port-row {
  display: grid;
  grid-template-columns: 140px 120px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f8fbff;
  color: #5d6d83;
  font-size: 13px;
}

.guide-columns {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.guide-column {
  padding: 18px;
  border-radius: 14px;
  background: #f8fbff;
  border: 1px solid #e7eef8;
}

@media (max-width: 1100px) {
  .hero-card,
  .guide-grid,
  .guide-columns {
    grid-template-columns: 1fr;
  }

  .wide-card {
    grid-column: auto;
  }
}

@media (max-width: 768px) {
  .collector-guide-page {
    padding: 16px;
  }

  h1 {
    font-size: 24px;
  }

  .port-row {
    grid-template-columns: 1fr;
  }
}
</style>
