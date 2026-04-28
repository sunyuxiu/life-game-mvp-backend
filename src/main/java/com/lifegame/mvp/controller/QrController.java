package com.lifegame.mvp.controller;

import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.dto.QrParseResponse;
import com.lifegame.mvp.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    @GetMapping("/parse")
    public Result<QrParseResponse> parse(@RequestParam String code) {
        try {
            return Result.ok(qrService.parse(code));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}
