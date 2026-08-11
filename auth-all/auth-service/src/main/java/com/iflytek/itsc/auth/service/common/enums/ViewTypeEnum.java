package com.iflytek.itsc.auth.service.common.enums;

/**
 * @Classname ViewTypeEnum
 * @Description 用于前端判断是否是第一次查看的页面的页面类型
 * @Date 2024/4/17 13:59
 * @Created by wxqiu
 */
public enum ViewTypeEnum {

    control("control"),

    dataGroupManager("dataGroupManager"),

    logAnalysis("logAnalysis"),
    integrationGuide("integrationGuide");

    private String value;

    ViewTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
