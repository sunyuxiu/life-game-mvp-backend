package com.lifegame.mvp.dto;

import lombok.Data;

@Data
public class TierResponse {
    private String currentTier;
    private Integer currentWeekPoints;
    private String nextTier;
    private Integer pointsNeeded;
}
