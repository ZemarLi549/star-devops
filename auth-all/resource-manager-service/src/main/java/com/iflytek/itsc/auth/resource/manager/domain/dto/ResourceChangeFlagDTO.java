package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xdkong2
 * @date 2024/3/18
 * @desc 资源数据变更标记，数量/最后更新时间变更，则表示资源变化了，需要重新同步
 **/
@Data
public class ResourceChangeFlagDTO implements Serializable {
    private static final long serialVersionUID = -6345879153441922320L;

    /**
     * 数量
     */
    private long count;
    /**
     * 最后更新时间
     */
    private Date modifyTime;
    /**
     * 最后更新时间-long
     */
    private long modifyTimeLong;
}
