package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.utils.UUIDUtil;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.PermissionElementForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysDataGroupSaveForm;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysDataGroupBuilder {

    private static String PREFIX_TOKEN = "tok_";

    public static SysDataGroupEntity buildGroupInsertEntity(SysDataGroupSaveForm form) {
        SysDataGroupEntity entity = new SysDataGroupEntity();
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setDataGroupName(form.getDataGroupName());
        entity.setIselement(form.getIselement());
        entity.setParentId(null == form.getParentId() ? Constant.DEFAULT_PARENT_ID : form.getParentId());
        entity.setDataGroupToken(form.getIselement()
                ? PREFIX_TOKEN + UUIDUtil.getRandomUUID().toLowerCase() : "");
        entity.setCreateUser(UserInfoContext.getUserId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        entity.setRemark(form.getRemark());
        return entity;
    }

    public static SysDataGroupEntity buildPermissionElementInsertEntity(PermissionElementForm form) {
        SysDataGroupEntity entity = new SysDataGroupEntity();
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setDataGroupName(form.getDataGroupName());
        entity.setIselement(true);
        entity.setParentId(Constant.DEFAULT_PARENT_ID);
        entity.setDataGroupToken(PREFIX_TOKEN + UUIDUtil.getRandomUUID().toLowerCase());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }

    public static SysDataGroupEntity buildGroupUpdateEntity(SysDataGroupSaveForm form) {
        SysDataGroupEntity entity = new SysDataGroupEntity();
        entity.setDataGroupId(form.getDataGroupId());
        entity.setDataGroupName(form.getDataGroupName());
        entity.setIselement(form.getIselement());
        entity.setParentId(form.getParentId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setModifyTime(new Date());
        entity.setRemark(form.getRemark());
        return entity;
    }

    /**
     * 组装业务组树
     */
    public static List<DataGroupTreeDTO> buildElementTree(List<SysDataGroupEntity> list) {
        // 组装树
        Map<Long, DataGroupTreeDTO> id2NodeMap = new LinkedHashMap<>();
        for (SysDataGroupEntity entity : list) {
            DataGroupTreeDTO dto = new DataGroupTreeDTO();
            dto.setDataGroupId(entity.getDataGroupId());
            dto.setDataGroupName(entity.getDataGroupName());
            dto.setDataGroupToken(entity.getDataGroupToken());
            dto.setSortNum(entity.getSortNum());
            dto.setIselement(entity.getIselement());
            dto.setIsselect(false);
            dto.setParentId(entity.getParentId());
            dto.setWorkSpaceId(entity.getWorkSpaceId());
            dto.setRemark(entity.getRemark());
            dto.setChild(new ArrayList<>());
            id2NodeMap.put(entity.getDataGroupId(), dto);
        }

        // 将子集放入父级children中
        for (Map.Entry<Long, DataGroupTreeDTO> nodeEntry : id2NodeMap.entrySet()) {
            DataGroupTreeDTO node = nodeEntry.getValue();
            if (id2NodeMap.containsKey(node.getParentId())) {
                DataGroupTreeDTO parentNode = id2NodeMap.get(node.getParentId());
                parentNode.getChild().add(node);
            }
        }

        // 只取一级节点
        return id2NodeMap.values().stream()
                .filter(s -> Constant.DEFAULT_PARENT_ID.equals(s.getParentId()))
                .collect(Collectors.toList());
    }
}
