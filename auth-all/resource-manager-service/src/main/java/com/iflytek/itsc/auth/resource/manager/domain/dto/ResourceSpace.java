package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author xdkong2
 * @date 2024/3/18
 * @desc 空间
 **/
@Data
public class ResourceSpace implements Serializable {
    private static final long serialVersionUID = -1891206765537799689L;

    /**
     * 工作空间id
     */
    private Long workSpaceId;
    /**
     * 工作空间名称
     */
    private String workSpaceName;

    /**
     * 业务组树
     */
    private List<ResourceSpaceDataGroup> dataGroupMap;
    /**
     * 应用列表树,key=数据单元token
     */
    private Map<String, List<ResourceSpaceApplication>> applicationMap;
}
