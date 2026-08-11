package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 菜单
 **/
@Data
public class ResourceUserMenu {

    /**
     * 菜单列表
     */
    private List<ResourceUserMenuDetail> menuList;
}
