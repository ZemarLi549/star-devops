package com.iflytek.itsc.auth.resource.manager.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author xdkong2
 * @date 2024/3/11
 * @desc 权限同步-应用列表
 **/
@Data
public class ResourceSpaceApplication {

    /**
     * 数据单元token
     */
    private String dataGroupToken;
    /**
     * 数据单元名称
     */
    private String dataGroupName;
    /**
     * 应用名称
     */
    private String applicationName;

    private String applicationCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private Date modifyTime;
}
