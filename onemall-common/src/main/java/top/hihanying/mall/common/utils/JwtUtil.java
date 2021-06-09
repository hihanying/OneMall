package top.hihanying.mall.common.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * jwt生成接口
 */
@Slf4j
public class JwtUtil {

    /**
     * 私钥
     */
    private final static String Base_64_SECRET = "MDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjY=";

    /**
     * 过期时间
     */
    private final static int EXPIRES_SECOND = 1000 * 60 * 24 * 60;

    /**
     * 解析jwt toke 获取数据
     *
     * @param jsonWebToken
     * @return
     */
    public static Claims parseJWT(String jsonWebToken) {
        log.info("解析jwt: " + jsonWebToken);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(Base_64_SECRET))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 生成jwt token user
     * @param userId
     * @param userName
     * @return
     */
    public static String createJWT(Long userId, String userName) {
        // 签名算法 signatureAlgorithm
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(Base_64_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT") // 头部
                .claim("userId", userId) // 载荷1：用户id
                .claim("userName", userName) // 载荷2：用户名
                .signWith(signatureAlgorithm, signingKey); // 签名密钥
        // 当前时间 now
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //添加Token过期时间
        if (EXPIRES_SECOND >= 0) {
            long expMillis = nowMillis + EXPIRES_SECOND;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        String compact = builder.compact();
        log.info("生成jwt===========" + compact);
        return compact;
    }

    public static String createAdminJWT(Long shopId, Long sysAdminId, String loginUserName, String loginPassWord, boolean isShop) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //生成签名密钥
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(Base_64_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //添加构成JWT的参数
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .claim("shop_id", shopId)
                .claim("sys_user_id", sysAdminId)
                .claim("is_shop", isShop)
                .claim("login_username", loginUserName)
                .claim("login_password", loginPassWord)
//	                .claim("user_open_id", userOpenId)
                .signWith(signatureAlgorithm, signingKey);
        //添加Token过期时间
        if (EXPIRES_SECOND >= 0) {
            long expMillis = nowMillis + EXPIRES_SECOND;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp).setNotBefore(now);
        }

        //生成JWT
        String compact = builder.compact();
        log.info("生成jwt===========" + compact);
        return compact;
    }

}