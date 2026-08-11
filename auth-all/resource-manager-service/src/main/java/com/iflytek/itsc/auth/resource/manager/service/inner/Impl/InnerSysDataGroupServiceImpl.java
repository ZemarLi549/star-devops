package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.yulichang.toolkit.JoinWrappers;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysDataGroupService;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysDataGroupBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupDeliverDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceChangeFlagDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysDataGroupSaveForm;
import com.iflytek.itsc.auth.resource.manager.service.base.SysApplicationService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysDataGroupService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysWorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/14
 * @desc
 **/
@Service
public class InnerSysDataGroupServiceImpl implements InnerSysDataGroupService {
    @Autowired
    private SysDataGroupService sysDataGroupService;
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysApplicationService sysApplicationService;

    /**
     * 新增业务组/数据单元
     */
    @Override
    @Transactional
    public void insert(SysDataGroupSaveForm form) {
        // 合法性校验
        form.setDataGroupId(null);
        if (form.getParentId() == null) form.setParentId(Constant.DEFAULT_PARENT_ID);
        this.validateGroupLegal(form);

        // 保存业务组
        SysDataGroupEntity entity = SysDataGroupBuilder.buildGroupInsertEntity(form);
        // 根据父节点获取当前所有子节点，获取排序号
        Integer sortNum = findSortNum(form.getWorkSpaceId(), form.getParentId());
        entity.setSortNum(sortNum + 1);
        sysDataGroupService.save(entity);
    }

