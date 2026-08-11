package com.iflytek.itsc.auth.resource.manager.domain.form;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc 用户管理
 **/
@Data
@Valid
public class SysWorkSpaceUserForm implements Serializable {
    private static final long serialVersionUID = -271796991905660639L;

    /**
     * 空间id
     */
    @NotNull(message = "工作空间id不能为空")
    private Long workSpaceId;
    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    private Long roleId;
}
