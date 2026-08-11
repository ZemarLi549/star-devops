package com.iflytek.itsc.auth.resource.manager.common.utils;

import java.util.UUID;

/**
 * UUID工具类
 */
public class UUIDUtil {

	public static String getRandomUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

}
