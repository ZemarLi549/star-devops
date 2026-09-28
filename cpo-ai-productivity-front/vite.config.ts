import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import qiankun from "vite-plugin-qiankun";

export default defineConfig(({ command }) => ({
  base: command === "serve" ? "/" : "/ai-productivity/",
  plugins: [
    vue(),
    qiankun("ai-productivity", { useDevMode: true }),
  ],
  resolve: {
    alias: {
      "@": "/src",
    },
  },
  server: {
    host: true,
    port: 3011,
    cors: true,
    origin: "http://localhost:3011",
    proxy: {
      "/productivity-api": {
        target: "http://127.0.0.1:9060",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/productivity-api/, ""),
      },
    },
  },
  build: {
    target: "modules",
    outDir: "dist",
    sourcemap: false,
    reportCompressedSize: false,
    chunkSizeWarningLimit: 800,
    minify: "esbuild",
  },
}));
