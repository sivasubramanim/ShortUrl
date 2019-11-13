package com.neueda.urlshortener.repository;

import com.neueda.urlshortener.repository.ShortUrlRepository;
import com.neueda.urlshortener.utilities.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShortUrlRepositoryTest {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Test
    public void GetCounterTest(){
       Long counter = shortUrlRepository.getCounter() + 1;
       Long nextCounter = shortUrlRepository.getCounter();
       assertEquals(counter,nextCounter);
    }
    @Test
    public void SaveTest(){
        String key="test";
        String value="test";
       String output =  shortUrlRepository.save(key,value);
       assertEquals(output,key);
    }
    @Test
    public void SaveDuplicateKeyTest(){
        String key="test";
        String value="test";
        String value1="test123";
        shortUrlRepository.save(key,value);
        String output =  shortUrlRepository.save(key,value1);
        assertNotEquals(output,key);
    }

    @Test
    public void GetTest(){
        String key="test";
        String value="test";
        shortUrlRepository.save(key,value);
        String output =  shortUrlRepository.get(key);
        assertEquals(output,value);
    }
}
