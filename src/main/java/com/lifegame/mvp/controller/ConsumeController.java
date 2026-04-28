package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.ConsumeRequest;
import com.lifegame.mvp.service.ConsumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consume")
@RequiredArgsConstructor
public class ConsumeController {

    private final ConsumeService consumeService;

    @PostMapping
    public Result<Map<String, Object>> consume(@RequestBody ConsumeRequest request) {
        Long userId = UserContext.getUserId();
        try {
            return Result.ok(consumeService.consume(userId, request));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
