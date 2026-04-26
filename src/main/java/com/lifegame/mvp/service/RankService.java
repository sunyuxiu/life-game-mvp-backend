package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.RankResponse;
import com.lifegame.mvp.dto.RankUserVO;
import com.lifegame.mvp.entity.*;
import com.lifegame.mvp.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankService {

    private final RankWeekUserMapper rankWeekUserMapper;
    private final RankWeekSnapshotMapper rankWeekSnapshotMapper;
    private final UserMapper userMapper;
    private final TierMapper tierMapper;

    public String getCurrentWeekId() {
        LocalDate now = LocalDate.now();
        WeekFields wf = WeekFields.ISO;
        int year = now.get(wf.weekBasedYear());
        int week = now.get(wf.weekOfWeekBasedYear());
        return String.format("%04d%02d", year, week);
    }

    public String getLastWeekId() {
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        WeekFields wf = WeekFields.ISO;
        int year = lastWeek.get(wf.weekBasedYear());
        int week = lastWeek.get(wf.weekOfWeekBasedYear());
        return String.format("%04d%02d", year, week);
    }

    public RankResponse getWeekRank(Long userId, String weekId, int topN) {
        if (weekId == null || weekId.isBlank()) {
            weekId = getCurrentWeekId();
        }
        boolean isCurrentWeek = weekId.equals(getCurrentWeekId());
        List<RankUserVO> topList = new ArrayList<>();

        if (isCurrentWeek) {
            List<RankWeekUser> topUsers = rankWeekUserMapper.getTopN(weekId, topN);
            for (RankWeekUser rwu : topUsers) {
                topList.add(buildRankUserVO(rwu));
            }
        } else {
            List<RankWeekSnapshot> snapshots = rankWeekSnapshotMapper.getTopN(weekId, topN);
            for (RankWeekSnapshot s : snapshots) {
                RankUserVO vo = new RankUserVO();
                vo.setUserId(s.getUserId());
                vo.setWeekPoints(s.getWeekPoints());
                vo.setRankNum(s.getRankNum());
                vo.setTierName(s.getTierName());
                User u = userMapper.selectById(s.getUserId());
                if (u != null) {
                    vo.setNickname(u.getNickname());
                    vo.setAvatarUrl(u.getAvatarUrl());
                }
                topList.add(vo);
            }
        }

        RankUserVO myRank = getMyRank(userId, weekId, isCurrentWeek);

        RankResponse response = new RankResponse();
        response.setWeekId(weekId);
        response.setTopList(topList);
        response.setMyRank(myRank);
        return response;
    }

    private RankUserVO buildRankUserVO(RankWeekUser rwu) {
        RankUserVO vo = new RankUserVO();
        vo.setUserId(rwu.getUserId());
        vo.setWeekPoints(rwu.getWeekPoints());
        vo.setRankNum(rwu.getRankNum());
        User u = userMapper.selectById(rwu.getUserId());
        if (u != null) {
            vo.setNickname(u.getNickname());
            vo.setAvatarUrl(u.getAvatarUrl());
        }
        Tier tier = getTierByPoints(rwu.getWeekPoints());
        vo.setTierName(tier != null ? tier.getName() : null);
        return vo;
    }

    private RankUserVO getMyRank(Long userId, String weekId, boolean isCurrentWeek) {
        RankUserVO myRank = new RankUserVO();
        myRank.setUserId(userId);
        User u = userMapper.selectById(userId);
        if (u != null) {
            myRank.setNickname(u.getNickname());
            myRank.setAvatarUrl(u.getAvatarUrl());
        }
        if (isCurrentWeek) {
            RankWeekUser rwu = rankWeekUserMapper.selectOne(new LambdaQueryWrapper<RankWeekUser>()
                    .eq(RankWeekUser::getWeekId, weekId)
                    .eq(RankWeekUser::getUserId, userId));
            if (rwu != null) {
                myRank.setWeekPoints(rwu.getWeekPoints());
                myRank.setRankNum(rwu.getRankNum());
                Tier tier = getTierByPoints(rwu.getWeekPoints());
                myRank.setTierName(tier != null ? tier.getName() : null);
            } else {
                myRank.setWeekPoints(0);
                myRank.setRankNum(null);
            }
        } else {
            RankWeekSnapshot snap = rankWeekSnapshotMapper.selectOne(new LambdaQueryWrapper<RankWeekSnapshot>()
                    .eq(RankWeekSnapshot::getWeekId, weekId)
                    .eq(RankWeekSnapshot::getUserId, userId));
            if (snap != null) {
                myRank.setWeekPoints(snap.getWeekPoints());
                myRank.setRankNum(snap.getRankNum());
                myRank.setTierName(snap.getTierName());
            } else {
                myRank.setWeekPoints(0);
                myRank.setRankNum(null);
            }
        }
        return myRank;
    }

    public Tier getTierByPoints(int points) {
        List<Tier> tiers = tierMapper.selectList(
                new LambdaQueryWrapper<Tier>()
                        .le(Tier::getMinPoints, points)
                        .ge(Tier::getMaxPoints, points)
                        .orderByAsc(Tier::getSortOrder));
        return tiers.isEmpty() ? null : tiers.get(0);
    }

    public List<Tier> getAllTiersSorted() {
        return tierMapper.selectList(new LambdaQueryWrapper<Tier>().orderByAsc(Tier::getSortOrder));
    }

    public void refreshCurrentWeekRank() {
        String weekId = getCurrentWeekId();
        rankWeekUserMapper.refreshWeekRank(weekId);
        rankWeekUserMapper.updateRankNums(weekId);
        log.info("Refreshed week rank for weekId={}", weekId);
    }

    public void snapshotLastWeekRank() {
        String lastWeekId = getLastWeekId();
        List<RankWeekUser> lastWeekUsers = rankWeekUserMapper.selectList(
                new LambdaQueryWrapper<RankWeekUser>().eq(RankWeekUser::getWeekId, lastWeekId));
        for (RankWeekUser rwu : lastWeekUsers) {
            Tier tier = getTierByPoints(rwu.getWeekPoints() != null ? rwu.getWeekPoints() : 0);
            RankWeekSnapshot snapshot = new RankWeekSnapshot();
            snapshot.setWeekId(lastWeekId);
            snapshot.setUserId(rwu.getUserId());
            snapshot.setWeekPoints(rwu.getWeekPoints() != null ? rwu.getWeekPoints() : 0);
            snapshot.setRankNum(rwu.getRankNum() != null ? rwu.getRankNum() : 0);
            snapshot.setTierName(tier != null ? tier.getName() : null);
            try {
                rankWeekSnapshotMapper.insert(snapshot);
            } catch (Exception e) {
                log.warn("Snapshot already exists for weekId={}, userId={}", lastWeekId, rwu.getUserId());
            }
        }
        log.info("Snapshot last week rank for weekId={}", lastWeekId);
    }
}
