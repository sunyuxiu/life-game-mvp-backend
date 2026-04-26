package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lottery_record")
public class LotteryRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long prizeId;
    private String prizeName;
    private Long couponCodeId;
    private LocalDateTime createdAt;
}
