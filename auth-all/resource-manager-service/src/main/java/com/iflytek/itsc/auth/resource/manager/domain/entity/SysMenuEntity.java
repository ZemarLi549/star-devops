package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单表
 */
@Data
@TableName("sys_menu")
public class SysMenuEntity implements Serializable {
    private static final long serialVersionUID = -4060281098371087790L;
    /**
     * 菜单id
     */
    @TableId(type = IdType.AUTO)
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 子平台模块，来自sys_config表
     */
    private String moduleType;

    /**
     * 菜单地址
     */
    private String menuPath;

    /**
     * 是否菜单分组
     */
    private Boolean isgroup;

    /**
     * 是否菜单
     */
    private Boolean ismenu;

    /**
     * 是否外链
     */
    private Boolean isoutlink;

    /**
     * 图标
     */
    private String icon;

    /**
     * 平级内序号
     */
    private Integer sortNum;

    /**
     * 是否默认，默认菜单每个人都会有
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

    /**
     * 菜单编码
     */
    private String menuNo;
}