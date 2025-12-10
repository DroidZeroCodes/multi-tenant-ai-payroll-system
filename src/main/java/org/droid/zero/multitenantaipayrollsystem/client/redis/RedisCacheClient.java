package org.droid.zero.multitenantaipayrollsystem.client.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheClient {

    private final StringRedisTemplate redis;

    public void set(String key, String value, long timeOut, TimeUnit timeUnit) {
        redis.opsForValue().set(key, value, timeOut, timeUnit);
    }

    public String get(String key) {
        return this.redis.opsForValue().get(key);
    }

    public void delete(String key) {
        this.redis.delete(key);
    }

    public void blacklistToken(String jwt, long ttlMillis) {
        redis.opsForValue().set("blacklist:" + jwt, "1", ttlMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String jwt) {
        return redis.hasKey("blacklist:" + jwt);
    }
}
