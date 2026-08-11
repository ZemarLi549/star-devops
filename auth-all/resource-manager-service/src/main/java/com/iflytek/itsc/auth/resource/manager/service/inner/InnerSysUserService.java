package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.UserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;

public interface InnerSysUserService {

    /**
     * 新增用户，包括用户管理处新增
     */
    void insertUser(SysUserForm form, boolean checkSpaceAuth);

    /**
     * 更新用户，包括工作台处更新
     */
    void updateUser(SysUserForm form);

    /**
     * 删除用户
     */
    void deleteUser(SysUserDeleteForm form);

    /**
     * 变更启用状态
     */
    void changeActive(SysUserForm form);

    /**
     * 重置密码
     */
    void resetPassword(SysUserPageParam form);

    Page<UserDTO> page(SysUserPageParam form);

    void updatePassword(SysUserPageParam form);

    String getDefaultPwd();
}
