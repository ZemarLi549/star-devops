package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysUserService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysWorkSpaceUserService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkSpaceUserQueryParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDetailDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc 空间下的用户管理
 **/
@RestController
@RequestMapping("/sys/workSpace/user")
public class SysWorkSpaceUserController {
    @Autowired
    private InnerSysWorkSpaceUserService innerSysWorkSpaceUserService;
    @Autowired
    private InnerSysUserService innerSysUserService;

    /**
     * 添加成员时，查询成员列表
     */
    @GetMapping("/listUserDrown")
    public RestResponse<List<KeyValueDTO>> listUserDrown(Long workSpaceId, String nickName) {
        List<KeyValueDTO> list = innerSysWorkSpaceUserService.listUserDrown(workSpaceId, nickName);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 新增成员 同用户池，但空间只选一个
     */
    @PostMapping("/insertUser")
    public RestResponse<Object> insertUser(@Validated(AddGroup.class) @RequestBody SysUserForm form) {
        innerSysUserService.insertUser(form, true);
        return RestResponse.buildSuccess();
    }

    /**
     * 添加成员
     */
    @PostMapping("/save")
    public RestResponse<Object> save(@Validated @RequestBody SysWorkSpaceUserForm form) {
        innerSysWorkSpaceUserService.saveOrUpdate(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 分页查询成员列表
     */
    @PostMapping("/page")
    public RestResponse<Page<WorkSpaceUserDTO>> page(@Validated @RequestBody SysWorkSpaceUserQueryParam param) {
        Page<WorkSpaceUserDTO> page = innerSysWorkSpaceUserService.page(param);
        return RestResponse.buildSuccess(page);
    }

    /**
     * 批量移除
     */
    @PostMapping("/batchRemove")
    public RestResponse<Object> batchRemove(@RequestBody SysWorkSpaceUserQueryParam param) {
        innerSysWorkSpaceUserService.batchRemove(param);
        return RestResponse.buildSuccess();
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/getDetail")
    public RestResponse<WorkSpaceUserDetailDTO> getDetail(Long workSpaceId, Long userId) {
        WorkSpaceUserDetailDTO dto = innerSysWorkSpaceUserService.getDetail(workSpaceId, userId);
        return RestResponse.buildSuccess(dto);
    }
}
