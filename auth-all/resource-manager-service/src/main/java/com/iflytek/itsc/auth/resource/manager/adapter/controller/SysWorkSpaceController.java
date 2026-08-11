package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysWorkSpaceService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkspaceParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyLongValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.SpaceDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceSaveForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc 工作空间
 **/
@RestController
@RequestMapping("/sys/workSpace")
public class SysWorkSpaceController {
    @Autowired
    private InnerSysWorkSpaceService innerSysWorkSpaceService;

    @PostMapping("/insert")
    public RestResponse<Object> insertWorkSpace(@Validated(AddGroup.class) @RequestBody SysWorkSpaceSaveForm form) {
        innerSysWorkSpaceService.insertWorkSpace(form);
        return RestResponse.buildSuccess();
    }

    @PostMapping("/update")
    public RestResponse<Object> updateWorkSpace(@Validated(UpdateGroup.class) @RequestBody SysWorkSpaceSaveForm form) {
        innerSysWorkSpaceService.updateWorkSpace(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除空间
     */
    @PostMapping("/delete")
    public RestResponse<Object> deleteWorkSpace(@RequestBody SysWorkSpaceSaveForm form) {
        innerSysWorkSpaceService.deleteWorkSpace(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 列表查询
     */
    @PostMapping("/list")
    public RestResponse<List<SpaceDTO>> list(@RequestBody SysWorkspaceParam form) {
        List<SpaceDTO> list = innerSysWorkSpaceService.list(form);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 全量的空间列表下拉
     */
    @GetMapping("/listTotalDrown")
    public RestResponse<List<KeyLongValueDTO>> listTotalDrown() {
        List<KeyLongValueDTO> list = innerSysWorkSpaceService.listTotalDrown();
        return RestResponse.buildSuccess(list);
    }
}
