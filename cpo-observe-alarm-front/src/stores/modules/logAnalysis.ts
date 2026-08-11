import { defineStore } from "pinia";
import { store } from "@/stores";

/*global Nullable*/
interface AppState {
  skywalkingApp: any[];
}

export const logStore = defineStore({
  id: "log",
  state: (): AppState => ({
    skywalkingApp:[],//权限系统里的spaceId切换用
  }),
  getters: {
  },
  actions: {
    setSkywalkingApp(app): void {
      this.skywalkingApp = app;
    },
  },
});
export function useLogStore(): any {
  return logStore(store);
}
