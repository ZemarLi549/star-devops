<template>
  <el-tabs v-model="activeName" class="monitor-tabs">
    <el-tab-pane label="配置步骤" name="first">
      <div>
        <Observer />
      </div>
    </el-tab-pane>
    <el-tab-pane label="快捷上报" name="second">
      <div>
        <div class="content-text">
          星迹可观测平台仅支持在「业务组」下进行数据单元(Token)的新建，创建完成后会自动生成Token,需要您手动复制到数据采集进行接入准备
        </div>
        <div style="margin-top: 12px">
          <!-- 跳转 -->
          <el-button
            type="primary"
            link
            @click="jumpTo"
            style="padding-left: 0"
          >
            前往业务组
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import Observer from "../observer.vue";
import { onMounted, ref } from "vue";
import { ArrowRight } from "@element-plus/icons-vue";

const jumpTo = () => {
  window.open(location.origin + "/usermanage/space", "_blank");
};

const activeName = ref("first");
onMounted(() => {
  activeName.value = "first";
});
</script>

<style scoped lang="scss"></style>
