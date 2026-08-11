package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Data
@Valid
public class SysApplicationDeleteForm implements Serializable {
    private static final long serialVersionUID = -3730667982501994990L;

    @NotEmpty(message = "应用id不能为空", groups = {DeleteGroup.class})
    private List<Long> applicationId;

    private String applicationName;
}
