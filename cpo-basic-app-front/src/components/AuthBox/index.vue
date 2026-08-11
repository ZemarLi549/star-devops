<template>
  <div
    v-loading="loading"
    :class="[
      'auth-list',
      { 'auth-list-user': originType === 'authUserMenu' },
      { 'auth-list-role-readonly': originType === 'authRoleMenu' && readonly },
      {
        'auth-list-user-menu-readonly': isNotCreateForm,
      },
      {
        'auth-list-user-data-readonly': isNotCreateForm,
      },
    ]"
  >
    <div class="auth-list-header">
      <div class="title" v-if="originType === 'authRoleMenu'">菜单授权</div>
      <div v-if="originType === 'authUserData'">
        <div
          class="title"
          style="
             {
              margin-bottom: 8px;
            }
          "
        >
          数据授权
          <el-tooltip>
            <template #content>
              给成员授予多个不同的数据单元（Token）,服务模块中将展示该数据
            </template>
            <span style="font-weight: 400">
              <FontIcon icon="icon-yiwen"
            /></span>
          </el-tooltip>
        </div>
        <div class="title-des">请在下方进行勾选该成员可查阅的数据权限范围</div>
      </div>
      <div class="left-content" v-if="originType !== 'authUserMenu'">
        <el-input
          class="!w-[250px]"
          v-model="searchKey"
          :placeholder="`搜索${
            originType === 'authRoleMenu' ? '菜单' : '数据'
          }名称`"
          ><template #prefix>
            <el-icon class="el-input__icon"><search /></el-icon> </template
        ></el-input>
      </div>
      <div
        v-if="authList.length"
        :class="[
          'right-content',
          { 'right-content-menu': originType === 'authUserMenu' },
          { 'right-content-data': originType === 'authUserData' },
        ]"
      >
        <el-button type="primary" link @click="setAllExpanded(isExpanded)">{{
          isExpanded ? "展开" : "收起"
        }}</el-button>
      </div>
    </div>
    <div class="auth-list-box">
      <div
        class="empty"
        v-if="originType === 'authUserMenu' && !authList.length"
      >
        <EmptyVue text="角色授权菜单功能预览区域" />
      </div>
      <el-tree-v2
        ref="treeRef"
        :data="authList"
        :show-checkbox="!readonly"
        :node-key="nodeId"
        :height="height"
        :props="{
          value: nodeId,
          label: nodeName,
          children: childrenName,
        }"
        default-expand-all
        :expand-on-click-node="false"
        :check-on-click-node="true"
        @check="onTreeCheck"
        @check-change="onTreeCheckChange"
        :check-strictly="true"
        :filter-method="filterNode"
        :empty-text="`${originType === 'authUserMenu' ? '' : '暂无数据'}`"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span class="tree-node-tag">
              <el-tag
                v-if="data.isbutton"
                size="small"
                :disable-transitions="true"
              >
                btn
              </el-tag>
              <el-tag
                type="warning"
                v-else-if="data.isapi"
                size="small"
                :disable-transitions="true"
                >api</el-tag
              >
            </span>
            <Icon
              v-if="data.hasOwnProperty('iselement')"
              class="icon"
              :icon-name="getTreeIconName(data)"
              size="middle"
            ></Icon>
            <span>{{ data[nodeName] }}</span>
          </span>
        </template>
      </el-tree-v2>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, nextTick, onMounted } from "vue";
import Icon from "@/components/Icon.vue";
const searchKey = ref("");
const props = defineProps({
  authList: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
  readonly: {
    type: Boolean,
    default: false,
  },
  height: {
    type: Number,
    default: 300,
  },
  nodeName: {
    type: String,
  },
  nodeId: {
    type: String,
  },
  childrenName: {
    type: String,
  },
  isdisable: {
    default: false,
  },
  originType: {
    //类型判断用于定制化 authUserMenu
    type: String,
  },
  isNotCreateForm: {
    type: Boolean,
  },
});
onMounted(() => {
  setAllExpanded(true);
});
const treeRef = ref();
const setChildreChecked = (node, isChecked) => {
  node.forEach((item) => {
    if (item[props.childrenName] && item[props.childrenName].length > 0) {
      treeRef.value!.setChecked(item[props.nodeId], isChecked);
      setChildreChecked(item[props.childrenName], isChecked);
    } else {
      treeRef.value!.setChecked(item[props.nodeId], isChecked);
    }
  });
};
const onTreeCheck = (data, info) => {
  const isCheckeds = info.checkedKeys.includes(
    treeRef.value!.getNode(data)?.key
  );
  if (isCheckeds) {
    // 判断该节点是否有下级节点，如果有那么遍历设置下级节点为选中
    data[props.childrenName] &&
      data[props.childrenName].length > 0 &&
      setChildreChecked(data[props.childrenName], true);
  } else {
    // 如果节点取消选中，则取消该节点下的子节点选中
    data[props.childrenName] &&
      data[props.childrenName].length > 0 &&
      setChildreChecked(data[props.childrenName], false);
  }
};
const onTreeCheckChange = (data) => {
  // 选中全部子节点，父节点也默认选中，但是子节点再次取消勾选或者全部子节点取消勾选也不会影响父节点勾选状态
  const checkNode = treeRef.value!.getNode(data); //获取当前节点
  const checkedNodeKeys = treeRef.value!.getCheckedKeys();
  // 勾选部分子节点，父节点变为勾选状态

  if (
    checkNode.parent &&
    checkNode.parent["children"].some((ele) => {
      return checkedNodeKeys.includes(ele.key);
    })
  ) {
    treeRef.value!.setChecked(checkNode.parent.key, true);
    if (checkNode.parent.parent) {
      onTreeCheckChange(checkNode.parent.data);
    }
  }
};
const allTreeKeys = computed(() => {
  return flatten(props.authList).map((i) => i[props.nodeId]);
});
// 折叠展开所有节点
const isExpanded = ref(false);
const setAllExpanded = (expanded) => {
  isExpanded.value = !expanded;
  if (expanded) {
    treeRef.value!.setExpandedKeys(allTreeKeys.value);
  } else {
    treeRef.value!.setExpandedKeys([]);
  }
};

const filterNode = (value, data) => {
  if (!value) return true;
  return data[props.nodeName].includes(value);
};

const getChecked = () => {
  const allCheckedKeys = treeRef.value!.getCheckedKeys();
  return allCheckedKeys;
};

const flatten = (arr) => {
  return arr.reduce((result, item) => {
    return result.concat(
      item,
      Array.isArray(item[props.childrenName])
        ? flatten(item[props.childrenName])
        : []
    );
  }, []);
};

const setChecked = (list) => {
  const listIds = flatten(list).map((i) => {
    if (i.isselect) {
      return i[props.nodeId];
    }
  });
  treeRef.value!.setCheckedKeys(listIds);
};

const getTreeIconName = (data) => {
  return data.iselement ? "icon-dataunit" : "icon-business-group";
};

watch(searchKey, (val) => {
  treeRef.value!.filter(val);
});

watch(
  () => props.authList,
  (val) => {
    setAllExpanded(true);
    setTimeout(() => {
      setChecked(val);
    }, 0);
  }
);

defineExpose({
  getChecked,
  setChecked,
  setAllExpanded,
});
</script>
<style lang="scss" scoped>
.auth-list-user {
  border-top: none !important;
  border-top-right-radius: 0 !important;
  border-top-left-radius: 0 !important;
}
.auth-list {
  border: 1px solid #ececec;
  width: 100%;
  background-color: #f7f8fa;
  padding: 16px;
  position: relative;
  border-radius: 4px;
  :deep(.basic-el-tree) {
    background-color: #f7f8fa;
    font-weight: 400;
    .is-checked {
      color: var(--color-primary);
    }
    .basic-el-tree-node__expand-icon {
      color: #000;
    }
  }

  .auth-list-header {
    border-bottom: var(--el-border);
    // padding: 0 16px;
    .title {
      font-weight: 600;
      font-size: 16px;
      margin-bottom: 16px;
      line-height: 20px;
      height: 20px;
    }
    .title-des {
      font-size: 12px;
      color: #8c8c8c;
      line-height: 20px;
      margin: 0 0 16px;
    }
    .left-content {
      // display: flex;
      margin-bottom: 16px;
    }
    .right-content {
      position: absolute;
      top: 98px;
      right: 24px;
      z-index: 999;
    }
    .right-content-menu {
      top: 18px !important;
    }
    .right-content-data {
      top: 132px !important;
    }
  }
  .auth-list-box {
    // height: calc(100vh - 500px) !important;
    // overflow: auto;
    // padding: 10px 16px;
    min-height: 420px;
    position: relative;
    .empty {
      position: absolute;
      top: 50%;
      background-color: rgb(247, 248, 250);
      z-index: 999;
      transform: translateY(-50%);
      width: 100%;
    }
  }
  .custom-tree-node {
    display: flex;
    align-items: center;
    .tree-node-tag {
      margin-right: 5px;
    }
    .node-auth-code {
      color: var(--el-text-color-placeholder);
      font-size: 12px;
      margin-left: 10px;
    }
  }
}
</style>
