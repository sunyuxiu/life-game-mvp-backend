package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lottery_prize")
public class LotteryPrize {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Integer pointsValue;
    private Integer probability;
    private Integer stock;
    private Integer isActive;
    private LocalDateTime createdAt;
}
