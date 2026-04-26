package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rank_week_snapshot")
public class RankWeekSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String weekId;
    private Long userId;
    private Integer weekPoints;
    private Integer rankNum;
    private String tierName;
    private LocalDateTime snapshotAt;
}
