package com.iflytek.itsc.auth.resource.manager.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc
 **/
@Data
public class SysWorkSpaceUserQueryParam extends BasePageParam implements Serializable {
    private static final long serialVersionUID = -9139580563488418854L;

    private Long workSpaceId;
    private String nickName;
    private List<Long> userIdList;
}
