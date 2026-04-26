package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.TierResponse;
import com.lifegame.mvp.entity.RankWeekUser;
import com.lifegame.mvp.entity.Tier;
import com.lifegame.mvp.mapper.RankWeekUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TierService {

    private final RankWeekUserMapper rankWeekUserMapper;
    private final RankService rankService;

    public TierResponse getMyTier(Long userId) {
        String weekId = rankService.getCurrentWeekId();
        RankWeekUser rwu = rankWeekUserMapper.selectOne(new LambdaQueryWrapper<RankWeekUser>()
                .eq(RankWeekUser::getWeekId, weekId)
                .eq(RankWeekUser::getUserId, userId));
        int weekPoints = rwu != null && rwu.getWeekPoints() != null ? rwu.getWeekPoints() : 0;

        Tier currentTier = rankService.getTierByPoints(weekPoints);
        List<Tier> allTiers = rankService.getAllTiersSorted();

        TierResponse response = new TierResponse();
        response.setCurrentWeekPoints(weekPoints);
        response.setCurrentTier(currentTier != null ? currentTier.getName() : "无段位");

        if (currentTier != null && !allTiers.isEmpty()) {
            int currentIdx = -1;
            for (int i = 0; i < allTiers.size(); i++) {
                if (allTiers.get(i).getId().equals(currentTier.getId())) {
                    currentIdx = i;
                    break;
                }
            }
            if (currentIdx >= 0 && currentIdx < allTiers.size() - 1) {
                Tier nextTier = allTiers.get(currentIdx + 1);
                response.setNextTier(nextTier.getName());
                response.setPointsNeeded(nextTier.getMinPoints() - weekPoints);
            } else {
                response.setNextTier(null);
                response.setPointsNeeded(0);
            }
        } else if (!allTiers.isEmpty()) {
            Tier firstTier = allTiers.get(0);
            response.setNextTier(firstTier.getName());
            response.setPointsNeeded(firstTier.getMinPoints() - Math.max(0, weekPoints));
        }
        return response;
    }
}
