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
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = -7841387008037160466L;

    private Long roleId;
    private String roleName;
    private String remark;
    private String createTime;
    private Boolean isdefault;

    /**
     * 关联用户数量
     */
    private Long userCount;

    /**
     * 服务模块菜单树
     */
    private List<MenuTreeDTO> menuList;
}
