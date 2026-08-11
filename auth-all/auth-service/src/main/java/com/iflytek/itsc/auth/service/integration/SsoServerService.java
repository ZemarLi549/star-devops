package com.iflytek.itsc.auth.service.integration;

import com.iflytek.itsc.auth.service.common.utils.DocumentBuilderFactoryUtil;
import com.iflytek.itsc.auth.service.common.utils.OkHttpClientUtil;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Classname SsoServerService
 * @Description sso 第三方服务请求实现
 * @Date 2024/1/23 14:07
 * @Created by wxqiu
 */
@Component
@Slf4j
public class SsoServerService {

    public boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo) {
        String content = null;
        try {
            String url = String.format("%s/p3/serviceValidate?ticket=%s&service=%s", loginInfoRequest.getServerUrl(), loginInfoRequest.getTicket(), loginInfoRequest.getClientUrl());
            log.info("ticket验证：" + url);
            content = OkHttpClientUtil.getInstance().getData(url).body().string();
            if (StringUtils.isNotBlank(content)) {
                Document document = DocumentBuilderFactoryUtil.createDocumentBuilderFactory().newDocumentBuilder()
                        .parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
                insertUserInfo.setAccount(document.getElementsByTagName("cas:userAccount").item(0).getTextContent());
                insertUserInfo.setNickName(document.getElementsByTagName("cas:userName").item(0).getTextContent());
                return true;
            }
            log.error("validateTicket error, content is null");
            return false;
        } catch (Exception e) {
            log.error("validateTicket method error,content:{}", content, e);
            return false;

        }
    }
}
