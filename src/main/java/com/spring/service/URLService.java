package com.spring.service;

/**
 * 网址相关接口
 */
public interface URLService {
    /**
     * 根据短网址查询出真实网址
     * @param shortURL
     * @return
     */
    String queryRealURL(String shortURL);

    /**
     * 真实网址转短网址
     * @param longURL
     * @return
     */
    String shortenURL(String longURL);

}
