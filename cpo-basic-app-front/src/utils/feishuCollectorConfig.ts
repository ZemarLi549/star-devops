export interface FeishuCollectorConfig {
  enabled: boolean;
  scheduleTime: string;
  collectDirectChats: boolean;
  collectGroupChats: boolean;
  collectMentions: boolean;
  collectMeetings: boolean;
  collectDocuments: boolean;
  summarizeDaily: boolean;
  summarizeWeekly: boolean;
  excludeBotInteractions: boolean;
  storeRawText: boolean;
  retentionDays: number;
  includeGroups: string[];
  excludeGroups: string[];
  includeKeywords: string[];
  excludeKeywords: string[];
  updatedAt?: string;
}

export interface FeishuCollectorUserLike {
  feishuUserId?: string;
  feishu_user_id?: string;
  user_id?: string;
  account?: string;
  user_name?: string;
}

export const FEISHU_COLLECTOR_STORAGE_PREFIX = "cpo:feishu-collector-config";

export const DEFAULT_FEISHU_COLLECTOR_CONFIG: FeishuCollectorConfig = {
  enabled: true,
  scheduleTime: "06:30",
  collectDirectChats: true,
  collectGroupChats: true,
  collectMentions: true,
  collectMeetings: true,
  collectDocuments: true,
  summarizeDaily: true,
  summarizeWeekly: true,
  excludeBotInteractions: true,
  storeRawText: false,
  retentionDays: 7,
  includeGroups: [],
  excludeGroups: [],
  includeKeywords: [],
  excludeKeywords: [],
  updatedAt: "",
};

const cloneDefaultConfig = (): FeishuCollectorConfig => ({
  ...DEFAULT_FEISHU_COLLECTOR_CONFIG,
  includeGroups: [...DEFAULT_FEISHU_COLLECTOR_CONFIG.includeGroups],
  excludeGroups: [...DEFAULT_FEISHU_COLLECTOR_CONFIG.excludeGroups],
  includeKeywords: [...DEFAULT_FEISHU_COLLECTOR_CONFIG.includeKeywords],
  excludeKeywords: [...DEFAULT_FEISHU_COLLECTOR_CONFIG.excludeKeywords],
});

export const buildFeishuCollectorOwnerKey = (userInfo: FeishuCollectorUserLike) => {
  return (
    userInfo.feishuUserId ||
    userInfo.feishu_user_id ||
    userInfo.user_id ||
    userInfo.account ||
    userInfo.user_name ||
    "anonymous"
  );
};

export const buildFeishuCollectorStorageKey = (ownerKey: string) => {
  return `${FEISHU_COLLECTOR_STORAGE_PREFIX}:${ownerKey}`;
};

export const splitCollectorLines = (value: string) => {
  return value
    .split(/\r?\n/)
    .map((item) => item.trim())
    .filter(Boolean);
};

export const joinCollectorLines = (items: string[]) => {
  return items.filter(Boolean).join("\n");
};

export const loadFeishuCollectorConfig = (ownerKey: string) => {
  const fallback = cloneDefaultConfig();

  if (typeof window === "undefined") {
    return fallback;
  }

  try {
    const raw = window.localStorage.getItem(buildFeishuCollectorStorageKey(ownerKey));
    if (!raw) {
      return fallback;
    }

    const parsed = JSON.parse(raw);
    return {
      ...fallback,
      ...parsed,
      includeGroups: Array.isArray(parsed?.includeGroups)
        ? parsed.includeGroups.filter(Boolean)
        : fallback.includeGroups,
      excludeGroups: Array.isArray(parsed?.excludeGroups)
        ? parsed.excludeGroups.filter(Boolean)
        : fallback.excludeGroups,
      includeKeywords: Array.isArray(parsed?.includeKeywords)
        ? parsed.includeKeywords.filter(Boolean)
        : fallback.includeKeywords,
      excludeKeywords: Array.isArray(parsed?.excludeKeywords)
        ? parsed.excludeKeywords.filter(Boolean)
        : fallback.excludeKeywords,
    };
  } catch (error) {
    return fallback;
  }
};

export const saveFeishuCollectorConfig = (
  ownerKey: string,
  config: FeishuCollectorConfig
) => {
  if (typeof window === "undefined") {
    return;
  }

  const payload = {
    ...config,
    updatedAt: new Date().toISOString(),
  };
  window.localStorage.setItem(
    buildFeishuCollectorStorageKey(ownerKey),
    JSON.stringify(payload)
  );
};

export const clearFeishuCollectorConfig = (ownerKey: string) => {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.removeItem(buildFeishuCollectorStorageKey(ownerKey));
};
