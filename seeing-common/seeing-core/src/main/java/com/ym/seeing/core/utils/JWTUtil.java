package com.ym.seeing.core.utils;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ym.seeing.core.domain.User;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Yangmiao
 * @Date: 2022/11/21 14:43
 * @Desc: JWT
 */
public class JWTUtil {

    private static String EXPIRE_TIME = "";
    private static String SECRET = "www.hellohao.cn";

    public static String createToken(User user) {
        Calendar calendar = Calendar.getInstance();
        //单位秒，604800 为7天
        calendar.add(Calendar.SECOND, 604800);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
                .withClaim("email", user.getEmail())
                .withClaim("username", user.getUserName())
                .withClaim("uid", user.getUid())
                .withClaim("password", user.getPassword())
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
        return token;
    }

    public static JSONObject checkToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET))
                .build();//验证对象
        JSONObject jsonObject = new JSONObject();
        if (null == token) {
            jsonObject.put("check", false);
            return jsonObject;
        }
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            Date expiresAt = verify.getExpiresAt();
            jsonObject.put("check", true);
            jsonObject.put("email", verify.getClaim("email").asString());
            jsonObject.put("password", verify.getClaim("password").asString());
            jsonObject.put("uid", verify.getClaim("uid").asString());
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            System.out.println("token认证已过期，请重新登录获取");
            jsonObject.put("check", false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token无效");
            jsonObject.put("check", false);
        }
        return jsonObject;
    }
}
