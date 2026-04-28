package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.entity.Merchant;
import com.lifegame.mvp.entity.User;
import com.lifegame.mvp.mapper.MerchantMapper;
import com.lifegame.mvp.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantMapper merchantMapper;
    private final UserMapper userMapper;

    public List<Merchant> getRecommended(Long userId) {
        User user = userMapper.selectById(userId);
        String city = user != null ? user.getCity() : null;
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getIsActive, 1);
        if (StringUtils.hasText(city)) {
            wrapper.eq(Merchant::getCity, city);
            List<Merchant> cityMerchants = merchantMapper.selectList(wrapper);
            if (!cityMerchants.isEmpty()) {
                return cityMerchants;
            }
        }
        return merchantMapper.selectList(new LambdaQueryWrapper<Merchant>().eq(Merchant::getIsActive, 1));
    }

    public Merchant getById(Long id) {
        return merchantMapper.selectById(id);
    }
}
