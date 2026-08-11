<template>
  <div :class="['card-wrapper', {'select': current.systemId == card.systemId}]">
    <div class="card-title">
      <img :src="getAssetsFile(`${card.type}.png`)" alt="" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { SystemType } from "./type";
const getAssetsFile = (url) => {
   return new URL(`../../assets/images/${url}`, import.meta.url).href
}

const emit = defineEmits(['create-config', 'edit-config'])

const props = defineProps({
  current: {
    type: Object,
    default: () => {}
  },
  card: {
    type: Object,
    default: () => {}
  }
})

</script>

<style lang="scss" scoped>
.card-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 72px;
  background: #ffffff;
  padding: 12px;
  margin: 12px 0;
  border: 1px solid #e5e5e5;
  border-radius: 6px;
}

.card-wrapper:last-child {
  margin-bottom: 0;
}

.select {
  background: #f4f7ff;
  border: 1px solid #1f69ff;
}

.card-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  img {
    width: 174px;
    padding: 12px 9px;
  }
  span {
    margin-left: 16px;
  }

  .card-count {
    padding: 0 4px;
    font-size: 12px;
    color: #595959;
    background: rgba(255,255,255,0.10);
    border: 1px solid #f0f0f0;
    border-radius: 4px;
  }
}

.card-bottom {
  

  .card-status {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #595959;
    position: relative;

    i {
      display: inline-block;
      width: 6px;
      height: 6px;
      margin-right: 6px;
      background: #bfbfbf;
      border-radius: 50%;
    }

    .enable {
      background: #12b312;
    }
  }
}

.is-disabled {
  cursor: pointer!important;
}
</style>