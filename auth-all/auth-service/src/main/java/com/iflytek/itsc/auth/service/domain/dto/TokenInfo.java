package com.iflytek.itsc.auth.service.domain.dto;

import com.iflytek.itsc.auth.service.common.config.TokenExpireConfig;
import com.iflytek.itsc.auth.service.util.JWTUtil;
import lombok.Data;

/**
 * @Classname TokenInfo
 * @Description token 相关信息
 * @Date 2023/12/15 17:05
 * @Created by wxqiu
 */
@Data
public class TokenInfo {

    private String token;

    private String refreshToken;
    /**
     * token 过期时间
     */
    private Long tokenExpireTime;


    public TokenInfo(String userId, String account, TokenExpireConfig tokenExpire) {
        //生成访问token
        String token = JWTUtil.sign(userId, account, tokenExpire.getAccessTokenExpire());
        this.token = token;
        //生成刷新token
        String refreshToken = JWTUtil.sign(userId, account, tokenExpire.getRefreshTokenExpire());
        this.refreshToken = refreshToken;
        Long tokenExpireTime = (long) (tokenExpire.getAccessTokenExpire() * 60 * 1000);
        this.tokenExpireTime = tokenExpireTime;
    }
}
