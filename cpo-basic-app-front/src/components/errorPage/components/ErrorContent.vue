<template>
  <div class="error-page">
    <div class="error-img">
      <img :src="props.src" :alt="props.code" />
    </div>
    <div class="error-content">
      <h1>{{ props.code }}</h1>
      <h3>{{ props.desc }}</h3>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'ErrorContent'
  }
</script>
<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { useUserStore } from "@/stores/modules/user";
import { useConfigStore } from "@/stores/modules/microConfig";
const userStore = useUserStore();

const router = useRouter()

const props = defineProps({
  code: {
    type: String,
    default: ''
  },
  desc: {
    type: String,
    default: ''
  },
  src: {
    type: String,
    default: ''
  }
})

const configStore = useConfigStore();

const goHome = ()=>{
  if(configStore.singleDepoly){
    router.replace(userStore.menuList[0]);
  } else {
    router.replace("/control");
  }
  // router.replace('/control')
}


</script>

<style scoped lang="scss">
.error-page {
  display: flex;
  height: 100%; 
  align-items: center;
  justify-content: center;
  background: linear-gradient(269deg,#e9f1ff 1%, #fafbff 99%);
  .error-img {
    flex: 0 1 923px;
    margin: auto 0;
    img {
      width: 100%;
    }
  }
  .error-content {
    margin-right: 274px;
    h1 {
      font-size: 150px;
      font-weight: 600;
      color: #262626;
      letter-spacing: 2px;
      line-height: 150px;
    }
    h3 {
      height: 48px;
      font-size: 16px;
      font-weight: 400;
      color: #596376;
      line-height: 24px;
      width: 272px;
      margin: 8px 0 24px;
    }
  }
}

</style>
