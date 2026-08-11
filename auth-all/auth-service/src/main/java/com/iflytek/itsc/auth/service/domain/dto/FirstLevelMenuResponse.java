package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Classname FirstLevelMenuResponse
 * @Description 快捷菜单响应参数
 * @Date 2023/12/18 16:38
 * @Created by wxqiu
 */
@Data
public class FirstLevelMenuResponse {

    Map<String, List<FirstLevelMenu>> firstLevelMenuMap;
}
