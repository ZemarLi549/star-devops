package com.iflytek.itsc.auth.resource.manager.domain.dto;

import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherField;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author xdkong2
 * @date 2024/10/16
 * @desc 其它模块所用的用户基础信息，及用户关联空间
 **/
@Data
@CipherData
public class OtherServiceUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 3286359691154677162L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 邮箱
     */
    @CipherField
    private String email;
    /**
     * 手机号
     */
    @CipherField
    private String phone;
    /**
     * 是否启用
     */
    private boolean isactive;
    /**
     * 空间id 用于连表查询缓存字段值用
     */
    private Long workspaceId;
    /**
     * 关联的空间id，可做空间权限校验
     */
    private Set<Long> workspaceIdSet;
}
