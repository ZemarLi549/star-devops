package com.iflytek.itsc.auth.service.integration;

import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUser;
import com.iflytek.itsc.auth.service.domain.dto.UserPermissionRequest;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Classname ResourceManagerServiceTest
 * @Description 权限资源管理服务相关测试类
 * @Date 2024/3/6 10:12
 * @Created by wxqiu
 */
@SpringBootTest
public class ResourceManagerServiceTest {

    @Autowired
    ResourceManagerService resourceManagerService;
    @Test
    public void test() {
        UserPermissionRequest userPermissionRequest = new UserPermissionRequest();
        userPermissionRequest.setAccount("admin");
        ResourceUser resourceUser = resourceManagerService.getResourceUser(userPermissionRequest);
        assertThat(resourceUser.getAccount()).isEqualTo("admin");
    }

//    @Test
    public void test1() {
        InsertUserInfo insertUserInfo = new InsertUserInfo();
        insertUserInfo.setEmail("wxqiu@iflytek.com");
        insertUserInfo.setAccount("test111");
        insertUserInfo.setPhone("18855109598");
        insertUserInfo.setNickName("开心");
        ResourceUser resourceUser = resourceManagerService.insertUser(insertUserInfo);

    }

//    @Test
    public void testUpdateLoginTime() {
        resourceManagerService.updateLoginTime(1l);

    }
}
