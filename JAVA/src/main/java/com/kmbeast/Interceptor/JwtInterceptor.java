package com.kmbeast.Interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 放行OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从Header中获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "认证头格式错误：应为 'Bearer <token>'");
            return false;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            return sendError(response, "未提供认证令牌");
        }

        // 验证token
        try {
            Claims claims = JwtUtil.fromToken(token);
            if (claims == null) {
                return sendError(response, "无效的认证令牌");
            }

            Integer userId = claims.get("id", Integer.class);
            Integer roleId = claims.get("role", Integer.class);

            // 存储到线程局部变量
            LocalThreadHolder.setUserId(userId, roleId);

            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return sendError(response, "登录已过期，请重新登录");
        } catch (Exception e) {
            return sendError(response, "令牌解析异常: " + e.getMessage());
        }
    }

    private boolean sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<String> error = ApiResult.error(message);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSONObject.toJSONString(error));
            writer.flush();
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        LocalThreadHolder.clear();
    }
}