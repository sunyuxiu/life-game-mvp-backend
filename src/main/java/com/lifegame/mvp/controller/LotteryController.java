package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.LotteryRequest;
import com.lifegame.mvp.dto.LotteryResponse;
import com.lifegame.mvp.service.LotteryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lottery")
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryService lotteryService;

    @PostMapping("/draw")
    public Result<LotteryResponse> draw(@RequestBody(required = false) LotteryRequest request) {
        Long userId = UserContext.getUserId();
        if (request == null) request = new LotteryRequest();
        try {
            return Result.ok(lotteryService.draw(userId, request));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
