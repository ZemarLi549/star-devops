package com.iflytek.itsc.auth.resource.manager.domain.form;

import lombok.Data;

import java.util.List;

@Data
public class InnerPermissionForm {

    private List<Long> userIds;

    private List<Long> workspaceIds;

    private String userNameOrAccount;
}
