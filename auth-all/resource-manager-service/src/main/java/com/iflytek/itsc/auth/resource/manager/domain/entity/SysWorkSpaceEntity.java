package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工作空间
 */
@Data
@TableName("sys_work_space")
public class SysWorkSpaceEntity implements Serializable {
    private static final long serialVersionUID = -378671571931727170L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long workSpaceId;

    /**
     * 名称
     */
    private String workSpaceName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否默认
     */
    private Boolean isdefault;

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

    @TableField(exist = false)
    private Long num;
}