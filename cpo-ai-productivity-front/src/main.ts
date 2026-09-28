import { createApp } from "vue";
import { qiankunWindow, renderWithQiankun } from "vite-plugin-qiankun/dist/helper";
import App from "./App.vue";
import "./styles.css";

let app: ReturnType<typeof createApp> | null = null;

function render(props: Record<string, unknown> = {}) {
  app = createApp(App, { qiankunProps: props });
  app.mount("#app");
}

if (qiankunWindow.__POWERED_BY_QIANKUN__) {
  renderWithQiankun({
    bootstrap() {
      return Promise.resolve();
    },
    mount(props) {
      render(props);
    },
    unmount() {
      app?.unmount();
      app = null;
    },
    update() {
      return Promise.resolve();
    },
  });
} else {
  render();
}
