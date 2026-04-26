package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.LotteryRequest;
import com.lifegame.mvp.dto.LotteryResponse;
import com.lifegame.mvp.entity.CouponCode;
import com.lifegame.mvp.entity.LotteryPrize;
import com.lifegame.mvp.entity.LotteryRecord;
import com.lifegame.mvp.mapper.CouponCodeMapper;
import com.lifegame.mvp.mapper.LotteryPrizeMapper;
import com.lifegame.mvp.mapper.LotteryRecordMapper;
import com.lifegame.mvp.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LotteryService {

    private final UserMapper userMapper;
    private final LotteryPrizeMapper lotteryPrizeMapper;
    private final CouponCodeMapper couponCodeMapper;
    private final LotteryRecordMapper lotteryRecordMapper;
    private final PointsService pointsService;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public LotteryResponse draw(Long userId, LotteryRequest request) {
        int affected = userMapper.decrementLotteryTimes(userId);
        if (affected == 0) {
            throw new RuntimeException("抽奖次数不足");
        }
        List<LotteryPrize> prizes = lotteryPrizeMapper.selectList(
                new LambdaQueryWrapper<LotteryPrize>().eq(LotteryPrize::getIsActive, 1));
        if (prizes.isEmpty()) {
            throw new RuntimeException("暂无奖品");
        }
        LotteryPrize prize = weightedRandom(prizes);
        // Use local variables to avoid mutating the shared prize entity
        String effectivePrizeType = prize.getType();
        CouponCode couponCode = null;
        if ("COUPON".equals(effectivePrizeType)) {
            if (prize.getStock() > 0) {
                int stockAffected = lotteryPrizeMapper.decrementStock(prize.getId());
                if (stockAffected == 0) {
                    effectivePrizeType = "NONE";
                } else {
                    couponCode = issueCoupon(prize.getId(), userId);
                }
            }
        } else if ("POINTS".equals(effectivePrizeType)) {
            pointsService.addPoints(userId, prize.getPointsValue(), 0, "LOTTERY", null);
        }
        LotteryRecord record = new LotteryRecord();
        record.setUserId(userId);
        record.setPrizeId(prize.getId());
        record.setPrizeName(prize.getName());
        record.setCouponCodeId(couponCode != null ? couponCode.getId() : null);
        record.setCreatedAt(LocalDateTime.now());
        lotteryRecordMapper.insert(record);
        LotteryResponse response = new LotteryResponse();
        response.setPrizeName(prize.getName());
        response.setPrizeType(effectivePrizeType);
        response.setPointsValue(prize.getPointsValue());
        response.setCouponCode(couponCode != null ? couponCode.getCode() : null);
        return response;
    }

    private LotteryPrize weightedRandom(List<LotteryPrize> prizes) {
        int totalWeight = prizes.stream().mapToInt(LotteryPrize::getProbability).sum();
        int rand = random.nextInt(totalWeight);
        int cumulative = 0;
        for (LotteryPrize prize : prizes) {
            cumulative += prize.getProbability();
            if (rand < cumulative) {
                return prize;
            }
        }
        return prizes.get(prizes.size() - 1);
    }

    private CouponCode issueCoupon(Long prizeId, Long userId) {
        List<CouponCode> available = couponCodeMapper.selectList(
                new LambdaQueryWrapper<CouponCode>()
                        .eq(CouponCode::getPrizeId, prizeId)
                        .eq(CouponCode::getStatus, 0)
                        .last("LIMIT 1 FOR UPDATE"));
        if (available.isEmpty()) {
            return null;
        }
        CouponCode couponCode = available.get(0);
        int issued = couponCodeMapper.atomicIssue(couponCode.getId(), userId);
        if (issued == 0) {
            return null;
        }
        couponCode.setStatus(1);
        couponCode.setIssuedTo(userId);
        return couponCode;
    }
}
