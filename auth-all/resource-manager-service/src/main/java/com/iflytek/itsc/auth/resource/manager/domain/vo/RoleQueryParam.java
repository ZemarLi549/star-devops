package com.iflytek.itsc.auth.resource.manager.domain.vo;

import lombok.Data;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc 角色查询条件
 **/
@Data
public class RoleQueryParam extends BasePageParam {
    private Long workSpaceId;
    private String roleName;
}
