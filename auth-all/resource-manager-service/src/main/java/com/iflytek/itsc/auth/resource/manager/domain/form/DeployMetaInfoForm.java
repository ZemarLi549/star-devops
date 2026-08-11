package com.iflytek.itsc.auth.resource.manager.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname DeployMetaInfoForm
 * @Description TODO
 * @Date 2024/10/21 16:56
 * @Created by wxqiu
 */
@Data
public class DeployMetaInfoForm {

    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    @NotBlank(message = "模块url不能为空")
    private String moduleUrl;
}
