package com.iflytek.itsc.auth.resource.manager.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Data
public class SysUserPageParam extends BasePageParam{
    private String account;

    /**
     * 用户名
     */
    private String nickName;

    private Long roleId;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 旧密码
     */
    private String oldPwd;

    private String userId;
    private List<String> userIds;
}
