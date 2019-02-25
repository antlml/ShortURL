package com.spring.configBean;

import com.utils.BloomFilterAOFable;
import com.utils.LRUCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 该类提供项目中所需要的一些Bean
 */
@Configuration
public class ProjectBeansFactory {
    /**
     * 缓存操作
     * @param cacheManager 缓存管理器
     * @return Cache url_cache
     */
    @Bean
    public Cache getCache(@Autowired CacheManager cacheManager){
        return cacheManager.getCache("url_cache");
    }

    /**
     * 布隆过滤器
     * @param backupFilePath 备份文件的路径
     * @param falsePositiveProbability 容错率
     * @param expectedNumberOfElements 预计的元素数量
     * @param restoreFromFile 是否从文件中恢复
     * @param maxPieceAOFQueue 队列中消息达到此数值时写入到文件中
     * @return BloomFilterAOFable<String> 布隆实例
     */
    @Bean
    public BloomFilterAOFable<String> getBloom(
            @Value("${bloom.backupFilePath}") String backupFilePath,
            @Value("${bloom.falsePositiveProbability}") float  falsePositiveProbability,
            @Value("${bloom.expectedNumberOfElements}") int expectedNumberOfElements,
            @Value("${bloom.restoreFromFile}") boolean restoreFromFile,
            @Value("${bloom.maxPieceAOFQueue}") int maxPieceAOFQueue){
        return new BloomFilterAOFable<String>(falsePositiveProbability, expectedNumberOfElements,
                backupFilePath, restoreFromFile, maxPieceAOFQueue);
    }

    /**
     * 获取LRUCache实例
     * @param cacheCapacity 缓存系统键的数量
     * @return LRUCache<String, String>
     */
    @Bean
    public LRUCache<String, String> getLRUCache(
            @Value("${LRUCache.cacheCapacity}") int cacheCapacity){
        return new LRUCache<String, String>(cacheCapacity);
    }
}