    /**
     * 更新业务组/数据单元
     */
    @Override
    @Transactional
    public void update(SysDataGroupSaveForm form) {
        // 合法性校验
        if (form.getParentId() == null) form.setParentId(Constant.DEFAULT_PARENT_ID);
        this.validateGroupLegal(form);

        // 更新业务组
        SysDataGroupEntity entity = SysDataGroupBuilder.buildGroupUpdateEntity(form);
        // 如果父节点变了，重新排序
        SysDataGroupEntity oldEntity = sysDataGroupService.getById(form.getDataGroupId());
        if (!oldEntity.getParentId().equals(form.getParentId())) {
            Integer sortNum = findSortNum(form.getWorkSpaceId(), form.getParentId());
            entity.setSortNum(sortNum + 1);
        }
        sysDataGroupService.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(SysDataGroupSaveForm form) {
        SysDataGroupEntity oldEntity = sysDataGroupService.getById(form.getDataGroupId());
        if (oldEntity == null) {
            throw new AuthBizException("数据不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(oldEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        List<Long> idsDG = new ArrayList<>();
        if (form.getIselement()) {
            // 删除数据单元
            idsDG.add(form.getDataGroupId());
            // 删除数据单元的话，需要删除应用
            deleteDataGroup(idsDG);
        } else {
            // 删除业务组以及下属的业务组，并且将所有的数据单元到根节点
            // 查询所有业务组
            List<SysDataGroupEntity> list = sysDataGroupService.lambdaQuery()
                    .eq(SysDataGroupEntity::getIselement, false)
                    .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                    .list();

            // 查询业务组子节点
            idsDG.add(form.getDataGroupId());
            this.getGroupTreeElement(list, form.getDataGroupId(), idsDG);

            // 根据业务组id找到所有的数据单元id,这些节点都必须要移动到根节点
            List<SysDataGroupEntity> listDG = sysDataGroupService.lambdaQuery()
                    .eq(SysDataGroupEntity::getIselement, true)
                    .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                    .in(SysDataGroupEntity::getParentId, idsDG)
                    .list();
            Integer sortNum = findSortNum(form.getWorkSpaceId(), Constant.DEFAULT_PARENT_ID);
            for (int i = 0; i < listDG.size(); i++) {
                SysDataGroupEntity e = listDG.get(i);
                e.setParentId(Constant.DEFAULT_PARENT_ID);
                e.setModifyTime(new Date());
                e.setSortNum(sortNum + 1 + i);
            }
            // 删除业务组
            sysDataGroupService.deleteByIds(idsDG);
            // 批量移动数据单元到根节点
            sysDataGroupService.updateBatchById(listDG);
        }
    }

    /**
     * 查询业务组树
     */
    @Override
    public List<DataGroupTreeDTO> dataGroupTree(SysDataGroupSaveForm form) {
        Long workspaceId = form.getWorkSpaceId();
        if (!UserInfoContext.isWorkSpaceRelated(workspaceId)) {
            throw new AuthBizException("无工作空间权限");
        }
        // 所有业务组/数据单元对象
        List<SysDataGroupEntity> allDG = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getIsvalid, true)
                .eq(SysDataGroupEntity::getWorkSpaceId, workspaceId)
                .orderByAsc(SysDataGroupEntity::getSortNum)
                .list();
        if (CollectionUtils.isEmpty(allDG)) return new ArrayList<>();
        List<DataGroupTreeDTO> list = SysDataGroupBuilder.buildElementTree(allDG);
        if (CollectionUtil.isNotEmpty(list)) {
            // 一级菜单父级null，页面不展示0
            list.forEach(s -> s.setParentId(null));
        }
        return list;
    }

    /**
     * 查询新增/编辑时的上级业务组树
     */
    @Override
    public List<DataGroupTreeDTO> bizTree(SysDataGroupSaveForm form) {
        if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        // 当前空间下的全量业务组数据
        List<SysDataGroupEntity> allBizList = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getIsvalid, true)
                .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                .eq(SysDataGroupEntity::getIselement, false)
                .orderByAsc(SysDataGroupEntity::getSortNum)
                .list();
        if (CollectionUtils.isEmpty(allBizList)) return new ArrayList<>();
        List<DataGroupTreeDTO> tree = SysDataGroupBuilder.buildElementTree(allBizList);
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (CollectionUtil.isNotEmpty(tree)) {
            for (DataGroupTreeDTO dto : tree) {
                dto.setWorkSpaceName(workSpaceEntity.getWorkSpaceName());
            }
        }

        return tree;
    }

    @Override
    @Transactional
    public void moveDataGroup(SysDataGroupSaveForm form) {
        SysDataGroupEntity dataGroupEntity = sysDataGroupService.getById(form.getDataGroupId());
        if (dataGroupEntity == null) {
            throw new AuthBizException("数据不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(dataGroupEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        if (form.getParentId() == null) {
            form.setParentId(Constant.DEFAULT_PARENT_ID);
        }
        // 前端传给我的是移动的数据单元信息 dataGroupId；新放入的父节点id  parentId
        // 获取当前数据单元应该插入的位置
        Integer sortNum = findSortNum(form.getWorkSpaceId(), form.getParentId());

        // 更新数据单元
        sysDataGroupService.lambdaUpdate()
                .set(SysDataGroupEntity::getParentId, form.getParentId())
                .set(SysDataGroupEntity::getModifyTime, new Date())
                .set(SysDataGroupEntity::getSortNum, sortNum + 1)
                .eq(SysDataGroupEntity::getDataGroupId, form.getDataGroupId())
                .update();
    }

    private Integer findSortNum(Long workSpaceId, Long parentId) {
        SysDataGroupEntity allDG = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getWorkSpaceId, workSpaceId)
                .eq(SysDataGroupEntity::getParentId, parentId)
                .orderByDesc(SysDataGroupEntity::getSortNum)
                .last("limit 1")
                .one();
        // 获取最后一个数字
        Integer sortNum = 0;
        if (null != allDG) {
            sortNum = allDG.getSortNum();
        }
        return sortNum;
    }

    /**
     * 根据业务组找数据单元
     */
    private void getGroupTreeElement(List<SysDataGroupEntity> list, Long pId, List<Long> result) {
        for (SysDataGroupEntity e : list) {
            if (pId.equals(e.getParentId())) {
                result.add(e.getDataGroupId());
                getGroupTreeElement(list, e.getDataGroupId(), result);
            }
        }
    }


    /**
     * 删除数据单元
     */
    private void deleteDataGroup(List<Long> dataGroupIds) {
        // 删除应用
        sysApplicationService.deleteByDataGroupIds(dataGroupIds);

        // 删除数据单元
        sysDataGroupService.deleteByIds(dataGroupIds);
    }

    private void validateGroupLegal(SysDataGroupSaveForm form) {
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        if (form.getIselement()) {
            //数据单元名称空间下唯一
            Long count = sysDataGroupService.lambdaQuery()
                    .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                    .eq(SysDataGroupEntity::getIselement, true)
                    .eq(SysDataGroupEntity::getDataGroupName, form.getDataGroupName())
                    .ne(null != form.getDataGroupId(), SysDataGroupEntity::getDataGroupId, form.getDataGroupId())
                    .count();
            if (count > 0) {
                throw new AuthBizException("空间下存在同名数据单元！");
            }
        } else {
            //数据单元名称空间下唯一
            Long count = sysDataGroupService.lambdaQuery()
                    .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                    .eq(SysDataGroupEntity::getIselement, false)
                    .eq(SysDataGroupEntity::getDataGroupName, form.getDataGroupName())
                    .ne(null != form.getDataGroupId(), SysDataGroupEntity::getDataGroupId, form.getDataGroupId())
                    .count();
            if (count > 0) {
                throw new AuthBizException("空间下存在同名业务组！");
            }
        }

        if (form.getDataGroupId() != null) {
            if (form.getDataGroupId().equals(form.getParentId())) {
                throw new AuthBizException("请选择除自己以外的业务组为父节点！");
            }
            SysDataGroupEntity dataGroupEntity = sysDataGroupService.getById(form.getDataGroupId());
            if (dataGroupEntity == null) {
                throw new AuthBizException("业务组不存在");
            }
        }
        if (form.getParentId() != null && !Constant.DEFAULT_PARENT_ID.equals(form.getParentId())) {
            SysDataGroupEntity parentEntity = sysDataGroupService.getById(form.getParentId());
            if (parentEntity == null) {
                throw new AuthBizException("父节点不存在");
            } else if (parentEntity.getIselement()) {
                throw new AuthBizException("数据单元下不能挂载业务组");
            }
        }
    }

    /**
     * 查询所有数据单元的总数和最后变更时间，告警平台用
     */
    @Override
    public ResourceChangeFlagDTO getDataElementCountAndLatestTime() {
        ResourceChangeFlagDTO dto = JoinWrappers.lambda(SysDataGroupEntity.class)
                .selectCount(SysDataGroupEntity::getDataGroupId, ResourceChangeFlagDTO::getCount)
                .selectMax(SysDataGroupEntity::getModifyTime, ResourceChangeFlagDTO::getModifyTime)
                .eq(SysDataGroupEntity::getIselement, true)
                .one(ResourceChangeFlagDTO.class);
        if (dto != null && dto.getModifyTime() != null) {
            dto.setModifyTimeLong(dto.getModifyTime().getTime());
        }
        return dto;
    }

    /**
     * 查询所有数据单元，告警平台用
     */
    @Override
    public List<DataGroupDeliverDTO> listAllDataElement() {
        return JoinWrappers.lambda(SysDataGroupEntity.class)
                .selectAs(SysDataGroupEntity::getDataGroupId, DataGroupDeliverDTO::getDataGroupId)
                .selectAs(SysDataGroupEntity::getDataGroupToken, DataGroupDeliverDTO::getDataGroupToken)
                .selectAs(SysDataGroupEntity::getDataGroupName, DataGroupDeliverDTO::getDataGroupName)
                .selectAs(SysDataGroupEntity::getWorkSpaceId, DataGroupDeliverDTO::getWorkSpaceId)
                .eq(SysDataGroupEntity::getIselement, true)
                .list(DataGroupDeliverDTO.class);
    }
}
