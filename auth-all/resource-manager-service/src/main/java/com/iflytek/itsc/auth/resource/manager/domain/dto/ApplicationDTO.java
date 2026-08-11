package com.iflytek.itsc.auth.resource.manager.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 应用
 */
@Data
public class ApplicationDTO{
    /**
     * id
     */
    private Long applicationId;

    /**
     * 工作空间id
     */
    private Long workSpaceId;

    /**
     * 数据单元id
     */
    private Long dataGroupId;

    /**
     * 名称
     */
    private String applicationName;

    /**
     * 编码
     */
    private String applicationCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String createUserName;

    private Long createUser;

}