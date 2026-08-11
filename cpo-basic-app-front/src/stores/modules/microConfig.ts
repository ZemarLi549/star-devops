/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { defineStore } from "pinia";
import { store } from "@/stores";

/*global Nullable*/
interface AppState {
  documentUrl: string;
  loginConfig: object;
  deployList: string[];
  menuClosed: boolean;
  singleDepoly: boolean;
}

export const configStore = defineStore({
  id: "config",
  state: (): AppState => ({
    documentUrl: '',//文档中心地址
    loginConfig: {}, //登陆方式相关配置
    deployList: [], //部署项目
    menuClosed: false, //菜单是否全部展开
    singleDepoly: false, //是否独立部署
  }),
  getters: {
  },
  actions: {
    setDocumentUrl(url: string): void {
      this.documentUrl = url;
    },
    setLoginConfig(config): void {
      this.loginConfig = config;
    },
    setDeployConfig(deployList: string[]): void {
      this.deployList = deployList;
    },
    setMenuClosed(val: boolean): void {
      this.menuClosed = val;
    },
    setSingleDepoly(val: boolean): void {
      this.singleDepoly = val;
    },
    resetDepolyConfig() {
      this.deployList = [];
      this.menuClosed = false;
      this.singleDepoly = false;
    }
  },
});
export function useConfigStore(): any {
  return configStore(store);
}
