package com.iflytek.itsc.auth.service.common.constants;

/**
 * @Classname Constant
 * @Description 常量类
 * @Date 2023/12/8 14:58
 * @Created by wxqiu
 */
public class Constant {

    public static class Token {
        /**
         * Header key
         */
        public static final String TOKEN = "Token";


    }

    public static final String WORKSPACE_ID = "Workspace-Id";

    public static final String PATH = "Path";


    public static class AuthenticationType {

        public static final String SSO = "sso";
        public static final String INNER = "inner";
        public static final String UAP = "uap";
    }

    public static class LoginMode {
        public static final String LOCAL = "LOCAL";
        public static final String LDAP = "LDAP";
        public static final String MIXED = "MIXED";
    }




}
