package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkSpaceUserQueryParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDetailDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceUserForm;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc
 **/
public interface InnerSysWorkSpaceUserService {

    void saveOrUpdate(SysWorkSpaceUserForm form);

    Page<WorkSpaceUserDTO> page(SysWorkSpaceUserQueryParam param);

    void batchRemove(SysWorkSpaceUserQueryParam param);

    List<KeyValueDTO> listUserDrown(Long workSpaceId, String nickName);

    WorkSpaceUserDetailDTO getDetail(Long workSpaceId, Long userId);
}
