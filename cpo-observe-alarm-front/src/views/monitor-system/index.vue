<template>
  <div class="main-container">
    <el-container>
      <el-aside width="256px" :class="['aside']">
        <div class="aside-wrapper">
          <div class="aside-inner">
            <div class="aside-title">
              <span style="font-weight: bold; font-size: 16px">类型</span>
            </div>
            <div class="aside-cards">
              <Card
                v-for="item in cards"
                :key="item.systemId"
                @click.stop="selectCard(item)"
                :current="current"
                :card="item"
              />
            </div>
          </div>
        </div>
      </el-aside>
      <el-main class="main-wrapper">
        <div>
          <div class="main-title">
            <div>{{ current?.type }}</div>
          </div>
          <component :is="current?.components"></component>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, markRaw } from "vue";
import Card from "./card.vue";
import { SystemType } from "./type";
import starTrace from "./configTypes/starTrace.vue";
import prometheus from "./configTypes/prometheus.vue";
import fushionD from "./configTypes/fushionD.vue";
import eSight from "./configTypes/eSight.vue";
const cards = ref([
  {
    type: SystemType.STARTRACE,
    systemId: 1,
    components: markRaw(starTrace),
  },
  {
    type: SystemType.PROMETHEUS,
    systemId: 2,
    components: markRaw(prometheus),
  },
  {
    type: SystemType.FUSIOND,
    systemId: 3,
    components: markRaw(fushionD),
  },
  {
    type: SystemType.ESIGHT,
    systemId: 4,
    components: markRaw(eSight),
  },
]);

const current = ref(cards.value[0]);

const selectCard = (item) => {
  current.value = item;
};
</script>

<style lang="scss" scoped>
.main-container {
  width: 100%;
  height: 100%;

  .observer-alarm-el-container {
    height: 100%;
  }

  .aside {
    position: relative;
    overflow: visible;

    .aside-wrapper {
      height: 100%;
      background-color: #fcfdff;
      margin-right: 1px;

      .aside-inner {
        display: flex;
        flex-direction: column;
        height: 100%;
        padding: 20px;

        .aside-title {
          display: flex;
          justify-content: space-between;
        }
        .aside-btn {
          margin: 16px 0;
        }

        .aside-cards {
          flex: 1;
          overflow: auto;
        }
      }
    }

    .icon-fold-btn {
      position: absolute;
      top: 0;
      bottom: 0;
      right: -10px;
      margin: auto;
      cursor: pointer;
    }
  }

  .aside-hide {
    border-right: none;
    padding: 0;
  }

  .main-wrapper {
    background-color: white;
    padding: 20px;
    position: relative;
    overflow: hidden;

    .main-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      div:first-child {
        font-weight: bold;
        font-size: 16px;
      }
    }

    .vertical-center {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-weight: bold;
    }
  }
}

.unselect {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100% - 40px);
  .unselect-content {
    text-align: center;

    .unselect-tips {
      display: flex;

      div:nth-child(2) {
        margin-left: 20px;
      }
    }

    .unselect-bottom {
      margin-top: 20px;
    }
  }
}
</style>
