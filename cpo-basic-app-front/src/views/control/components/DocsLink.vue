<template>
  <div class="control-card">
    <div class="card-header">
      <span class="card-header-title">SOP 文档入口</span>
      <div class="link" @click="goDocument('')">
        打开文档中心
        <FontIcon style="font-size: 18px" icon="icon-you" />
      </div>
    </div>
    <div class="card-content">
      <img src="@/assets/imgs/control/docs_bg.png" alt="" />
      <div @click="goDocument('')" style="cursor: pointer">
        <div class="title">SOP Doc / SOP VDOC</div>
        <div class="sub-title">
          近期建设重点、部署说明、接入文档和可视化操作文档统一沉淀到 SOP 文档体系，不再分散展示在首页。
        </div>
      </div>
      <el-divider></el-divider>
      <ul>
        <li v-for="docLink in docLinkArray" :key="docLink.title">
          <span class="docs-tag">{{ docLink.tag }}</span>
          <span class="docs-title" @click="goDocument(docLink.link)">
            <span>{{ docLink.title }}</span>
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import { docLinkArray } from "../constant";
import { useConfigStore } from "@/stores/modules/microConfig";
const configStore = useConfigStore();
const goDocument = (linkPath) => {
  if (!configStore.$state.documentUrl) {
    ElMessage.info("文档中心地址暂未配置");
    return;
  }
  window.open(configStore.$state.documentUrl + (linkPath || "/sop-doc"));
};
</script>

<style scoped lang="scss">
.card-content {
  margin-top: 24px;
  img {
    height: 110px;
    width: 100%;
  }
  .title {
    font-size: 16px;
    font-weight: 600;
    color: #232a35;
    line-height: 24px;
    margin: 12px 0 6px;
  }
  .sub-title {
    width: calc(100% - 1px);
    font-size: 12px;
    color: #757f92;
    line-height: 20px;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
  }
  ul {
    display: flex;
    flex-direction: column;
    gap: 16px;
    li {
      display: flex;
      align-items: center;
      gap: 8px;
      .docs-tag {
        border: 1px solid #dfe2ea;
        border-radius: 2px;
        width: 60px;
        font-size: 12px;
        color: #4b5976;
        text-align: center;
        height: 22px;
        line-height: 22px;
      }
      .docs-title {
        color: #2c3645;
        flex: 1;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        cursor: pointer;
      }
    }
  }
}
</style>
