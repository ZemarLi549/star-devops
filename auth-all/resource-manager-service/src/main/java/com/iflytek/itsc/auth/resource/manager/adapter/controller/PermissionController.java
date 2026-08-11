package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.dto.*;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.vo.BasePageParam;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerPermissionService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysDataGroupService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysUserService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.InnerPermissionForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.PermissionElementForm;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc
 **/
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private InnerPermissionService innerPermissionService;
    @Autowired
    private InnerSysUserService innerSysUserService;
    @Autowired
    private InnerSysDataGroupService innerSysDataGroupService;


    /**
     * 查询指定用户
     */
    @PostMapping("/listUserByIds")
    public RestResponse<List<SysUserEntity>> listUser(@RequestBody InnerPermissionForm form) {
        List<SysUserEntity> list = innerPermissionService.listUser(form.getUserIds());
        return RestResponse.buildSuccess(list);
    }


    /**
     * 查询用户基础信息，及绑定的空间id
     */
    @PostMapping("/listOtherServiceUserInfo")
    public RestResponse<List<OtherServiceUserInfoDTO>> listOtherServiceUserInfo(@RequestBody InnerPermissionForm form) {
        List<OtherServiceUserInfoDTO> list = innerPermissionService.listOtherServiceUserInfo(form);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/listUsers")
    public RestResponse<List<SysUserEntity>> listAllUsers() {
        List<SysUserEntity> list = innerPermissionService.listAllUsers();
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询所有用户中最后更新的用户时间，以及用户总数
     */
    @GetMapping("/userCountAndLatestTime")
    public RestResponse<ResourceChangeFlagDTO> userCountAndLatestTime() {
        return RestResponse.buildSuccess(innerPermissionService.userCountAndLatestTime());
    }

    /**
     * 查询所有数据单元的总数和最后变更时间，告警平台用
     */
    @GetMapping("/getDataElementCountAndLatestTime")
    public RestResponse<ResourceChangeFlagDTO> getDataElementCountAndLatestTime() {
        return RestResponse.buildSuccess(innerSysDataGroupService.getDataElementCountAndLatestTime());
    }

    /**
     * 查询所有数据单元，告警平台用
     */
    @GetMapping("/listAllDataElement")
    public RestResponse<List<DataGroupDeliverDTO>> listAllDataElement() {
        return RestResponse.buildSuccess(innerSysDataGroupService.listAllDataElement());
    }

    /**
     * 分页查询用户
     */
    @PostMapping("/user/page")
    public RestResponse<Page<UserDTO>> page(@RequestBody SysUserPageParam form) {
        Page<UserDTO> list = innerSysUserService.page(form);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询业务组树-带数据单元
     */
    @GetMapping("/dataGroupTree")
    public RestResponse<List<DataGroupTreeDTO>> dataGroupTree(Long workSpaceId) {
        List<DataGroupTreeDTO> tree = innerPermissionService.dataGroupTree(workSpaceId);
        return RestResponse.buildSuccess(tree);
    }

    /**
     * 新增数据单元
     */
    @PostMapping("/insertElement")
    public RestResponse<SysDataGroupEntity> insertElement(@Validated @RequestBody PermissionElementForm form) {
        SysDataGroupEntity entity = innerPermissionService.insertElement(form);
        return RestResponse.buildSuccess(entity);
    }

    /**
     * 查询用户空间表总数和最后更新时间
     */
    @GetMapping("/userSpaceCountAndLatestTime")
    public RestResponse<ResourceChangeFlagDTO> userSpaceCountAndLatestTime() {
        return RestResponse.buildSuccess(innerPermissionService.userSpaceCountAndLatestTime());
    }

    /**
     * 分页查询用户空间表，根据空间id排序
     */
    @PostMapping("/userSpace/page")
    public RestResponse<Page<SysUserWorkSpaceEntity>> userSpacePage(@RequestBody BasePageParam pageParam) {
        return RestResponse.buildSuccess(innerPermissionService.userSpacePage(pageParam));
    }
}
