package com.iflytek.itsc.auth.service.integration;

import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.common.utils.DocumentBuilderFactoryUtil;
import com.iflytek.itsc.auth.service.common.utils.OkHttpClientUtil;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.integration.dto.UapServer.GetTokenRequest;
import com.iflytek.itsc.auth.service.integration.dto.UapServer.RestResponse;
import com.iflytek.itsc.auth.service.integration.dto.UapServer.TokenInfoResponse;
import com.iflytek.itsc.auth.service.integration.dto.UapServer.UapUserInfo;
import com.iflytek.itsc.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @Classname UapServerService
 * @Description uap 第三方服务请求实现
 * @Date 2024/1/23 8:57
 * @Created by wxqiu
 */
@Component
@Slf4j
public class UapServerService {

    @Value("${uap.rest-server-url:http://172.30.34.73:8380/uap-server/rest}")
    private String restServerUrl;
    @Value("${uap.app-code:yy_1704868151449}")
    private String appCode;
    @Value("${uap.app-auth-code:2URVLWF9E4QQ9D4MM311IXNRGSAQV6AO}")
    private String appAuthCode;

    private static TokenInfoResponse tokenInfo = new TokenInfoResponse();

    public Boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo) {
        String content = null;
        try {
            String url = String.format("%s/serviceValidate?service=%s&ticket=%s", loginInfoRequest.getServerUrl(), loginInfoRequest.getClientUrl(), loginInfoRequest.getTicket());
            log.info("ticket验证：" + url);
            content = OkHttpClientUtil.getInstance().getData(url).body().string();
            if (StringUtils.isNotBlank(content)) {
                Document document = DocumentBuilderFactoryUtil.createDocumentBuilderFactory().newDocumentBuilder()
                        .parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
                insertUserInfo.setAccount(document.getElementsByTagName("cas:LoginName").item(0).getTextContent());
                insertUserInfo.setUserId(document.getElementsByTagName("cas:UserId").item(0).getTextContent());
                try {
                    insertUserInfo.setTenantId(document.getElementsByTagName("cas:TenantId").item(0).getTextContent());
                } catch (NullPointerException e) {
                    log.error("tenantId is null");
                }
                return true;
            }
            log.error("validateTicket error, content is null");
            return false;
        } catch (Exception e) {
            log.error("validateTicket method error,content:{}", content, e);
            return false;

        }
    }


    public UapUserInfo getUserInfo(String loginName, String tenantId) {
        try {
            String url = String.format("%s/user/get/info/%s/UAPSystem", restServerUrl, loginName);
            Response response = OkHttpClientUtil.getInstance().getDataWithHeaders(url, Headers.of("token", getToken(tenantId), "tenantId", tenantId));
            RestResponse restResponse = JsonUtil.json2Object(response.body().string(), RestResponse.class);
            if (restResponse.isFlag()) {
                String json = JsonUtil.object2Json(restResponse.getData());
                UapUserInfo uapUserInfo = JsonUtil.json2Object(json, UapUserInfo.class);
                return uapUserInfo;
            }
            log.error("getToken error:{}", ErrorCodeEnum.INTEGRATION_ERROR.getErrorCode() + ErrorCodeEnum.INTEGRATION_ERROR.getErrorMsg() + ":" + restResponse.getCode() + restResponse.getMessage());
        } catch (Exception exception) {
            log.error("getUserInfo method error", exception);
        }
        return null;
    }


    public String getToken(String tenantId) {
        try {
            if (LocalDateTime.now().isBefore(tokenInfo.getExpiresDateTime())) {
                return tokenInfo.getToken();
            }
            String url = String.format("%s/v2/token/getToken", restServerUrl);
            String response = OkHttpClientUtil.getInstance().postJson(url, JsonUtil.object2Json(GetTokenRequest.builder().appCode(appCode).appAuthCode(appAuthCode).tenantId(tenantId).build()));
            RestResponse<TokenInfoResponse> restResponse = JsonUtil.json2Object(response, RestResponse.class);
            if (restResponse.isFlag()) {
                String json = JsonUtil.object2Json(restResponse.getData());
                tokenInfo = JsonUtil.json2Object(json, TokenInfoResponse.class);
                //根据过期的毫秒数设置token过期时间
                tokenInfo.setExpiresDateTime(LocalDateTime.now().plusSeconds(tokenInfo.getExpiresInMillis() / 1000));
                return tokenInfo.getToken();
            }
            log.error("getToken error:{}", ErrorCodeEnum.INTEGRATION_ERROR.getErrorCode() + ErrorCodeEnum.INTEGRATION_ERROR.getErrorMsg() + ":" + restResponse.getCode() + restResponse.getMessage());
        } catch (Exception exception) {
            log.error("getToken method error", exception);
        }
        return tokenInfo.getToken();
    }


}
