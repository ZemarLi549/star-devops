package com.iflytek.itsc.auth.resource.manager.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SpaceDTO {

    /**
     * id
     */
    private Long workSpaceId;

    /**
     * 名称
     */
    private String workSpaceName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 空间成员数
     */
    private Long count;
    /**
     * 是否默认
     */
    private Boolean isdefault;
}
