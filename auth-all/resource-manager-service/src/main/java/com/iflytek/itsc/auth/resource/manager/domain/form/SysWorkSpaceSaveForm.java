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
 * @desc 工作空间增改表单
 **/
@Data
@Valid
public class SysWorkSpaceSaveForm implements Serializable {
    private static final long serialVersionUID = -7824700800486838455L;
    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = {UpdateGroup.class, DeleteGroup.class})
    private Long workSpaceId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称长度不能超过100")
    private String workSpaceName;

    /**
     * 备注
     */
    @Length(max = 200, message = "备注长度不能超过200")
    private String remark;
}
