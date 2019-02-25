package com.spring.service;


import com.spring.dao.URLDao;
import com.spring.pojo.URLDO;
import com.utils.BloomFilterAOFable;
import com.utils.LRUCache;
import com.utils.MD5;
import com.utils.PECode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 网址服务实现类
 */
@Service
public class URLServiceImpl implements URLService {

    /* LRUCache 缓存最近常用的长短连接 */
    @Autowired
    LRUCache<String, String> cache;

    /* 布隆过滤器 判断数据中是否含有请求中的短链接 */
    @Autowired
    BloomFilterAOFable<String> bloom;

    /* 缓存管理器 */
    @Autowired
    Cache url_cache;

    /* UrlDAO层 */
    @Autowired
    URLDao urlDao;

    /**
     * 根据短网址查询真实网址
     * @param shortUrl 短网址
     * @return String 真实网址
     */
    @Override
    @SuppressWarnings("unchecked")
    public String queryRealURL(String shortUrl) {
        // 判断数据库中是否存在该网址的映射关系，不存在直接返回
        if(!bloom.contains(shortUrl)){
            return "This id no url mapping!";
        }
        // 尝试从redis缓存中取数据
        Cache.ValueWrapper realUrlWrapper = url_cache.get("shortUrl_" + shortUrl);
        String realUrl = (String) realUrlWrapper.get();
        if(realUrl != null){
            // 取得数据直接返回
            return realUrl;
        }
        // 计算对应的数据库id
        int id = PECode._62_to_10(shortUrl);
        String longUrl = urlDao.queryLongURL(id);
        // 添加到缓存中
        url_cache.put("shortUrl_" + shortUrl, longUrl);
        return longUrl;
    }

    /**
     * 缩短网址
     * @param longURL 原始的长网址
     * @return String 缩短之后的网址
     */
    @Override
    @SuppressWarnings("unchecked")
    public String shortenURL(String longURL) {
        // 若LRU缓存中有对应的短链接，则直接返回
        String shortUrl;
        shortUrl = (String) cache.get(longURL);
        if(shortUrl != null){
            return shortUrl;
        }
        int sqlId;
        URLDO urldo = new URLDO();
        // LRU缓存中没有则插入数据库
        try {
            urldo.setInitialURL(longURL);
            urldo.setIdxHash(MD5.createMD5(longURL));
            urlDao.insertLongURL(urldo);
            sqlId = urldo.getId();
            shortUrl = PECode._10_to_62(sqlId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 插入布隆
        bloom.add(shortUrl);
        // 插入LRU缓存
        cache.addToCache(longURL, shortUrl);
        // 需要通过缓存管理手动添加到Redis
        url_cache.put("shortUrl_" + shortUrl, longURL);
        return shortUrl;
    }
}
