package com.iflytek.itsc.auth.resource.manager.domain.form;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @Classname DeployInfoAndMenuForm
 * @Description TODO
 * @Date 2024/10/21 10:30
 * @Created by wxqiu
 */
@Data
public class DeployMetaInfoAndMenuForm {

    @Valid
    private DeployMetaInfoForm meta;

    @Valid
    private List<SysMenuForm> menus;



}


