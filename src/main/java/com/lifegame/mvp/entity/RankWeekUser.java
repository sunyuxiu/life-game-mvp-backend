package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rank_week_user")
public class RankWeekUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String weekId;
    private Long userId;
    private Integer weekPoints;
    private Integer rankNum;
    private LocalDateTime updatedAt;
}
