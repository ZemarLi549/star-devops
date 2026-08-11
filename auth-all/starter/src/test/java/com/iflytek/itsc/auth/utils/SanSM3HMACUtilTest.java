package com.iflytek.itsc.auth.utils;

import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.san.SanSM3HMACUtil;
import com.iflytek.itsc.util.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname SanSM3HMACUtilTest
 * @Description SanSM3HMACUtil测试类
 * @Date 2024/5/8 15:10
 * @Created by wxqiu
 */

@SpringBootTest
public class SanSM3HMACUtilTest {

    @Autowired
    SanSM3HMACUtil sanSM3HMACUtil;
    @Test
    public void testIntegralityEncode(){
        String  encode = sanSM3HMACUtil.integralityEncode("101");
        System.out.println(encode);
    }

    @Test
    public void testVerifyIntegrality() {
        boolean validateIntegrality = sanSM3HMACUtil.verifyIntegrality("101", "c0uWQHAq45NDyBSIUp/R/v4MNUqhvuUFEXemdKv1Xts=");
        System.out.println(validateIntegrality);
    }
}
