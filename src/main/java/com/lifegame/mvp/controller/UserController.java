package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.UpdateCityRequest;
import com.lifegame.mvp.entity.User;
import com.lifegame.mvp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<User> getMe() {
        Long userId = UserContext.getUserId();
        return Result.ok(userService.getById(userId));
    }

    @PutMapping("/city")
    public Result<Void> updateCity(@RequestBody UpdateCityRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateCity(userId, request.getCity());
        return Result.ok();
    }
}
