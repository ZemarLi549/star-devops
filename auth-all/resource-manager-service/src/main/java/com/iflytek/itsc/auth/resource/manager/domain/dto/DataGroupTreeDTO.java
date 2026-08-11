package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class DataGroupTreeDTO {

    private Long dataGroupId;

    private String dataGroupName;

    /**
     * 工作空间id
     */
    private Long workSpaceId;
    private String workSpaceName;
    /**
     * 数据单元token
     */
    private String dataGroupToken;
    /**
     * 父id
     */
    private Long parentId;

    /**
     * 是否数据单元
     */
    private Boolean iselement;
    /**
     * 序号
     */
    private Integer sortNum;
    /**
     * 是否选中
     */
    private boolean isselect;
    /**
     * 描述
     */
    private String remark;

    private List<DataGroupTreeDTO> child;
}
