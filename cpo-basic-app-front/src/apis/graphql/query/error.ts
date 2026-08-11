import { Errors, ErrorsPm } from "../fragments/error";

// 获取索引配置
export const getServiceErrors = `query queryTraces(${Errors.variable}) {${Errors.query}}`;
export const getServiceErrorsPm = `query queryTraces(${ErrorsPm.variable}) {${ErrorsPm.query}}`;