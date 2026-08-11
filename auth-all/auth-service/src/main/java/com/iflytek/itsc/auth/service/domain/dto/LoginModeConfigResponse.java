package com.iflytek.itsc.auth.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginModeConfigResponse {
    private String mode;
    private List<String> options;
    private Boolean captchaEnabled;
}
