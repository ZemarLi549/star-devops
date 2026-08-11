package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作组
 */
@Data
@TableName("sys_application")
public class SysApplicationEntity implements Serializable {
    private static final long serialVersionUID = 5290616995603729948L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 是否有效
     */
    private Boolean isvalid;

    /**
     * 创建者
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long modifyUser;

    /**
     * 更新时间
     */
    private Date modifyTime;
}