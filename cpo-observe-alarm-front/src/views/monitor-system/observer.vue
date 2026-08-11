<template>
  <el-radio-group v-model="activeName">
    <el-radio-button label="指标说明" value="指标说明" />
    <el-radio-button label="日志说明" value="日志说明" />
    <el-radio-button label="链路说明" value="链路说明" />
  </el-radio-group>
  <el-tabs v-model="activeName" class="sys-tabs">
    <el-tab-pane label="指标说明" name="指标说明">
      <div>
        <h3><span>一、配置步骤</span></h3>
        <ul>
          <li>1.修改指标监控系统的配置文件，编辑配置文件config.toml，新增以下内容</li>
        </ul>
        <pre>
          <code class="language-javascript">
[HTTP.ThirdParty.Alarm]
EventUploadUrl = "http://{alarm-domain}/alarm/api/v1/collect/adapter/metric/event"</code>
        </pre>
        <div>
          <img style="width: 80%" src="@/assets/images/sys/image5.png" alt="" />
        </div>
        <ul>
          <li>2.重新启动监控系统，并加载该配置文件。</li>
        </ul>
      </div>
    </el-tab-pane>
    <el-tab-pane label="日志说明" name="日志说明">
      <div>
        <h3><span>一、配置步骤</span></h3>
        <ul>
          <li>1.星迹可观测平台的nacos系统中，进入日志监控对应的工作空间。</li>
        </ul>
        
        <div>
          <img style="width: 80%" src="@/assets/images/sys/image3.png" alt="" />
        </div>
        <h3><span>二、修改配置</span></h3>
        <ul>
          <li>1.添加告警平台的事件推送接口，更新后生效。</li>
        </ul>
        <pre>
          <code class="language-yaml">
st-log-platform:
  alarm:
    load-rule-scheduler-cron: 50 * * * * *  #加载告警配置定时任务cron
    alarm-event-webhook: http://{alarm-domain}/alarm/api/v1/collect/adapter/log/event
    send-alarm:
      core-pool-size: 1
      max-pool-size: 2</code>
        </pre>
        <div>
          <img style="width: 80%" src="@/assets/images/sys/image4.png" alt="" />
        </div>
      </div>
    </el-tab-pane>
    <el-tab-pane label="链路说明" name="链路说明">
      <div>
        <h3><span>一、配置步骤</span></h3>
        <ul>
          <li>1.星迹可观测平台的nacos系统中，进入调用链监控对应的工作空间。</li>
        </ul>
        <div>
          <img style="width: 80%" src="@/assets/images/sys/image1.png" alt="" />
        </div>
        <h3><span>二、修改配置</span></h3>
        <ul>
          <li>1.添加告警平台的事件推送接口，更新后生效。</li>
        </ul>
        <pre>
          <code class="language-yaml">
webhooks: http://{alarm-domain}/alarm/api/v1/collect/adapter/trace/event</code>
        </pre>
        <div>
          <img style="width: 80%" src="@/assets/images/sys/image2.png" alt="" />
        </div>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref } from 'vue'

const activeName = ref('指标说明')

const getAssetsFile = (url) => {
   return new URL(`../../assets/images/${url}`, import.meta.url).href
}
</script>

<style lang="scss" scoped>
.sys-tabs {
  margin-top: 16px;
  :deep(.observer-alarm-el-tabs__header) {
    display: none;
  }
  :deep(.observer-alarm-el-tabs__content) {
    overflow: auto;
    height: calc(100vh - 303px);
  }
}

pre {
  background-color: #f5f5f5;
  padding: 0 16px;
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