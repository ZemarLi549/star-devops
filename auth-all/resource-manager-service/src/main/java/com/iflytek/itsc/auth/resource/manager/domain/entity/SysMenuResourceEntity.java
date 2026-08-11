package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单接口关系表
 */
@Data
@TableName("sys_menu_resource")
public class SysMenuResourceEntity implements Serializable {
    private static final long serialVersionUID = 2555715637872327249L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 请求方法
     */
   private String method;
    /**
     * 路径url
     */
   private String path;
    /**
     * 创建时间
     */
    private Date createTime;
}