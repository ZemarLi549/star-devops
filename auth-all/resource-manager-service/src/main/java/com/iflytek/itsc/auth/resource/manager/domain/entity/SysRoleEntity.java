package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表
 */
@Data
@TableName("sys_role")
public class SysRoleEntity implements Serializable {
    private static final long serialVersionUID = -4087006111457304680L;
    /**
     * 角色id
     */
    @TableId(type = IdType.AUTO)
    private Long roleId;

    /**
     * 工作空间id
     */
    private Long workSpaceId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否超管
     */
    private Boolean issuperadmin;

    /**
     * 是否默认
     */
    private Boolean isdefault;

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