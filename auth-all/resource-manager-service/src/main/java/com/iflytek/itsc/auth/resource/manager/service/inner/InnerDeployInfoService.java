package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.iflytek.itsc.auth.resource.manager.domain.dto.DeployInfoDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.DeployMetaInfoForm;

/**
 * @Classname InnerDeployInfoService
 * @Description TODO
 * @Date 2024/10/21 16:45
 * @Created by wxqiu
 */
public interface InnerDeployInfoService {

    DeployInfoDTO listDeployInfo();

    void saveDeployInfo(DeployMetaInfoForm deployMetaInfoForm);
    
    void deleteByModuleName(String moduleName);
}
