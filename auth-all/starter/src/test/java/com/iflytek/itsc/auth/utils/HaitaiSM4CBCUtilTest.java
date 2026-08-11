package com.iflytek.itsc.auth.utils;

import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.HaitaiSM4CBCUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname HaitaiSM4CBCUtilTest
 * @Description
 * @Date 2024/12/4 10:24
 * @Created by wxqiu
 */
@SpringBootTest
public class HaitaiSM4CBCUtilTest {

    @Autowired
    private HaitaiSM4CBCUtil haitaiSM4CBCUtil;


    @Test
    public void testEncode() {
        System.out.println(haitaiSM4CBCUtil.encode("1234567890123456"));
    }

    @Test
    public void testDecode() {
        String a = haitaiSM4CBCUtil.decode("MFkCAQEWIDQ1ZGVmNWQ4ZGM4MjQ1MmRiMDM0MzExZjE3ZWM2MDJmMAoGCCqBHM9VAWgCFgNIRVgDIQCBcqMLBXFb3BYU9J8RhGaxql96sh1xvacHO7yo3d3Qwg==");
        System.out.println(a);
    }


}
