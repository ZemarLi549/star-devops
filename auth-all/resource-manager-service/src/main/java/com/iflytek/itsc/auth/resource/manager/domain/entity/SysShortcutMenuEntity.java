package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Classname SysShortcutMenuEntity
 * @Description 快捷菜单实体类
 * @Date 2024/8/22 13:54
 * @Created by wxqiu
 */
@Data
@TableName("sys_shortcut_menu")
public class SysShortcutMenuEntity {

    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 菜单路径
     */
    private String menuPath;
    /**
     * 创建者
     */
    private String account;
    /**
     * 工作空间id
     */
    private Long workspaceId;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifyTime;
}
