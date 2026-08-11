<template>
    <div class="control-card" id="entrance-card">
      <div class="card-header">
        <span class="card-header-title">
          网络可用性探测
          <el-tooltip
            content="网络可用性探测"
            placement="right"
          >
            <span style="margin-left: 8px; font-weight: 400">
              <FontIcon icon="icon-yiwen" />
            </span>
          </el-tooltip>
        </span>
      </div>
      <div class="card-content">
        <el-input v-model="inputValue" class="card-content-input" placeholder="请输入URL，快速检测网络可用性"></el-input>
        <div class="card-content-btn">一键检测</div>
      </div>
    </div>
  </template>
  
  <script setup lang="ts">
  import {ref} from 'vue'
  const inputValue = ref('')
  </script>
  
  <style scoped lang="scss">
  .control-card {
      background: url('@/assets/imgs/control/useability_bg.png') no-repeat right/432px 137px;
      .card-content {
        margin-top: 24px;
        display: flex;
        gap: 12px;
        .card-content-input {
            flex: 1;
            height: 38px;
        }
        .card-content-btn {
            height: 38px;
            width: 88px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #2365eb;
            border-radius: 4px;
            color: #fff;
            cursor: pointer;
        }
      }
  }
  </style>