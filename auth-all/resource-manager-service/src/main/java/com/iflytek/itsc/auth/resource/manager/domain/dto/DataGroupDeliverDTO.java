package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据单元dto，数据同步用
 */
@Data
public class DataGroupDeliverDTO implements Serializable {
    private static final long serialVersionUID = -5823083087930481928L;

    private Long dataGroupId;

    private String dataGroupName;

    private Long workSpaceId;

    private String dataGroupToken;
}
