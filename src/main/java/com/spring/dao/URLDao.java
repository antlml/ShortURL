package com.spring.dao;

import com.spring.pojo.URLDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface URLDao {

    /**
     * 插入一条网址信息
     * @param urldo 
     * @return 数据库主键id
     */
    int insertLongURL(URLDO urldo);

    /**
     * 根据主键id查询出数据库中的原始长网址
     * @param id
     * @return 原始长网址
     */
    String queryLongURL(int id);
}
