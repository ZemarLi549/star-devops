package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

@Data
public class SpaceRoleDTO {

    private Long userId;

    private String roleName;

    private String workSpaceName;

    private Integer issuperadmin;
}
