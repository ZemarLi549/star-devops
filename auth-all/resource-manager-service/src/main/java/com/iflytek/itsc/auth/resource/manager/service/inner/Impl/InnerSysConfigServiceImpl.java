package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.service.base.SysConfigService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysConfigService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Service
@Slf4j
public class InnerSysConfigServiceImpl implements InnerSysConfigService {
    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public List<KeyValueDTO> listModule() {
        List<SysConfigEntity> list = sysConfigService.listSysConfigEntityByPropertyType(Constant.MODULE_TYPE);
        return list.stream().map(s -> new KeyValueDTO(s.getPropertyKey(), s.getPropertyValue())).collect(Collectors.toList());
    }
}
