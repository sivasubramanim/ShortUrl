package com.neueda.urlshortener.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;
    public TestRedisConfiguration() {
        this.redisServer = new RedisServer(6379);
    }
    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }
    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
