package com.iflytek.itsc.auth.resource.manager.domain.dto;

import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuResourceEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 用户权限信息-父类
 **/
@Data
public class ResourceUser implements Serializable {
    private static final long serialVersionUID = 1864992774452018901L;
    
    private Long userId;
    
    /**
     * 用户名/具有唯一性
     */
    private String account;
    /**
     * 用户基础信息
     */
    private ResourceUserBaseInfo baseInfo;
    /**
     * 是否是超级管理员
     */

    private boolean isSuperAdmin;
    
    /**
     * 关联的工作空间id集合
     */
    private Set<Long> relateWorkSpaceIdSet;
    /**
     * 用户空间下拥有的资源权限列表
     */
    private Map<Long, List<ResourceUserApi>>   menuResourceApiMap;
}
