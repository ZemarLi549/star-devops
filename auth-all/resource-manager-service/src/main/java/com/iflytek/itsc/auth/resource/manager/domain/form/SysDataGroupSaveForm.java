package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
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
 * @date 2023/12/14
 * @desc
 **/
@Data
@Valid
public class SysDataGroupSaveForm implements Serializable {
    private static final long serialVersionUID = -3258864848676186167L;

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = {UpdateGroup.class, DeleteGroup.class})
    private Long dataGroupId;

    /**
     * 工作空间id
     */
    @NotNull(message = "工作空间id不能为空", groups = {AddGroup.class})
    private Long workSpaceId;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 是否数据单元
     */
    @NotNull(message = "是否数据单元不能为空")
    private Boolean iselement;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称长度不能超过100")
    private String dataGroupName;

    /**
     * 数据单元token
     */
    @Length(max = 100, message = "数据单元token长度不能超过100")
    private String dataGroupToken;

    /**
     * 序号
     */
    private Integer sortNum;

    /**
     * 描述
     */
    @Length(max = 200, message = "描述长度不能超过200")
    private String remark;
}
