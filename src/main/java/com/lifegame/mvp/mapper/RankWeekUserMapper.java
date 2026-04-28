package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.RankWeekUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankWeekUserMapper extends BaseMapper<RankWeekUser> {

    void refreshWeekRank(@Param("weekId") String weekId);

    void updateRankNums(@Param("weekId") String weekId);

    List<RankWeekUser> getTopN(@Param("weekId") String weekId, @Param("topN") int topN);
}
