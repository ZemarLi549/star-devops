<template>
  <div>
    <div class="inner-card">
      <div class="card-tip-title" ref="parentTitle">
        <span style="margin-right: 7px;">{{showEllipsis ? props.thumb : props.title }}</span>
        <el-tooltip placement="top" :content="props.tip">
          <img class="tip-img" src="@/assets/icons/icon_info.svg" alt="" />
        </el-tooltip>
      </div>
      <div style="font-size: 30px; font-weight: 700; margin: 5px 0 20px;">{{ props.count }}</div>
      <div style="font-size: 12px; color: #8E939E">
        <span style="margin-right: 12px;">环比</span>
        <span v-if="props.percent > 0"><img src="@/assets/images/upArrow.png" alt="" style="height: 10px; margin-right: 4px;"></span>
        <span v-if="props.percent < 0"><img src="@/assets/images/downArrow.png" alt="" style="height: 10px; margin-right: 4px;"></span>
        <span :class="['gray-color', {'up-color': props.percent > 0}, {'down-color': props.percent < 0}]">{{ props.percent ? Number((props.percent || 0) / 100).toFixed(2) + '%' : '- -' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, onMounted, onUnmounted, ref } from 'vue'
import { InfoFilled } from '@element-plus/icons-vue'

const parentTitle = ref(null)
const showEllipsis = ref(false)
const props = defineProps({
  title: {
    type: String,
    default: () => ''
  },
  thumb: {
    type: String,
    default: () => ''
  },
  count: {
    type: String || Number,
    default: () => 0
  },
  percent: {
    type: String || Number,
    default: () => 0
  },
  tip: {
    type: String,
    default: () => ''
  }
})

const check = () => {
  const parentTitleWidth = parentTitle.value?.clientWidth
  if(parentTitleWidth < 180) {
    showEllipsis.value = true
  } else {
    showEllipsis.value = false
  }
}



onMounted(() => {
  check()
  window.addEventListener('resize', check)
})

onUnmounted(() => {
  window.removeEventListener('resize', check)
})

</script>

<style lang="scss" scoped>
.gray-color {
  color: #8E939E;
}

.up-color {
  color: #F55863;
}

.down-color {
  color: #00A870;
}

.inner-card {
  padding: 0 20px;
}

.card-tip-title {
  span {
    color: #596376;
    font-weight: 400;
    vertical-align: middle;
  }
  .tip-img {
    vertical-align: middle;
    height: 16px;
    display: inline-block
  }
}

.ellipsis {
  width: 145px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>