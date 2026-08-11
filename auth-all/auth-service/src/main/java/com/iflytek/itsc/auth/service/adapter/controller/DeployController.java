package com.iflytek.itsc.auth.service.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.domain.form.DeployMetaInfoAndMenuForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.SaveBatchInitGroup;
import com.iflytek.itsc.auth.service.integration.ResourceManagerService;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname DeployController
 * @Description TODO
 * @Date 2024/10/17 16:16
 * @Created by wxqiu
 */
@RestController
public class DeployController {

    @Autowired
    ResourceManagerService resourceManagerService;

    @GetMapping("/meta")
    public RestResponse listDeployInfo() {
        return RestResponse.buildSuccess(resourceManagerService.listDeployInfo());
    }


    @PostMapping("/metaAndMenu/{moduleName}")
    public RestResponse saveDeployMetaInfoAndMenu(@PathVariable("moduleName") String moduleName) {
        resourceManagerService.saveDeployMetaInfoAndMenu(moduleName);
        return RestResponse.buildSuccess();
    }

    @DeleteMapping("/metaAndMenu/{moduleName}")
    public RestResponse deleteDeployMetaInfoAndMenu(@PathVariable("moduleName") String moduleName) {
        resourceManagerService.deleteDeployMetaInfoAndMenu(moduleName);
        return RestResponse.buildSuccess();
    }


}
