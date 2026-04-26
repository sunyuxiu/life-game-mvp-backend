package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.CheckinRequest;
import com.lifegame.mvp.entity.DoorCheckin;
import com.lifegame.mvp.entity.DoorQr;
import com.lifegame.mvp.mapper.DoorCheckinMapper;
import com.lifegame.mvp.mapper.DoorQrMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final DoorQrMapper doorQrMapper;
    private final DoorCheckinMapper doorCheckinMapper;
    private final PointsService pointsService;

    @Transactional
    public Map<String, Object> checkin(Long userId, CheckinRequest request) {
        String code = request.getCode();
        DoorQr doorQr = doorQrMapper.selectOne(new LambdaQueryWrapper<DoorQr>().eq(DoorQr::getCode, code));
        if (doorQr == null) {
            throw new RuntimeException("门口码不存在");
        }
        LocalDate today = LocalDate.now();
        Long count = doorCheckinMapper.selectCount(new LambdaQueryWrapper<DoorCheckin>()
                .eq(DoorCheckin::getUserId, userId)
                .eq(DoorCheckin::getMerchantId, doorQr.getMerchantId())
                .eq(DoorCheckin::getCheckinDate, today));
        if (count > 0) {
            throw new RuntimeException("今日已打卡");
        }
        DoorCheckin checkin = new DoorCheckin();
        checkin.setUserId(userId);
        checkin.setMerchantId(doorQr.getMerchantId());
        checkin.setCheckinDate(today);
        checkin.setCreatedAt(LocalDateTime.now());
        doorCheckinMapper.insert(checkin);
        pointsService.addPoints(userId, 10, 1, "DOOR_CHECKIN", checkin.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("pointsGained", 10);
        result.put("lotteryTimesGained", 1);
        result.put("merchantId", doorQr.getMerchantId());
        return result;
    }
}
