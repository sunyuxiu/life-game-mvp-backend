package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE `user` SET points = points + #{delta}, lottery_times = lottery_times + #{lotteryDelta} WHERE id = #{userId}")
    int addPointsAndLottery(@Param("userId") Long userId, @Param("delta") int delta, @Param("lotteryDelta") int lotteryDelta);

    @Update("UPDATE `user` SET lottery_times = lottery_times - 1 WHERE id = #{userId} AND lottery_times > 0")
    int decrementLotteryTimes(@Param("userId") Long userId);
}
