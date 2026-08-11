package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Classname ApplicationRequest
 * @Date 2024/4/10 16:06
 * @Created by wxqiu
 */
@Data
public class ApplicationRequest {

    @NotNull(message = "workspaceId 不能为空")
    private Long workspaceId;

    private List<String> groups;

    private String dataGroupToken;
}
