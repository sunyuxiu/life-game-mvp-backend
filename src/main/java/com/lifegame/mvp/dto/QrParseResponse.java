package com.lifegame.mvp.dto;

import lombok.Data;

@Data
public class QrParseResponse {
    private String type;
    private Long merchantId;
    private String merchantName;
    private String code;
}
