package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户工作组关系表
 */
@Data
@TableName("sys_user_work_space")
public class SysUserWorkSpaceEntity implements Serializable {
    private static final long serialVersionUID = 562292993853970242L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 工作组id
     */
    private Long workSpaceId;

    /**
     * 创建时间
     */
    private Date createTime;
}