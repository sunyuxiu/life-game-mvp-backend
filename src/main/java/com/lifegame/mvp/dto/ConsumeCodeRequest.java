package com.lifegame.mvp.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConsumeCodeRequest {
    private BigDecimal amount;
}
