package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysDataGroupService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysDataGroupSaveForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/14
 * @desc
 **/
@RestController
@RequestMapping("/sys/dataGroup")
public class SysDataGroupController {
    @Autowired
    private InnerSysDataGroupService innerSysDataGroupService;

    /**
     * 新增业务组
     */
    @PostMapping("/insert")
    public RestResponse<Object> insert(@Validated(AddGroup.class) @RequestBody SysDataGroupSaveForm form) {
        innerSysDataGroupService.insert(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 更新业务组
     */
    @PostMapping("/update")
    public RestResponse<Object> update(@Validated(UpdateGroup.class) @RequestBody SysDataGroupSaveForm form) {
        innerSysDataGroupService.update(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除业务组
     */
    @PostMapping("/delete")
    public RestResponse<Object> delete( @RequestBody SysDataGroupSaveForm form) {
        innerSysDataGroupService.delete(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 业务组树,带数据单元
     */
    @PostMapping("/dataGroupTree")
    public RestResponse<List<DataGroupTreeDTO>> dataGroupTree(@RequestBody SysDataGroupSaveForm form) {
        List<DataGroupTreeDTO> tree = innerSysDataGroupService.dataGroupTree(form);
        return RestResponse.buildSuccess(tree);
    }


    /**
     * 拖动数据单元，树重新排序
     */
    @PostMapping("/moveDataGroup")
    public RestResponse<Object> moveDataGroup(@RequestBody SysDataGroupSaveForm form) {
        innerSysDataGroupService.moveDataGroup(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 获取业务组树，不带数据单元
     */
    @PostMapping("/bizTree")
    public RestResponse<List<DataGroupTreeDTO>> bizTree(@RequestBody SysDataGroupSaveForm form) {
        List<DataGroupTreeDTO> tree = innerSysDataGroupService.bizTree(form);
        return RestResponse.buildSuccess(tree);
    }
}
