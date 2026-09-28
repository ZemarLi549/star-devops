import axios from "axios";

export type ApiRecord = Record<string, any>;

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/productivity-api",
  timeout: 30_000,
});

function getSession(): ApiRecord {
  try {
    const raw = window.localStorage.getItem("userinfo");
    if (!raw) return {};
    const envelope = JSON.parse(raw);
    return envelope?.value && typeof envelope.value === "object" ? envelope.value : envelope;
  } catch {
    return {};
  }
}

client.interceptors.request.use((config) => {
  const session = getSession();
  if (session.token) {
    config.headers = config.headers || {};
    config.headers.token = session.token;
  }
  if (session.workspace_id && !config.headers?.["Workspace-Id"]) {
    config.headers = config.headers || {};
    config.headers["Workspace-Id"] = session.workspace_id;
  }
  return config;
});

function unwrap(response: { data?: ApiRecord }) {
  const body = response.data || {};
  if (body.success === false) {
    throw new Error(body.errorMessage || body.message || "接口返回失败");
  }
  return body.data ?? body;
}

export const getOverview = (userId: string, range: string) =>
  client.get("/api/productivity/overview/self", { params: { userId, range } }).then(unwrap);

export const getLatestReport = (userId: string) =>
  client.get("/api/productivity/report/daily/latest", { params: { userId } }).then(unwrap);

export const getLatestSummary = (userId: string) =>
  client.get("/api/productivity/report/daily/summary/latest", { params: { userId } }).then(unwrap);

export const getAstrBotStatus = () =>
  client.get("/api/productivity/astrbot/status").then(unwrap);

export const getNotifications = (userId: string) =>
  client.get("/api/productivity/plugin/notification/latest", {
    params: { userId, limit: 6 },
  }).then(unwrap);

export const generateDailySummary = (userId: string, focus: string) =>
  client.post("/api/productivity/report/daily/summary/generate", { userId, focus }).then(unwrap);
