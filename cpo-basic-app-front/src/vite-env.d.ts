/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare interface Window {
  __config__: object,
  __isshort_console_sw__: any,
  __isshort_console_alarm__: any,
}
