package com.iflytek.itsc.auth.resource.manager.domain.vo;

import lombok.Data;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Data
public class BasePageParam {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
