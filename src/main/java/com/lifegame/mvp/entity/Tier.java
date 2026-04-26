package com.lifegame.mvp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tier")
public class Tier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer minPoints;
    private Integer maxPoints;
    private Integer sortOrder;
}
