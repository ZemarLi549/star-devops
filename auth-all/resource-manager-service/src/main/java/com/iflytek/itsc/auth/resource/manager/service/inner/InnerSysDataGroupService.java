package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupDeliverDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DataGroupTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceChangeFlagDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysDataGroupSaveForm;

import java.util.List;

public interface InnerSysDataGroupService {

    /**
     * 新增业务组
     */
    void insert(SysDataGroupSaveForm form);

    /**
     * 更新业务组
     */
    void update(SysDataGroupSaveForm form);

    void delete(SysDataGroupSaveForm form);

    List<DataGroupTreeDTO> dataGroupTree(SysDataGroupSaveForm form);

    List<DataGroupTreeDTO> bizTree(SysDataGroupSaveForm form);

    void moveDataGroup(SysDataGroupSaveForm form);

    /**
     * 查询所有数据单元的总数和最后变更时间，告警平台用
     */
    ResourceChangeFlagDTO getDataElementCountAndLatestTime();

    /**
     * 查询所有数据单元，告警平台用
     */
    List<DataGroupDeliverDTO> listAllDataElement();
}
