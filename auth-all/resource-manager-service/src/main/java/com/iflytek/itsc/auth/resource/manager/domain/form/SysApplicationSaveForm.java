package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Data
@Valid
public class SysApplicationSaveForm implements Serializable {
    private static final long serialVersionUID = -4537569396138659808L;

    @NotNull(message = "应用id不能为空", groups = {UpdateGroup.class, DeleteGroup.class})
    private Long applicationId;

    @NotBlank(message = "应用名称不能为空")
    @Length(max = 100, message = "应用名称长度不能超过100")
    private String applicationName;

    @NotNull(message = "数据单元不能为空")
    private Long dataGroupId;

    @Length(max = 200, message = "备注长度不能超过200")
    private String remark;

    @NotNull(message = "空间id不能为空")
    private Long workSpaceId;
}
