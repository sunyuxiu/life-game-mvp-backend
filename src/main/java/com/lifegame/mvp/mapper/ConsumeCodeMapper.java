package com.lifegame.mvp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifegame.mvp.entity.ConsumeCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ConsumeCodeMapper extends BaseMapper<ConsumeCode> {

    @Update("UPDATE consume_code SET status=1, consumed_by=#{userId}, consumed_at=NOW() WHERE id=#{id} AND status=0")
    int atomicConsume(@Param("id") Long id, @Param("userId") Long userId);
}
