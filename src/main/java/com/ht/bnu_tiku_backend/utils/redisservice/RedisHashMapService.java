package com.ht.bnu_tiku_backend.utils.redisservice;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RedisHashMapService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getOne(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> getEntries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public Long deleteOne(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().delete(key, hashKey);
    }

    public void deleteAll(String key) {
        stringRedisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
