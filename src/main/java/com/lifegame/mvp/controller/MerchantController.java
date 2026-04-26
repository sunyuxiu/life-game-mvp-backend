package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.entity.Merchant;
import com.lifegame.mvp.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @GetMapping
    public Result<List<Merchant>> getRecommended() {
        Long userId = UserContext.getUserId();
        return Result.ok(merchantService.getRecommended(userId));
    }

    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            return Result.fail("商家不存在");
        }
        return Result.ok(merchant);
    }
}
