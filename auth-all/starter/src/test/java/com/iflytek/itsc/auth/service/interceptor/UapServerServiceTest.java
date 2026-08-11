package com.iflytek.itsc.auth.service.interceptor;

import com.iflytek.itsc.auth.service.integration.UapServerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname UapServerServiceTest
 * @Description
 * @Date 2024/1/23 10:55
 * @Created by wxqiu
 */
@SpringBootTest
public class UapServerServiceTest {

    @Autowired
    private UapServerService uapServerService;

    @Test
    public void testGetToken(){
        uapServerService.getToken("tenantId");

    }

    @Test
    public void testGetUserInfo(){
        uapServerService.getUserInfo("qiuwenxuan","3d78032e-c116-4408-a225-2cb340065236");
    }
}
