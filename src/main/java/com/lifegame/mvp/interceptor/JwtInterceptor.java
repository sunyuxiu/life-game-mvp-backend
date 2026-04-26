package com.lifegame.mvp.interceptor;

import com.lifegame.mvp.common.JwtUtil;
import com.lifegame.mvp.common.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未授权\",\"data\":null}");
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            String type = claims.get("type", String.class);
            UserContext.setTokenType(type);
            if ("USER".equals(type)) {
                UserContext.setUserId(Long.parseLong(claims.getSubject()));
            } else if ("CASHIER".equals(type)) {
                UserContext.setCashierId(Long.parseLong(claims.getSubject()));
                UserContext.setMerchantId(claims.get("merchantId", Long.class));
            }
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\",\"data\":null}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
