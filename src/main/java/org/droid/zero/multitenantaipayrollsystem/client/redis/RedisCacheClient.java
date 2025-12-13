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

    public boolean isRateLimitExceeded(String email){
        //First, get the number of login attempts in redis
        String value = this.get("login_attempts:" + email);

        //If it doesn't exist, initialize the attempts to zero
        int attempts = value == null ? 0 : Integer.parseInt(value);
        attempts++; //Increase the number of attempts

        //If exceeded maximum (5 attempts) then it is rate limited
        if(attempts > 5) return true;

        //If not, record the attempt
        redis.opsForValue().set("login_attempts:" + email, Integer.toString(attempts), 15, TimeUnit.MINUTES);
        return false;
    }
}
