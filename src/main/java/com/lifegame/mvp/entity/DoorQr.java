package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("door_qr")
public class DoorQr {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Long merchantId;
    private LocalDateTime createdAt;
}
