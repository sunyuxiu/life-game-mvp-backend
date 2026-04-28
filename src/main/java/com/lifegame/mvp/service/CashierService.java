package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.common.JwtUtil;
import com.lifegame.mvp.dto.CashierLoginRequest;
import com.lifegame.mvp.dto.ConsumeCodeRequest;
import com.lifegame.mvp.entity.Cashier;
import com.lifegame.mvp.entity.ConsumeCode;
import com.lifegame.mvp.mapper.CashierMapper;
import com.lifegame.mvp.mapper.ConsumeCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashierService {

    private final CashierMapper cashierMapper;
    private final ConsumeCodeMapper consumeCodeMapper;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(CashierLoginRequest request) {
        Cashier cashier = cashierMapper.selectOne(new LambdaQueryWrapper<Cashier>()
                .eq(Cashier::getUsername, request.getUsername()));
        if (cashier == null || !cashier.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateCashierToken(cashier.getId(), cashier.getMerchantId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("cashierId", cashier.getId());
        result.put("merchantId", cashier.getMerchantId());
        return result;
    }

    public Map<String, Object> generateConsumeCode(Long cashierId, Long merchantId, ConsumeCodeRequest request) {
        String code = "CONSUME_" + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(10);
        ConsumeCode consumeCode = new ConsumeCode();
        consumeCode.setCode(code);
        consumeCode.setMerchantId(merchantId);
        consumeCode.setCashierId(cashierId);
        consumeCode.setAmount(request.getAmount());
        consumeCode.setStatus(0);
        consumeCode.setExpiredAt(expiredAt);
        consumeCode.setCreatedAt(LocalDateTime.now());
        consumeCodeMapper.insert(consumeCode);
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("expiredAt", expiredAt);
        return result;
    }
}
