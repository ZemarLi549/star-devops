<template>
  <el-config-provider namespace="basic-el" :locale="zhCn">
    <Layout></Layout>
  </el-config-provider>
</template>

<script setup lang="ts">
import Layout from "./layout/Index.vue";
import zhCn from "element-plus/dist/locale/zh-cn.mjs";
</script>
