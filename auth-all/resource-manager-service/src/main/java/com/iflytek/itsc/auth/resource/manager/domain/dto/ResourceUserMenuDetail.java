package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 菜单树
 **/
@Data
public class ResourceUserMenuDetail implements Serializable {
    private static final long serialVersionUID = 5541250965893298849L;
    /**
     * 模块标识
     */
    private String module;
    /**
     * 菜单id
     */
    private String menuId;
    /**
     * 菜单名称
     */
    private String menuName;
    /**
     * 父id
     */
    private String parentId;
    /**
     * 菜单地址
     */
    private String menuPath;
    /**
     * 图标
     */
    private String icon;
    /**
     * 是否菜单分组
     */
    private boolean isgroup;
    /**
     * 是否菜单
     */
    private boolean ismenu;
    /**
     * 是否外链
     */
    private boolean isoutlink;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 子集
     */
    private List<ResourceUserMenuDetail> children;
}
