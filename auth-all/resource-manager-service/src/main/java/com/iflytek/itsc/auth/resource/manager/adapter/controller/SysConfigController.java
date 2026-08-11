package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysConfigService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@RestController
@RequestMapping("/sys/config")
public class SysConfigController {
    @Autowired
    private InnerSysConfigService innerSysConfigService;

    /**
     * 查询子平台
     */
    @GetMapping("/listModule")
    public RestResponse<List<KeyValueDTO>> listModule() {
        List<KeyValueDTO> list = innerSysConfigService.listModule();
        return RestResponse.buildSuccess(list);
    }
}
