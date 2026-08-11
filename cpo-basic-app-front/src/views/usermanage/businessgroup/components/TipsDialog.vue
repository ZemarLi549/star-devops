<template>
  <el-dialog
    title="如何有效使用业务组管理"
    v-model="visible"
    width="480px"
    class="business-dialog"
    :close-on-click-modal="false"
    custom-class="dialog-customer-class"
    align-center
  >
    <el-timeline class="timeline">
      <el-timeline-item
        v-for="(activity, index) in activities"
        :key="index"
        :type="activity.type"
        :hollow="activity.hollow"
      >
        <div class="title">{{ `${activity.title}` }}</div>
        <div class="content">{{ `${activity.content}` }}</div>
        <img :src="activity.pic" class="pic" v-if="activity.pic" alt=""/>
      </el-timeline-item>
    </el-timeline>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="visible = false"> 我知道了</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import authBusinesstips1 from "@/assets/imgs/usermanage/auth_business_tips1.png";
import authBusinesstips2 from "@/assets/imgs/usermanage/auth_business_tips2.png";
const activities = [
  {
    title: "1. 创建数据单元",
    content:
      "数据单元是定义数据访问权限的最小隔离级别，数据单元创建后会自动生成 唯一Token，请将Token配置到数据采集进行数据接入准备。",
    pic: authBusinesstips1,
    type: "primary",
    hollow: true,
    // color: "#1F69FF",
  },
  {
    title: "2. 在数据单元里新建应用",
    content:
      "在单个数据单元中，您可以创建多个应用，每个应用都有唯一的ID。这些应 用用于调用链的数据采集和上传，上传后与数据单元关联且数据将归属于相 应的数据单元。在授权用户数据时，以数据单元为单位提供服务。",
    type: "primary",
    // color: "#1F69FF",
    hollow: true,
  },
  {
    title: "3. 业务分组层级使用",
    content:
      "通过业务分组管理可以实现多层级数据单元的分类，您可以根据实际业务需求 进行划分数据单元到不同的业务分组节点下，然后授权不同成员数据访问权限。",
    type: "primary",
    hollow: true,
    // color: "#1F69FF",
    pic: authBusinesstips2,
  },
];

const visible = ref(false);
const open = () => {
  visible.value = true;
};

defineExpose({
  open,
});
</script>
<style>
.dialog-fade-enter-active .dialog-customer-class {
  animation: my-dialog-fade-in 0.5s linear !important;
}

.dialog-fade-leave-active .dialog-customer-class {
  animation: my-dialog-fade-out 1.5s linear !important;
}

/* @keyframes my-dialog-fade-in {
  0% {
    transform: scale(0);
    transform-origin: 1350px 50px;
    opacity: 0;
  }
  50% {
    transform: scale(0.5);
    transform-origin: 1350px 50px;
    opacity: 0.5;
  }

  100% {
    transform: scale(1);
    transform-origin: 1350px 50px;
    opacity: 1;
  }
} */

/* @keyframes my-dialog-fade-out {
  0% {
    transform: translate3d(100%, 0, 0);
    transform: scale(1);
    transform-origin: 50px 50px;
    opacity: 1;
  }
  50% {
    transform: translate3d(100%, 0, 0);
    transform: scale(0.5);
    transform-origin: 50px 50px;
    opacity: 0.5;
  }

  100% {
    transform: translate3d(100%, 0, 0);
    transform: scale(0);
    transform-origin: 50px 50px;
    opacity: 0;
  }
} */
</style>
<style lang="scss" scoped>
.business-dialog {
  .timeline {
    padding-left: 4px;
  }
  .title {
    line-height: 24px;
    font-weight: 600;
    position: relative;
    top: -3px;
    margin-bottom: 5px;
  }
  .content {
    margin-left: 15px;
    font-size: 12px;
    line-height: 20px;
    color: var(--text-color-regular);
    width: 396px;
  }
  .pic {
    // height: 58px;
    width: 397px;
    margin: 8px 0 0 15px;
  }
}

</style>
<style>
.dialog-fade-enter-active .business-dialog {
  animation: anim-open .3s;
}
.dialog-fade-leave-active .business-dialog {
  animation: anim-close .3s;
}
@keyframes anim-open {
  0% {
    transform: translate3d(130%, -45%, 0) scale3d(0.1, 0.1, 1);
    opacity: 0;
  }
  100% {
    transform: translate3d(0, 0, 0) scale3d(1, 1, 1);
    opacity: 1;
  }
}
@keyframes anim-close {
  0% {
    transform: translate3d(0, 0, 0) scale3d(1, 1, 1);
    opacity: 1;
  }
  100% {
    transform: translate3d(130%, -45%, 0) scale3d(0.1, 0.1, 1);
    opacity: 0;
  }
}
</style>
