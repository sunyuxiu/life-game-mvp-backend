package com.lifegame.mvp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifegame.mvp.dto.QrParseResponse;
import com.lifegame.mvp.entity.DoorQr;
import com.lifegame.mvp.entity.Merchant;
import com.lifegame.mvp.mapper.DoorQrMapper;
import com.lifegame.mvp.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QrService {

    private final DoorQrMapper doorQrMapper;
    private final MerchantMapper merchantMapper;

    public QrParseResponse parse(String code) {
        QrParseResponse response = new QrParseResponse();
        response.setCode(code);
        if (code.startsWith("DOOR_")) {
            DoorQr doorQr = doorQrMapper.selectOne(new LambdaQueryWrapper<DoorQr>().eq(DoorQr::getCode, code));
            if (doorQr == null) {
                throw new RuntimeException("二维码不存在");
            }
            Merchant merchant = merchantMapper.selectById(doorQr.getMerchantId());
            response.setType("DOOR");
            response.setMerchantId(doorQr.getMerchantId());
            response.setMerchantName(merchant != null ? merchant.getName() : "");
        } else if (code.startsWith("CONSUME_")) {
            response.setType("CONSUME");
        } else {
            throw new RuntimeException("未知二维码类型");
        }
        return response;
    }
}
