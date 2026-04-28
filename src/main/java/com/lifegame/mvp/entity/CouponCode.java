package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("coupon_code")
public class CouponCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long prizeId;
    private String code;
    private Integer status;
    private Long issuedTo;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
}
