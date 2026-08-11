package com.iflytek.itsc.auth.resource.manager.domain.vo;

import lombok.Data;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Data
public class SysApplicationPageParam extends BasePageParam{
    private String name;
    /**
     * 工作空间id
     */
    private Long workSpaceId;

    /**
     * 数据单元id
     */
    private Long dataGroupId;

}
