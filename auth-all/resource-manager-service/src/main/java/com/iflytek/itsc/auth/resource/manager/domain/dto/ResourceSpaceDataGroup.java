package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 工作组、数据空间
 **/
@Data
public class ResourceSpaceDataGroup {
    /**
     * id
     */
    private Long dataGroupId;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 空间id
     */
    private Long workSpaceId;
    /**
     * 工作组名称
     */
    private String dataGroupName;
    /**
     * 数据单元token
     */
    private String dataGroupToken;
    /**
     * 是否是数据单元
     */
    private boolean iselement;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 子集
     */
    private List<ResourceSpaceDataGroup> children;
}
