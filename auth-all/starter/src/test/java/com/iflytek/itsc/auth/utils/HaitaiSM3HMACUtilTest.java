package com.iflytek.itsc.auth.utils;

import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.HaitaiSM3HMACUtil;
import com.iflytek.itsc.util.json.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname HaitaiSM3HMACUtilTest
 * @Description
 * @Date 2024/12/4 17:02
 * @Created by wxqiu
 */
@SpringBootTest
public class HaitaiSM3HMACUtilTest {

    @Autowired
    private HaitaiSM3HMACUtil haitaiSM3HMACUtil;


    @Test
    public void testIntegralityEncode() {
        String encode = haitaiSM3HMACUtil.integralityEncode("101");
        System.out.println(encode);
    }

    @Test
    public void testVerifyIntegrality() {
       String s = "MIIDWwYJKoZIhvcNAQcCoIIDTDCCA0gCAQExDjAMBggqgRzPVQGDEQUAMAsGCSqGSIb3DQEHAaCCAjIwggIuMIIB1KADAgECAgkAzNYpiMXFhycwCgYIKoEcz1UBg3UwdjERMA8GA1UEAwwISFNST09UQ0ExFTATBgNVBAsMDOS/oeaBr+S4reW/gzEVMBMGA1UECgwM5L+h5oGv5Lit5b+DMRIwEAYDVQQHDAnpu4Tnn7PluIIxEjAQBgNVBAgMCea5luWMl+ecgTELMAkGA1UEBgwCQ04wHhcNMjQxMjE4MTA1OTQ5WhcNMzQxMjE4MTA1OTQ5WjByMRMwEQYDVQQDDAp3enRlc3QxMjE4MRUwEwYDVQQLDAzlronlhajnoJTlj5ExFTATBgNVBAoMDOa1t+azsOaWueWchjEPMA0GA1UEBwwG5YyX5LqsMQ8wDQYDVQQIDAbljJfkuqwxCzAJBgNVBAYMAkNOMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEWkfeKqRxgq0GgkMkI/Yf3tqLiz2nj0aG5FREmF3l7LJu5NZpVTiHg9jc6vWeG1cxszS4hOiw3CEZMaMPSoW3laNPME0wHwYDVR0jBBgwFoAU+7GZnLfmYRWwDT2gsuLJltG6HOUwHQYDVR0OBBYEFArkB9XcCSvdNDO54btVRNAR+lYJMAsGA1UdDwQEAwIBXjAKBggqgRzPVQGDdQNIADBFAiAofWTWyFGgvIKm7MkkxjLGqUaA2ZxMpPj07UtLASKBiAIhAM2Hj0UQjBDC4xgVcgz0XkWWkSDjQcFUCMBBW5eYKn4mMYHvMIHsAgEBMIGDMHYxETAPBgNVBAMMCEhTUk9PVENBMRUwEwYDVQQLDAzkv6Hmga/kuK3lv4MxFTATBgNVBAoMDOS/oeaBr+S4reW/gzESMBAGA1UEBwwJ6buE55+z5biCMRIwEAYDVQQIDAnmuZbljJfnnIExCzAJBgNVBAYMAkNOAgkAzNYpiMXFhycwDAYIKoEcz1UBgxEFADAKBggqgRzPVQGDdQRHMEUCIHT/JXU9JKjk5wirN2yNw72Pg/RWwMSk+hNRFigag68dAiEA9urWPU0kDWTK8FQ1jVI+rq1ISVao9xDQMyMdd38k6QE=";
        boolean validateIntegrality = haitaiSM3HMACUtil.verifyIntegrality("101", s);
    }
}
