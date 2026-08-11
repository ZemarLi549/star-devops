package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.dto.DeployInfoDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDeployMetaInfoEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.DeployMetaInfoForm;
import com.iflytek.itsc.auth.resource.manager.service.base.SysConfigService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysDeployInfoService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerDeployInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Classname InnerDeployInfoServiceImpl
 * @Description TODO
 * @Date 2024/10/21 16:45
 * @Created by wxqiu
 */
@Service
@Slf4j
public class InnerDeployInfoServiceImpl implements InnerDeployInfoService {

    @Autowired
    SysDeployInfoService sysDeployInfoService;

    @Autowired
    SysConfigService sysConfigService;

    @Value("${nginx.url:http://127.0.0.1}")
    private String nginxUrl;
    @Value("${deploy_mode:all}")
    private String deployMode;

    @Override
    public DeployInfoDTO listDeployInfo() {
        List<SysDeployMetaInfoEntity> sysDeployInfoEntities = sysDeployInfoService.list();
        if (CollectionUtil.isEmpty(sysDeployInfoEntities)) {
            throw new AuthBizException("部署模块信息为空");
        }
        DeployInfoDTO deployInfoDTO = new DeployInfoDTO();
        int deployModuleSize = sysConfigService.listModuleKeys(Constant.DEPLOY_MODULE).size();
        if (1 == sysDeployInfoEntities.size() && deployModuleSize != sysDeployInfoEntities.size()) {
            deployInfoDTO.setMenuClosed(false);
        } else if (deployModuleSize > sysDeployInfoEntities.size()) {
            deployInfoDTO.setMenuClosed(true);
        } else if (deployModuleSize == sysDeployInfoEntities.size()) {
            deployInfoDTO.setMenuClosed(true);
        } else {
            throw new AuthBizException("部署模块信息和数据库不匹配");
        }
        // 部署模式：是否独立部署 根据环境变量来
        deployInfoDTO.setDeployMode(Constant.DEPLOY_MODE_SINGLE.equals(deployMode) ? deployMode : Constant.DEPLOY_MODE_ALL);
        deployInfoDTO.buildMetaList(sysDeployInfoEntities);
        return deployInfoDTO;
    }

    @Override
    public void saveDeployInfo(DeployMetaInfoForm deployMetaInfoForm) {
        //校验moduleName 的合法性
        Set<String> deployModuleKeys = sysConfigService.listModuleKeys(Constant.DEPLOY_MODULE);
        if (!deployModuleKeys.contains(deployMetaInfoForm.getModuleName())) {
            throw new AuthBizException("模块名称不合法");
        }
        //根据moduleName 判断是更新还是新增
        SysDeployMetaInfoEntity sysDeployMetaInfoEntityOld = sysDeployInfoService.findByModuleName(deployMetaInfoForm.getModuleName());
        if (null != sysDeployMetaInfoEntityOld) {
            //更新
            sysDeployMetaInfoEntityOld.setModuleUrl(nginxUrl.trim() + deployMetaInfoForm.getModuleUrl());
            sysDeployMetaInfoEntityOld.setModifyTime(new Date());
            sysDeployInfoService.updateById(sysDeployMetaInfoEntityOld);
        } else {
            //新增
            SysDeployMetaInfoEntity sysDeployMetaInfoEntity = new SysDeployMetaInfoEntity();
            sysDeployMetaInfoEntity.setModuleName(deployMetaInfoForm.getModuleName());
            sysDeployMetaInfoEntity.setModuleUrl(nginxUrl.trim() +deployMetaInfoForm.getModuleUrl());
            sysDeployInfoService.save(sysDeployMetaInfoEntity);
        }
    }

    @Override
    public void deleteByModuleName(String moduleName) {
        sysDeployInfoService.deleteByModuleName(moduleName);
    }
}
