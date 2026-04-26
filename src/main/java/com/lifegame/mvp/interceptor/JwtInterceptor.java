package com.lifegame.mvp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifegame.mvp.common.JwtUtil;
import com.lifegame.mvp.common.Result;
import com.lifegame.mvp.common.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeErrorResponse(response, 401, "未授权");
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
            writeErrorResponse(response, 401, "Token无效或已过期");
            return false;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(status, message)));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
