package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.RankResponse;
import com.lifegame.mvp.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    @GetMapping("/week")
    public Result<RankResponse> getWeekRank(
            @RequestParam(required = false) String weekId,
            @RequestParam(defaultValue = "50") int topN) {
        Long userId = UserContext.getUserId();
        return Result.ok(rankService.getWeekRank(userId, weekId, topN));
    }
}
