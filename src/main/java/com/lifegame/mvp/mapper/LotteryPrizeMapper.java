package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.LotteryPrize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LotteryPrizeMapper extends BaseMapper<LotteryPrize> {

    @Update("UPDATE lottery_prize SET stock=stock-1 WHERE id=#{id} AND stock>0")
    int decrementStock(@Param("id") Long id);
}
