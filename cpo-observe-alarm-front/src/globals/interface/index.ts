/*
 * @Author: zhewu4
 * @Date: 2023-11-28 16:28:00
 * @Last Modified by: zhewu4
 * @Last Modified time: 2023-11-28 16:28:38
 */

export interface CommonResponseGetList<T extends object> {
  current: number;
  maxLimit: number;
  orders: Array<string>;
  pages: number;
  records: Array<T>;
  size: number;
  total: number;
}

export interface Options<T = string | number> {
  label: string;
  value: T;
}

export type BasicType = number | string | boolean;

interface assignRuleInfo {
  assignRuleId: number;
  assignRuleName: string;
  assignRuleRemark: string;
}
interface notifyInfo {
  notifyId: number;
  notifyName: string;
  notifyRemark: string;
}

export interface BaseInfo {
  alarmId?: string;
  assignRuleInfos?: assignRuleInfo[];
  assignRuleNames?: string[];
  assignRuleRemarks?: string[];
  blockRuleName?: string;
  blockRuleRemark?: string;
  blockId?: number;
  cate?: number;
  closeReason?: string;
  ackReason?: string;
  content?: string;
  duration?: string;
  firstTriggerTime?: string;
  labels?: string;
  level?: number;
  notifyTime?: string;
  notifyInfos?: notifyInfo[];
  sources?: string;
  source?: string;
  state?: number;
  updateTime?: string;
  updater?: string;
  uniqueNum?: number;
  eventNum?: number;
}

export interface SearchParams {
  alarmId?: string;
  assignPolicyId?: string;
  assignRuleNames?: string[];
  blockRuleNames?: string[];
  blockRuleName?: string;
  cate?: number;
  level?: number;
  state?: number;
  startTime?: number;
  endTime?: number;
  uniqueOrigin?: string;
  eventId?: string;
  uniqueId?: string;
  status?: number;
  notifyId?: string;
  from?: number;
  to?: number;
}

export interface AlarmCount {
  pending?: number;
  dealing?: number;
  closed?: number;
  total?: number;
}
