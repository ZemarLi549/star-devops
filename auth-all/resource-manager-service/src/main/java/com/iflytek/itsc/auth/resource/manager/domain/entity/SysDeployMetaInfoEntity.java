package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Classname SysDeployInfoEntity
 * @Description TODO
 * @Date 2024/10/17 14:17
 * @Created by wxqiu
 */
@Data
@TableName("sys_deploy_meta_info")
public class SysDeployMetaInfoEntity {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private  String moduleName;

    private  String moduleUrl;

    private Date createTime;

    private Date modifyTime;


}
