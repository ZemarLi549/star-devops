import { defineConfig } from "vitepress-test";
import { sidebar, nav } from "./menu";
const IP = "http://172.30.34.73";
const BASE = "/sop_doc";
const CurrentVersion = "V4.1.1";
const VersionList = ["V4.0.2", "V4.0.3", "V4.0.4", "V4.0.5", "V4.0.6", "V4.0.7", "V4.0.8", "V4.0.9", "V4.1.0", 'V4.1.1']
const isPrivate = process.env.VITE_MODE === 'private'
// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "文档中心",
  description: "星迹可观测平台文档中心",
  srcDir: "docs",
  base: isPrivate ? `${BASE}/` : `${BASE}/${CurrentVersion}/`,
  head: [["link", { rel: "icon", href: "favicon.ico" }]],
  ignoreDeadLinks: 'localhostLinks',
  themeConfig: {
    logo: {
      light: "/logo_light.png", dark: "/logo_dark.png"
    },
    nav: isPrivate ? nav() : [
      ...nav(),
      {
        text: CurrentVersion,
        items: VersionList.filter(item => item !== CurrentVersion).reverse().map(item => {
          return { text: item, link: `${IP}${BASE}/${item}/` }
        })
      }
    ],
    outline: {
      level: [2, 4],
    },

    sidebar: sidebar(),
    lastUpdated: {
      formatOptions: {
        dateStyle: 'full',
        timeStyle: 'medium'
      }
    },
    search: {
      provider: "local",
    },
  },
});
