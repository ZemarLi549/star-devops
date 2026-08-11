package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xdkong2
 * @date 2023/12/17
 * @desc
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueDTO {

    private String key;
    private String value;
    private Boolean isselect;

    public KeyValueDTO(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
