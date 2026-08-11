<template>
  <div :class="['card-wrapper', { select: current == card.groupId }]">
    <div style="display: flex; align-items: center">
      <img
        style="width: 40px; height: 40px; margin-right: 8px"
        :src="
          getAssetsFile(
            card.status % 2 == 1 ? 'groupcheck.png' : 'groupuncheck.png'
          )
        "
        alt=""
      />
      <div>
        <div class="card-title">
          <div class="ellipsis">{{ card.groupName }}</div>
          <div class="card-count">
            {{ card.userCount > 99 ? "99+" : card.userCount || 0 }}
          </div>
        </div>
        <div class="card-bottom">
          <div
            :class="`card-status card-status-${
              card.status % 2 == 0 ? '0' : '1'
            }`"
          >
            <!-- <i :class="{'enable': card.status % 2 == 1}"></i> -->
            <span>{{ card.status % 2 == 0 ? "已停用" : "已启用" }}</span>
          </div>
          <div style="display: flex" v-if="current == card.groupId">
            <img class="icons" @click.stop="edit" :src="Edit" alt="" />
            <img class="icons" @click.stop="del" :src="Dele" alt="" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import Dele from "@/assets/icons/del.svg";
import Edit from "@/assets/icons/edit.svg";

const getAssetsFile = (url) => {
  return new URL(`../../../assets/images/${url}`, import.meta.url).href;
};

const props = defineProps({
  current: {
    type: Number || String,
    default: () => null,
  },
  card: {
    type: Object,
    default: () => {},
  },
});

const emit = defineEmits(["edit-group", "del-group"]);

const edit = () => {
  emit("edit-group");
};
const del = () => {
  emit("del-group");
};
</script>

<style lang="scss" scoped>
.card-wrapper {
  width: 100%;
  height: 72px;
  background: #f7f8fa;
  position: relative;
  padding: 12px;
  margin: 12px 0;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.card-wrapper:hover {
  box-shadow: 0px 1px 4px 0px #e5e5e5;
}

.card-wrapper:first-child {
  margin-top: 0;
}

.select {
  background: #ffffff;
  border: 1px solid #1f69ff;
}

.card-title {
  display: flex;
  justify-content: space-between;

  .card-count {
    position: absolute;
    padding: 0 3px;
    right: 0px;
    top: 0px;
    font-size: 12px;
    color: #595959;
    background: #eceef3;
    border-radius: 0px 4px 0px 4px;
  }
}

.card-bottom {
  display: flex;
  margin-top: 6px;
  justify-content: space-between;
  align-items: center;

  .card-status {
    width: 44px;
    height: 20px;

    border-radius: 2px;
    line-height: 20px;
  }
  .card-status-1 {
    background: #ecffe8;
    border: 0.5px solid #8de184;
    color: #00a870;
    font-size: 12px;
    text-align: center;
  }
  .card-status-0 {
    background: #f1f2f5;
    border: 0.5px solid #d9d9d9;
    color: #262626;
    font-size: 12px;
    text-align: center;
  }
}

.ellipsis {
  width: 140px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.icons {
  margin-left: 12px;
  cursor: pointer;
}
</style>
