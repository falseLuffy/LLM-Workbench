package com.example.llm.service.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.llm.entity.ApiKey;
import com.example.llm.mapper.ApiKeyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyMapper apiKeyMapper;
    private final StringRedisTemplate redisTemplate;
    
    private static final String API_KEY_CACHE_PREFIX = "llm:apikey:";

    /**
     * 验证API Key并返回所属用户ID
     * @param keyValue 比如 sk-xxxx
     * @return 对应的 userId，如果不合法返回 null
     */
    public Long validateKey(String keyValue) {
        String cacheKey = API_KEY_CACHE_PREFIX + keyValue;
        
        // 1. 查缓存
        String cachedUserId = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUserId != null) {
            return Long.parseLong(cachedUserId);
        }
        
        // 2. 缓存未命中，查数据库
        ApiKey apiKey = apiKeyMapper.selectOne(new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getKeyValue, keyValue)
                .eq(ApiKey::getStatus, 1)); // 必须是启用状态
                
        if (apiKey != null) {
            // 将正确的映射放入缓存，如设置过期时间为1小时，避免缓存穿透或修改后长时间不生效
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(apiKey.getUserId()), 1, TimeUnit.HOURS);
            return apiKey.getUserId();
        }
        
        return null;
    }

    public java.util.List<ApiKey> listApiKeys(Long userId) {
        return apiKeyMapper.selectList(new LambdaQueryWrapper<ApiKey>()
                .eq(ApiKey::getUserId, userId));
    }

    public void deleteApiKey(Long id, Long userId) {
        ApiKey apiKey = apiKeyMapper.selectById(id);
        if (apiKey != null && apiKey.getUserId().equals(userId)) {
            apiKey.setStatus(0);
            apiKeyMapper.updateById(apiKey);
            redisTemplate.delete(API_KEY_CACHE_PREFIX + apiKey.getKeyValue());
        }
    }
}
