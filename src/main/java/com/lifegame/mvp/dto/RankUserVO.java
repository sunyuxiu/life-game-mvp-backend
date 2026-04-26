package com.lifegame.mvp.dto;

import lombok.Data;

@Data
public class RankUserVO {
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private Integer weekPoints;
    private Integer rankNum;
    private String tierName;
}
