package com.lifegame.mvp.dto;

import lombok.Data;
import java.util.List;

@Data
public class RankResponse {
    private String weekId;
    private List<RankUserVO> topList;
    private RankUserVO myRank;
}
