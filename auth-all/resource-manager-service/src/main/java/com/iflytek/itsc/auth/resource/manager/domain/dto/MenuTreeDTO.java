package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/17
 * @desc
 **/
@Data
public class MenuTreeDTO implements Serializable {
    private static final long serialVersionUID = -7746030773846009477L;
    /**
     * 菜单id
     */
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 父id
     */
    private String parentId;

    /**
     * 是否菜单分组
     */
    private Boolean isgroup;

    /**
     * 是否菜单
     */
    private Boolean ismenu;
    /**
     * 是否接口
     */
    private Boolean isapi;
    /**
     * 是否按钮
     */
    private Boolean isbutton;
    /**
     * 是否选中
     */
    private Boolean isselect;
    /**
     * 是否不可操作
     */
    private Boolean disabled;
    /**
     * 子集
     */
    private List<MenuTreeDTO> children;
}
