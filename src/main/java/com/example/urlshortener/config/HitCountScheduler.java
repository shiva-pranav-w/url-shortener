package com.example.urlshortener.config;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class HitCountScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    @Scheduled(fixedRate = 30000) // every 30 secs
    public void syncHitCounts(){
        try{
            Set<String> keys = redisTemplate.keys("hit:*");

            if(keys == null || keys.isEmpty()){
                return;
            }

            for(String key:keys){
                String shortCode = key.replace("hit:", "");
                String value = redisTemplate.opsForValue().get(key);

                if(value == null) continue;

                Long hits = Long.parseLong(value);

                urlRepository.findByShortCode(shortCode).ifPresent(url -> {
                    url.setHitCount(url.getHitCount() + hits);
                    urlRepository.save(url);
                });

                redisTemplate.delete(key);
            }
        } catch(Exception e) {
            System.out.println("Redis not available, skipping...");
        }
    }
}
