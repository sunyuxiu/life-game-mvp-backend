package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.RankWeekSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RankWeekSnapshotMapper extends BaseMapper<RankWeekSnapshot> {
    List<RankWeekSnapshot> getTopN(@Param("weekId") String weekId, @Param("topN") int topN);
}
