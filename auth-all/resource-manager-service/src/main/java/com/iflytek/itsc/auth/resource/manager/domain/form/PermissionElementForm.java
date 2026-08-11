package com.iflytek.itsc.auth.resource.manager.domain.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2024/3/18
 * @desc 创建数据单元
 **/
@Data
@Valid
public class PermissionElementForm implements Serializable {
    private static final long serialVersionUID = 1840837171028023861L;

    /**
     * 工作空间id
     */
    @NotNull(message = "工作空间id不能为空")
    private Long workSpaceId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Length(max = 100, message = "名称长度不能超过100")
    private String dataGroupName;
}
