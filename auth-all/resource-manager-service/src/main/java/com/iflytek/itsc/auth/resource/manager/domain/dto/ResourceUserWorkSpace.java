package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 工作空间
 **/
@Data
public class ResourceUserWorkSpace {
    /**
     * 工作空间id
     */
    private Long workSpaceId;
    /**
     * 工作空间名称
     */
    private String workSpaceName;
    /**
     * 空间下用户角色名称
     */
    private String roleName;
    /**
     * 空间下所有关联用户数量
     */
    private Integer userCount;
    /**
     * 空间下所有角色数量
     */
    private Integer roleCount;
}
