package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consume_code")
public class ConsumeCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Long merchantId;
    private Long cashierId;
    private BigDecimal amount;
    private Integer status;
    private LocalDateTime expiredAt;
    private LocalDateTime consumedAt;
    private Long consumedBy;
    private LocalDateTime createdAt;
}
