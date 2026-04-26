package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import com.lifegame.mvp.dto.CashierLoginRequest;
import com.lifegame.mvp.dto.ConsumeCodeRequest;
import com.lifegame.mvp.service.CashierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
public class CashierController {

    private final CashierService cashierService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody CashierLoginRequest request) {
        try {
            return Result.ok(cashierService.login(request));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/consume-code")
    public Result<Map<String, Object>> generateConsumeCode(@RequestBody ConsumeCodeRequest request) {
        Long cashierId = UserContext.getCashierId();
        Long merchantId = UserContext.getMerchantId();
        return Result.ok(cashierService.generateConsumeCode(cashierId, merchantId, request));
    }
}
