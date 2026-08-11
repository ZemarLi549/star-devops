import { defineStore } from "pinia";
import { ref } from "vue";

export const useMenuStore = defineStore("menu", () => {
  const activeMenuItem = ref("");
  const setActiveMenuItem = (path: string) => {
    activeMenuItem.value = path;
  };
  return {
    activeMenuItem,
    setActiveMenuItem,
  };
});
