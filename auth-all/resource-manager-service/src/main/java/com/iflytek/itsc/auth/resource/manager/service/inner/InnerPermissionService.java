package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.dto.*;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.InnerPermissionForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.PermissionElementForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.BasePageParam;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;

import java.util.List;

public interface InnerPermissionService {

    /**
     * 查询用户权限信息
     */
    ResourceUser getUserPermission(String account);
    /**
     * 设置用户登录过
     */
    void updateLogged(Long userId);

    List<SysUserEntity> listUser(List<Long> userIds);

    List<SysUserEntity> listAllUsers();

    ResourceChangeFlagDTO userCountAndLatestTime();

    void updateLoginTime(Long userId);

    void updatePassword(SysUserPageParam form);

    void updateUser(SysUserForm form);

    /**
     * uap/sso新增用户
     */
    ResourceUser insertUser(SysUserForm form);
    /**
     * 获取当前工作空间下的所有数据单元列表*
     * @param workspaceId
     * @return
     */
    List<SysDataGroupEntity> findDataGroups(Long workspaceId);

    /**
     * 获取当前工作空间下的所有应用信息*
     * @param workspaceId
     * @return
     */
    List<ResourceSpaceApplication> findApplications(Long workspaceId);

    /**
     * 查询当前用户的工作空间列表*
     * @param userId
     * @return
     */
    List<ResourceUserWorkSpace> listWorkspaces(Long userId);

    /**
     *查询当前用户当前工作空间下的菜单树信息*
     * @param userId
     * @param workspaceId
     * @return
     */
    List<ResourceUserMenuDetail> listMenus(Long userId, Long workspaceId, String deployMode);

    /**
     * 查询业务组树-带数据单元
     */
    List<DataGroupTreeDTO> dataGroupTree(Long workSpaceId);

    /**
     * 新增数据单元
     */
    SysDataGroupEntity insertElement(PermissionElementForm form);

    /**
     * 更新用户新手指引状态
     */
    void updateGuideState(Long userId, String guideState);

    /**
     * 校验数据完整性
     *
     * @param userId
     */
    void validateIntegrality(Long userId);

    /**
     * 初始化温州/利川 加密数据
     */
    void initCipherData();


    /**
     * 查询用户基础信息，及绑定的空间id
     */
    List<OtherServiceUserInfoDTO> listOtherServiceUserInfo(InnerPermissionForm form);

    DeployInfoDTO listDeployInfo();

    void saveDeployMetaInfoAndMenu(String moduleName);

    void deleteDeployMetaInfoAndMenu(String moduleName);

    /**
     * 查询用户空间表总数和最后更新时间
     */
    ResourceChangeFlagDTO userSpaceCountAndLatestTime();

    /**
     * 分页查询用户空间表，根据空间id排序
     */
    Page<SysUserWorkSpaceEntity> userSpacePage(BasePageParam pageParam);
}
