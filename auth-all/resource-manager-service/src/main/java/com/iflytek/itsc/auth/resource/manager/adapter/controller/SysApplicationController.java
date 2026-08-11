package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysApplicationService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysApplicationPageParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ApplicationDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationSaveForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@RestController
@RequestMapping("/sys/application")
public class SysApplicationController {
    @Autowired
    private InnerSysApplicationService innerSysApplicationService;

    @PostMapping("/insert")
    public RestResponse<Object> insert(@Validated(AddGroup.class) @RequestBody SysApplicationSaveForm form) {
        innerSysApplicationService.insert(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 更新应用
     */
    @PostMapping("/update")
    public RestResponse<Object> update(@Validated(UpdateGroup.class) @RequestBody SysApplicationSaveForm form) {
        innerSysApplicationService.update(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除应用(批量)
     */
    @PostMapping("/delete")
    public RestResponse<Object> delete( @RequestBody SysApplicationDeleteForm form) {
        innerSysApplicationService.delete(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public RestResponse<Page<ApplicationDTO>> page(@RequestBody SysApplicationPageParam form) {
        Page<ApplicationDTO> list = innerSysApplicationService.page(form);
        return RestResponse.buildSuccess(list);
    }
}
