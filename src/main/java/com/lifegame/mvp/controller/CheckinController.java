package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.CheckinRequest;
import com.lifegame.mvp.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;

    @PostMapping("/door")
    public Result<Map<String, Object>> checkin(@RequestBody CheckinRequest request) {
        Long userId = UserContext.getUserId();
        try {
            return Result.ok(checkinService.checkin(userId, request));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
