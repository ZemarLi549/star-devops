package com.iflytek.itsc.auth.service.domain.dto;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysShortcutMenuEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Classname FirstLevelMenu
 * @Description 快捷菜单
 * @Date 2023/12/18 16:49
 * @Created by wxqiu
 */
@Data
public class FirstLevelMenu {
    /**
     * 菜单id
     */
    @NotBlank
    private String menuId;
    /**
     * 菜单名称
     */
    private String menuName;

    @NotBlank
    private String menuPath;


    public SysShortcutMenuEntity toSysShortcutMenuEntity() {
        Long workspaceId = UserInfoContext.getWorkspaceId();
        String account = UserInfoContext.getAccount();
        SysShortcutMenuEntity entity = new SysShortcutMenuEntity();
        entity.setMenuId(Long.valueOf(this.menuId));
        entity.setMenuPath(this.menuPath);
        entity.setAccount(account);
        entity.setWorkspaceId(workspaceId);
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }
}
