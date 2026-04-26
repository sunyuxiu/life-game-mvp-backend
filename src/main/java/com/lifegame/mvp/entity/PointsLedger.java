package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_ledger")
public class PointsLedger {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer delta;
    private String source;
    private Long refId;
    private LocalDateTime createdAt;
}
