package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 公共配置表
 */
@Data
@TableName("sys_config")
public class SysConfigEntity implements Serializable {
    private static final long serialVersionUID = 546805282409079518L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 类型 菜单按钮标识/子平台编码等
     */
    private String propertyType;

    /**
     * 属性名
     */
    private String propertyKey;

    /**
     * 属性值
     */
    private String propertyValue;

    /**
     * 平级内序号
     */
    private Integer sortNum;

    /**
     * 是否有效
     */
    private Boolean isvalid;

    /**
     * 备注
     */
    private String remark;
}