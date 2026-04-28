package com.lifegame.mvp.service;

import com.lifegame.mvp.entity.PointsLedger;
import com.lifegame.mvp.mapper.PointsLedgerMapper;
import com.lifegame.mvp.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final UserMapper userMapper;
    private final PointsLedgerMapper pointsLedgerMapper;

    public void addPoints(Long userId, int delta, int lotteryDelta, String source, Long refId) {
        userMapper.addPointsAndLottery(userId, delta, lotteryDelta);
        PointsLedger ledger = new PointsLedger();
        ledger.setUserId(userId);
        ledger.setDelta(delta);
        ledger.setSource(source);
        ledger.setRefId(refId);
        ledger.setCreatedAt(LocalDateTime.now());
        pointsLedgerMapper.insert(ledger);
    }
}
