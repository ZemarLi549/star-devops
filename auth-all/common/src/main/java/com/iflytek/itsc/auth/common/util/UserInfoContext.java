package com.iflytek.itsc.auth.common.util;

import lombok.Data;

import java.util.Set;

/**
 * @Classname UserInfoContext
 * @Description 用户信息上下文
 * @Date 2023/12/18 10:43
 * @Created by wxqiu
 */
public class UserInfoContext {

    private static final ThreadLocal<LoginUser> threadLocalUser = new ThreadLocal<>();

    public static void setUser(LoginUser user) {
        threadLocalUser.set(user);
    }

    public static void remove() {
        threadLocalUser.remove();
    }

    public static Long getUserId() {
        return threadLocalUser.get().getUserId();
    }

    public static Long getWorkspaceId() {
        return threadLocalUser.get().getWorkspaceId();
    }
    
    // 添加别名方法以兼容错误的调用
    public static Long getWorkSpaceId() {
        return getWorkspaceId();
    }


    public static String getAccount() {
        return threadLocalUser.get().getAccount();
    }

    public static boolean isSuperAdmin() {
        return threadLocalUser.get().isSuperAdmin();
    }

    public static boolean isWorkSpaceRelated(Long workSpaceId) {
        return isSuperAdmin() || threadLocalUser.get().getRelateWorkSpaceIdSet().contains(workSpaceId);
    }

    @Data
    public static class LoginUser {
        private Long userId;
        private Long workspaceId;
        private String account;
        private boolean isSuperAdmin;
        private Set<Long> relateWorkSpaceIdSet;
    }


}
