package com.spring.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ComponentScan("main")
public class URLServiceImplTest {

    @Autowired
    URLServiceImpl service;

    @Test
    public void queryRealURL() {
        System.out.println(service.queryRealURL("w"));
    }

    @Test
    public void shortenURL() {
        String shortURL = service.shortenURL("www.baidu.com");
        System.out.println(shortURL);
    }
}