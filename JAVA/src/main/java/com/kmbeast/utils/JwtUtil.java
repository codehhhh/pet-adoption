package com.kmbeast.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token 工具类（适配 JJWT 0.11.5 版本，兼容原有调用逻辑）
 */
public class JwtUtil {

    // 原有密钥（无需修改）
    private static final String PRIVATE_KEY_STR = "d8c986df-8512-42b5-906f-eeea9b3acf86";
    // 自动转换为 JJWT 0.11.x 要求的安全密钥（一次性初始化）
    private static final SecretKey PRIVATE_KEY = Keys.hmacShaKeyFor(PRIVATE_KEY_STR.getBytes());
    // 有效期2小时（毫秒，原有逻辑不变）
    private static final Integer TIME = 1000 * 60 * 60 * 2;

    /**
     * 生成 token（方法名、参数、返回值完全不变，直接替换即可）
     */
    public static String toToken(Integer id, Integer role) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // 原有头部信息逻辑不变
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                // 原有载荷逻辑不变
                .claim("id", id)
                .claim("role", role)
                .setSubject("用户认证")
                .setExpiration(new Date(System.currentTimeMillis() + TIME))
                .setId(UUID.randomUUID().toString())
                // ✅ 适配 0.11.x：使用安全密钥签名
                .signWith(PRIVATE_KEY)
                .compact();
    }

    /**
     * 解析 token（方法名、参数、返回值完全不变，直接替换即可）
     */
    public static Claims fromToken(String token) {
        try {
            // ✅ 适配 0.11.x：使用安全密钥解析
            return Jwts.parserBuilder()
                    .setSigningKey(PRIVATE_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 原有异常处理逻辑不变
            return null;
        }
    }
}