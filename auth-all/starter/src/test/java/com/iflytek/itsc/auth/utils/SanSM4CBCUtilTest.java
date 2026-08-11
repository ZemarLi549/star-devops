package com.iflytek.itsc.auth.utils;

import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.san.SanSM4CBCUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname SanSM4CBCUtilTest
 * @Description SanSM4CBCUtil 测试类
 * @Date 2024/5/8 15:07
 * @Created by wxqiu
 */

@SpringBootTest
public class SanSM4CBCUtilTest {

    @Autowired
    SanSM4CBCUtil sanSM4CBCUtil;


    @Test
    public void testEncode() {
        String encode = sanSM4CBCUtil.encode("test");
        System.out.println(encode);

    }

    @Test
    public void testDecode() {
        System.out.println(sanSM4CBCUtil.decode("+E04vKuJFkNvShYkpOqZaQ==")
        );
    }


}
