package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.TierResponse;
import com.lifegame.mvp.service.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tier")
@RequiredArgsConstructor
public class TierController {

    private final TierService tierService;

    @GetMapping("/me")
    public Result<TierResponse> getMyTier() {
        Long userId = UserContext.getUserId();
        return Result.ok(tierService.getMyTier(userId));
    }
}
