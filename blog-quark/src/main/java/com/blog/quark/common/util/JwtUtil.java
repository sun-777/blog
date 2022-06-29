package com.blog.quark.common.util;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.DAYS;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import com.blog.quark.configure.GlobalConfig;
import com.blog.quark.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public final class JwtUtil {
    // 默认最小存活时间30 minutes（单位:ms）
    private static final long JWT_MIN_TTL = MINUTES.toMillis(30);
    // 默认最大存活时间7 days（单位:ms）
    private static final long JWT_MAX_TTL = DAYS.toMillis(7);
    
    // JWT的生产者和消费者约定使用的Claim
    public static final String CLAIM_KEY_USERID = Claims.SUBJECT;  //Subject Claim
    public static final String CLAIM_KEY_NICKNAME = Claims.ISSUER;  //Issuer Claim
    public static final String CLAIM_KEY_EXPIRATION = Claims.EXPIRATION;   //Expiration Time Claim
    public static final String CLAIM_KEY_JWTID = Claims.ID;  //JWT ID Claim
    public static final String CLAIM_KEY_CREATED = Claims.ISSUED_AT;  // IssueAt Claim
    
    
    public enum TokenStatus {
        // 与token无关的状态，如参数异常等
        UNKOWN,
        // 不受支持的Claims JWS
        UNSUPPORTED,
        // 不是有效的JWS
        MALFORMED,
        // JWS解密失败
        INVALID,
        // token已过期
        EXPIRED,
        VALID;
    }
    
    
    public static String generateToken(User user) {
        return generateToken(user, null, JWT_MIN_TTL);
    }
    
    
    public static String generateToken(User user, Map<String, Object> claims) {
        return generateToken(user, claims, JWT_MIN_TTL);
    }
    
    
    public static String generateToken(User user, Map<String, Object> claims, long ttlMillis) {
        // 修正token存活时间
        ttlMillis = ttlMillis > JWT_MAX_TTL ? JWT_MAX_TTL : ttlMillis;
        ttlMillis = ttlMillis < JWT_MIN_TTL ? JWT_MIN_TTL : ttlMillis;
        
        if (null == claims) {
            claims = new HashMap<>();
        }
        claims.put(CLAIM_KEY_USERID, user.getUserId());
        claims.put(CLAIM_KEY_NICKNAME, user.getNickname());
        // JWT ID: JWT的唯一标识，根据业务需要，可以设置为一个不重复的值，主要用来作为一次性token，从而回避重放攻击
        claims.put(CLAIM_KEY_JWTID, UUID.randomUUID().toString().replaceAll("-", ""));
        long now = System.currentTimeMillis();
        // 设置签发时间
        claims.put(CLAIM_KEY_CREATED, new Date(now));
        claims.put(CLAIM_KEY_EXPIRATION, new Date(now + ttlMillis));
        
        return generateToken(claims);
    }
    
    
    public static TokenStatus verifyToken(final String token) {
        if (null == token) {
            return TokenStatus.UNKOWN;
        }
        
        try {
            parseJws(token);
        } catch (io.jsonwebtoken.ExpiredJwtException eje) {
            return TokenStatus.EXPIRED;
        } catch (io.jsonwebtoken.UnsupportedJwtException uje) {
            return TokenStatus.UNSUPPORTED;
        } catch (io.jsonwebtoken.MalformedJwtException mje) {
            return TokenStatus.MALFORMED;
        } catch (io.jsonwebtoken.security.SignatureException se) {
            return TokenStatus.INVALID;
        } catch (IllegalArgumentException iae) {
            return TokenStatus.UNKOWN;
        }
        return TokenStatus.VALID;
    }
    
    
    
    // token是否过期
    public static boolean isExpired(final String token) {
        return TokenStatus.EXPIRED == verifyToken(token);
    }
    
    
    // 获取userId
    public static Object getUserId(final String token) {
        if (null == token || token.isBlank()) {
            return null;
        }
        return getFromClaims(token, CLAIM_KEY_USERID);
    }
    
    
    /**
     * 从解析的token中，通过Map中key，获取对应的值
     * @param token
     * @param map  存放需要获取的key对应的值，key有 CLAIM_KEY_USERID、CLAIM_KEY_NICKNAME ... 等
     * @return
     */
    public static Map<String, Object> get(final String token, final Map<String, Object> map) {
        if (null == token || null == map || map.isEmpty()) {
            return null;
        }
        try {
            Jws<Claims> jws = parseJws(token);
            if (null != jws) {
                Claims claims = jws.getBody();
                return getFromClaims(claims, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    // 刷新token（更改token的生成时间、过期时间实现）
    public static String refreshToken(final String token) {
        if (null != token) {
            try {
                Jws<Claims> jws = parseJws(token);
                if (null != jws) {
                    final Claims claims = jws.getBody();
                    // CLAIM_KEY_CREATED
                    long created = claims.getIssuedAt().getTime();
                    // CLAIM_KEY_EXPIRATION
                    long expiration = claims.getExpiration().getTime();
                    final long ttl = expiration - created;
                    final long now = System.currentTimeMillis();
                    claims.put(CLAIM_KEY_CREATED, new Date(now));
                    claims.put(CLAIM_KEY_EXPIRATION, new Date(now + ttl));
                    return generateToken(claims);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    
    
    private static String generateToken(final Map<String, Object> claims) {
        // RSA私钥Key
        Key privateKey = RsaUtil.getPrivateKey(Objects.requireNonNull(GlobalConfig.getCrypto().getRsa().getPrivateKey()));
        if (null == privateKey) {
            throw new IllegalArgumentException("Error to get encrypt key");
        }
        return Jwts.builder()
                .setClaims(claims)
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

    }
    
    
    private static Jws<Claims> parseJws(final String token) {
        // RSA公钥Key
        Key publicKey = RsaUtil.getPublicKey(Objects.requireNonNull(GlobalConfig.getCrypto().getRsa().getPublicKey()));
        if (null == publicKey) {
            throw new IllegalArgumentException("Error to get decrypt key");
        }
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
    }
    
    
    
    private static Object getFromClaims(final String token, final String key) {
        if (!(null == key || key.isBlank())) {
            try {
                Jws<Claims> jws = parseJws(token);
                return getFromClaims(jws.getBody(), key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    
    
    private static Object getFromClaims(final Claims claims, final String key) {
        return claims.get(key);
    }
    
    

    private static Map<String, Object> getFromClaims(final Claims claims, final Map<String, Object> map) {
        Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String,Object> entry = iter.next();
            entry.setValue(getFromClaims(claims, entry.getKey()));
        }
        return map;
    }
    
    
    private JwtUtil() {}
}
