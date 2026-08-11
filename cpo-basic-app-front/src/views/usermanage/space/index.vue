<template>
  <div class="user-content">
    <div class="user-content-wrap">
      <div class="minHeight p16 bg-bg_color">
        <div>
          <el-input
            style="width: 250px; margin: 0 0 8px"
            v-model="searchValue"
            placeholder="请输入工作空间名称"
            clearable
          >
            <template #prefix>
              <el-icon class="el-input__icon">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </div>
        <div>
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
              <div
                id="guide-create"
                class="new-card-item"
                @click="handleEditMerchant()"
              >
                <div class="new-card-item_detail bg-bg_color">
                  <el-icon size="20">
                    <Plus />
                  </el-icon>

                  <div>创建工作空间</div>
                </div>
              </div>
            </el-col>
            <el-col
              v-for="(item, index) in Slist"
              :key="index"
              :xs="24"
              :sm="12"
              :md="8"
              :lg="8"
              :xl="6"
            >
              <Card
                :product="item"
                @delete-item="handleDeleteItem"
                @manage-item="handleEditMerchant(item)"
                @member-item="handleMemberManage"
              />
            </el-col>
          </el-row>
        </div>
      </div>
    </div>

    <DialogForm ref="editDialogRef" @refreshList="getCardListData" />

    <el-dialog
      v-model="dialogFormVisible"
      style="width: 550px"
      @close="handleCancel"
      :close-on-click-modal="false"
    >
      <template #header>
        <div
          style="
            display: flex;
            font-size: 16px;
            color: #fa3946;
            font-weight: 550;
          "
        >
          <el-icon size="24" color="#fa830c" style="margin-right: 8px"
            ><WarningFilled
          /></el-icon>
          删除工作空间 {{ `'${currentSelect.workSpaceName}'` }}
        </div>
      </template>
      <el-form ref="formRef" :model="form" label-position="top" :rules="rules">
        <el-form-item label="请输入空间名称，表示您确认删除此空间" prop="name">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleConfirm"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, reactive } from "vue";
import Card from "./components/card.vue";
import DialogForm from "./components/dialogform.vue";
import { ElMessage } from "element-plus";

import { Search, Plus, WarningFilled } from "@element-plus/icons-vue";

import SpaceApi from "@/apis/usermanage/space";
import { useUserStore } from "@/stores/modules/user";

const searchValue = ref("");
const SearchList = ref([]);

onMounted(() => {
  getCardListData();
});

const Slist = computed(() => {
  const search = SearchList.value.filter((v) => {
    return v.workSpaceName
      ?.toLowerCase()
      .includes(searchValue.value.toLowerCase());
  });
  return search;
});

// 编辑租户
const editDialogRef = ref();
const handleEditMerchant = (rowData?) => {
  const params = {
    title: rowData ? "修改工作空间" : "创建工作空间",
    rowData: rowData ? { ...rowData } : null,
  };
  editDialogRef.value.open(params);
};

function handleMemberManage() {}

const getCardListData = async () => {
  const res = await SpaceApi.list();

  SearchList.value = res || [];
};

const dialogFormVisible = ref(false);

const currentSelect = ref({
  workSpaceId: "",
  workSpaceName: "",
});

const formRef = ref(null);

const form = reactive({
  name: "",
});

const rules = reactive({
  name: [{ validator: validateName, trigger: "change" }],
});

function validateName(rule, value, cb) {
  if (!value || value != currentSelect.value.workSpaceName) {
    return cb(new Error("输入正确的工作空间名称"));
  } else {
    return cb();
  }
}

function handleDeleteItem(item) {
  dialogFormVisible.value = true;
  currentSelect.value = item;
}

function handleCancel() {
  dialogFormVisible.value = false;
  currentSelect.value = { workSpaceId: "", workSpaceName: "" };
  form.name = "";
}

const userStore = useUserStore();
function handleConfirm() {
  formRef.value.validate((valid) => {
    if (valid) {
      SpaceApi.delete(currentSelect.value?.workSpaceId).then(async () => {
        ElMessage({
          showClose: true,
          message: "操作成功",
          type: "success",
        });

        try {
          await userStore.updateWorkspaceId();
        } catch (e) {
          console.log(e);
        }

        handleCancel();
        getCardListData();
      });
    }
  });
}
</script>

<style lang="scss" scoped>
.new-card-item {
  height: calc(100% - 12px);
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #ececec;
  color: var(--el-text-color-regular);
  &:hover {
    box-shadow: 0 0px 12px 2px #ddd;
    transition: box-shadow 0.3s;
    border: 1px solid #658dfc;
    color: var(--el-color-primary);
  }
  &_detail {
    height: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    min-height: 204px;
    svg {
      margin-bottom: 10px;
    }
  }
}
</style>
