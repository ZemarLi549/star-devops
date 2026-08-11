package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.dto.RoleDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysRoleForm;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysRoleBuilder {

    public static SysRoleEntity buildInsertEntity(SysRoleForm form) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setRoleName(form.getRoleName());
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setIssuperadmin(false);
        entity.setIsdefault(false);
        entity.setRemark(form.getRemark());
        entity.setCreateUser(UserInfoContext.getUserId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }

    public static SysRoleEntity buildUpdateEntity(SysRoleForm form) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setRoleId(form.getRoleId());
        entity.setRoleName(form.getRoleName());
        entity.setRemark(form.getRemark());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setModifyTime(new Date());
        return entity;
    }

    /**
     * 转换分页结果
     */
    public static RoleDTO buildPageDTO(SysRoleEntity record) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(record.getRoleId());
        dto.setRoleName(record.getRoleName());
        dto.setRemark(record.getRemark());
        dto.setIsdefault(record.getIsdefault());
        if (record.getIsdefault()) {
            dto.setCreateTime("-");
        } else {
            dto.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getCreateTime()));
        }
        return dto;
    }
}
