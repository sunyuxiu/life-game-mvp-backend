package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.CouponCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponCodeMapper extends BaseMapper<CouponCode> {

    @Update("UPDATE coupon_code SET status=1, issued_to=#{userId}, issued_at=NOW() WHERE id=#{id} AND status=0")
    int atomicIssue(@Param("id") Long id, @Param("userId") Long userId);
}
