package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2023/12/5
 * @desc
 **/
@Data
@Valid
public class SysUserDeleteForm implements Serializable {
    /**
     * 用户id
     */
    @NotBlank(message = "userId不能为空", groups = {DeleteGroup.class})
    private Long userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 联系方式
     */
    private String phone;
}
