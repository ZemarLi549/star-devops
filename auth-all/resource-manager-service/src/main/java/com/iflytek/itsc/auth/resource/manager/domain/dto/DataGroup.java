package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname DataGroupResponse
 * @Date 2024/3/11 14:24
 * @Created by wxqiu
 */
@Data
@Builder
public class DataGroup {

    private String dataGroupToken;

    private String dataGroupName;
}
