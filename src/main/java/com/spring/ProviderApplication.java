package com.spring;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.endpoint.EndpointUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient // 开启
@SpringBootApplication
@EnableAutoConfiguration
@EnableCaching // 开启缓存
public class ProviderApplication {

//    DiscoveryClient
//    EndpointUtils

    public static void main(String[] args) {


        SpringApplication.run(ProviderApplication.class, args);
    }
}
