package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;

import java.util.List;

public interface InnerSysConfigService {

    /**
     * 查询子平台列表
     */
    List<KeyValueDTO> listModule();
}
