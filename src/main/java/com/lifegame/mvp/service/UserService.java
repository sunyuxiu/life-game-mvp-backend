package com.lifegame.mvp.service;

import com.lifegame.mvp.entity.User;
import com.lifegame.mvp.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public void updateCity(Long userId, String city) {
        User user = new User();
        user.setId(userId);
        user.setCity(city);
        userMapper.updateById(user);
    }
}
