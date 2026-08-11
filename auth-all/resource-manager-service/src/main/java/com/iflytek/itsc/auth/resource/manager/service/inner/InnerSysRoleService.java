package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.vo.RoleQueryParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.RoleDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysRoleForm;

import java.util.List;

public interface InnerSysRoleService {

    void insert(SysRoleForm form);

    void update(SysRoleForm form);

    void delete(SysRoleForm form);

    Page<RoleDTO> page(RoleQueryParam param);

    /**
     * 查询参考角色
     */
    List<KeyValueDTO> listTemplate();

    RoleDTO getDetail(Long roleId);

    /**
     * 查询授权菜单树
     */
    List<MenuTreeDTO> getAuthMenuTree(Long workSpaceId, Long roleId);

    List<KeyValueDTO> listSpaceTotal(Long workSpaceId);
}
