package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.ConsumeRequest;
import com.lifegame.mvp.entity.ConsumeCode;
import com.lifegame.mvp.mapper.ConsumeCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConsumeService {

    private final ConsumeCodeMapper consumeCodeMapper;
    private final PointsService pointsService;

    @Transactional
    public Map<String, Object> consume(Long userId, ConsumeRequest request) {
        ConsumeCode consumeCode = consumeCodeMapper.selectOne(new LambdaQueryWrapper<ConsumeCode>()
                .eq(ConsumeCode::getCode, request.getCode())
                .eq(ConsumeCode::getStatus, 0));
        if (consumeCode == null) {
            throw new RuntimeException("消费码不存在或已使用");
        }
        if (consumeCode.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("消费码已过期");
        }
        int affected = consumeCodeMapper.atomicConsume(consumeCode.getId(), userId);
        if (affected == 0) {
            throw new RuntimeException("核销失败，已核销或不存在");
        }
        pointsService.addPoints(userId, 50, 2, "CONSUME", consumeCode.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("pointsGained", 50);
        result.put("lotteryTimesGained", 2);
        return result;
    }
}
