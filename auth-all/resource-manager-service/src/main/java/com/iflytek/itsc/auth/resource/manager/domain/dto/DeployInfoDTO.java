package com.iflytek.itsc.auth.resource.manager.domain.dto;

import cn.hutool.core.codec.Base64;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDeployMetaInfoEntity;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname DeployInfoDTO
 * @Description TODO
 * @Date 2024/10/17 14:26
 * @Created by wxqiu
 */
@Data
public class DeployInfoDTO {

    private String deployMode;

    private boolean menuClosed;

    private List<Meta> metaList;


    public DeployInfoDTO buildMetaList(List<SysDeployMetaInfoEntity> sysDeployMetaInfoEntities) {
        List<Meta> metas = sysDeployMetaInfoEntities.stream().map(sysDeployInfoEntity -> {
            Meta meta = new Meta();
            meta.setModuleName(sysDeployInfoEntity.getModuleName());
            meta.setModuleUrl(Base64.encode(sysDeployInfoEntity.getModuleUrl()));
            return meta;
        }).collect(Collectors.toList());
        this.setMetaList(metas);
        return this;
    }
}


@Data
class Meta {
    private String moduleName;

    private String moduleUrl;
}


