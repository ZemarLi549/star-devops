package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkspaceParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyLongValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.SpaceDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceSaveForm;

import java.util.List;

public interface InnerSysWorkSpaceService {

    /**
     * 新增工作空间
     */
    void insertWorkSpace(SysWorkSpaceSaveForm form);

    void updateWorkSpace(SysWorkSpaceSaveForm form);

    void deleteWorkSpace(SysWorkSpaceSaveForm form);

    List<SpaceDTO> list(SysWorkspaceParam form);

    /**
     * 全量的空间列表下拉
     */
    List<KeyLongValueDTO> listTotalDrown();
}
