package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/8
 * @desc
 **/
@Data
@Valid
public class SysRoleForm implements Serializable {
    private static final long serialVersionUID = 8783358676653168148L;

    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空", groups = {UpdateGroup.class, DeleteGroup.class})
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Length(max = 100, message = "角色名称长度不能超过100")
    private String roleName;

    /**
     * 工作空间id
     */
    @NotNull(message = "工作空间id不能为空")
    private Long workSpaceId;

    /**
     * 备注
     */
    @Length(max = 200, message = "描述长度不能超过200")
    private String remark;

    /**
     * 菜单、按钮、接口ids
     */
    private List<String> menuIds;
}
