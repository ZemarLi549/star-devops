package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户角色关系表
 */
@Data
@TableName("sys_user_role")
@IntegralityData
public class SysUserRoleEntity implements Serializable {
    private static final long serialVersionUID = -7339838464598711977L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @IntegralityField
    private Long userId;

    /**
     * 工作组id
     */
    @IntegralityField
    private Long workSpaceId;

    /**
     * 角色id
     */
    @IntegralityField
    private Long roleId;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private String roleName;

    /*
   利川数据完整性校验字段
    */
    private String sign;
}