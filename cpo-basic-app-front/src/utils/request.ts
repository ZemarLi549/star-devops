import axios from "axios";
import type { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";
import { ElMessage } from "element-plus";
// import { useLoginStore } from "@/store/login";

export const BASE_URI = import.meta.env.VITE_BASE_URL;
// export const BASE_URI = "/observability/portal";

const service: AxiosInstance = axios.create({
  baseURL: BASE_URI,
  timeout: 30*1000,
});

export default service;
