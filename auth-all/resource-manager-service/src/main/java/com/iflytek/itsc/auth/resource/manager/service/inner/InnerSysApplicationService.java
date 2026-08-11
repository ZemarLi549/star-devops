package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysApplicationPageParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ApplicationDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationSaveForm;

public interface InnerSysApplicationService {

    void insert(SysApplicationSaveForm form);

    void update(SysApplicationSaveForm form);

    void delete(SysApplicationDeleteForm form);

    Page<ApplicationDTO> page(SysApplicationPageParam form);
}
