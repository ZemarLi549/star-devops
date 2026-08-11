package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname updateFirstViewRequest
 * @Date 2024/4/17 11:28
 * @Created by wxqiu
 */
@Data
public class UpdateGuideStateRequest {

    @NotBlank(message = "guideState 不能为空")
    private String  guideState;

}
