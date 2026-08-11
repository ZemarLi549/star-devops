package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务组
 */
@Data
@TableName("sys_data_group")
public class SysDataGroupEntity implements Serializable {
    private static final long serialVersionUID = -378671571931727170L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long dataGroupId;

    /**
     * 工作空间id
     */
    private Long workSpaceId;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 名称
     */
    private String dataGroupName;

    /**
     * 数据单元token
     */
    private String dataGroupToken;

    /**
     * 是否数据单元
     */
    private Boolean iselement;

    /**
     * 备注
     */
    private String remark;

    /**
     * 序号
     */
    private Integer sortNum;

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