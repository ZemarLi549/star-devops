<template>
  <div
    :class="[
      'card-wrapper',
      {
        'card-color': card.hasConfig,
      },
    ]"
    :style="{
      '--hover-color': cardInfo.hoverColor,
      '--bg-color': cardInfo.bgColor,
    }"
  >
    <div class="card-title">
      <img :src="getAssetsFile(cardInfo.img_url + '.png')" alt="" />
      <span>{{ card.channelName }}</span>
    </div>
    <div class="card-bottom">
      <button class="card-btn" @click="handleStatus">
        {{ card.hasConfig ? "修改配置" : "立即配置" }} >
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import { ChanneMap } from "../notice-police/constant";
const getAssetsFile = (url) => {
  return new URL(`../../assets/images/${url}`, import.meta.url).href;
};

const emit = defineEmits(["create-config", "edit-config"]);

const props = defineProps({
  current: {
    type: Object,
    default: () => {},
  },
  card: {
    type: Object,
    default: () => {},
  },
});
const cardInfo = ChanneMap[props.card.channel] || ChanneMap['default'];
const handleStatus = () => {
  if (props.card?.hasConfig) {
    emit("edit-config");
  } else {
    emit("create-config");
  }
};
</script>

<style lang="scss" scoped>
.card-wrapper {
  width: 100%;
  height: 111px;
  margin: 12px 0;
  background: #f7f8fa;
  border-radius: 4px;
  border: 1px solid #f7f8fa;
}

.card-color {
  background-color: var(--bg-color);
  border: 1px solid var(--bg-color);

  &:hover {
    border-color: var(--hover-color);
  }
}

.card-wrapper:first-child {
  margin-top: 0;
}

.hasConfig {
  background: #fff7e6;
}

.select {
  background: #ebf5ff;
}

.card-title {
  display: flex;
  align-items: center;
  padding: 12px;
  height: 68px;

  img {
    width: 36px;
  }

  span {
    font-size: 16px;
    font-weight: bold;
    margin-left: 16px;
  }

  .card-count {
    padding: 0 4px;
    font-size: 12px;
    color: #595959;
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid #f0f0f0;
    border-radius: 4px;
  }
}

.card-bottom {
  text-align: right;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
  padding: 8px 16px 0 16px;

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

.card-btn {
  color: #595959;
  background: transparent;
}

.card-btn:hover {
  color: #1f69ff;
  cursor: pointer;
}

.is-disabled {
  cursor: pointer !important;
}
</style>
