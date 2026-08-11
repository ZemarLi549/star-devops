<template>
  <ErrorContent code="404" desc="很抱歉，您没有浏览这个页面的权限， 或该页面地址错误。" :src="error404" />
</template>

<script lang="ts">
  export default {
    name: 'Error404'
  }
</script>
<script lang="ts" setup>
import error404 from '@/assets/imgs/404-min.png'
import ErrorContent from './components/ErrorContent.vue'

</script>
