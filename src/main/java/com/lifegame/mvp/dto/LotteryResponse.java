package com.lifegame.mvp.dto;

import lombok.Data;

@Data
public class LotteryResponse {
    private String prizeName;
    private String prizeType;
    private Integer pointsValue;
    private String couponCode;
}
