package com.spring.provider;


import com.utils.BloomFilter;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

@Configuration
public class ConfigBean {
    public static void main(String[] args) throws InterruptedException {
        BloomFilter<String> filter = new BloomFilter<String>
                (0.01, 2000000000);
        while (true){
            filter.add(String.valueOf(new Random().nextInt()));
        }

    }
}
