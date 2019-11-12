package com.neueda.urlshortener.repository;

import com.neueda.urlshortener.data.ShortUrl;
import com.neueda.urlshortener.utilities.ShortIdGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.time.Instant;
import java.util.Date;


@Repository
public class ShortUrlRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlRepository.class);
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ShortUrlMongoRepository shortUrlMongoRepository;

    @Value("${shorturl.user}")
    String shortUrlUser;

    private final Jedis jedis;
    private final String idKey;
    private final String urlKey;

    public ShortUrlRepository() {
        this.jedis = new Jedis();
        this.idKey = "id";
        this.urlKey = "url:";
    }

    public ShortUrlRepository(Jedis jedis, String idKey, String urlKey) {
        this.jedis = jedis;
        this.idKey = idKey;
        this.urlKey = urlKey;
    }

    public Long getCounter() {
        Long counter = jedis.incr(idKey);
        LOGGER.info("getting counter: {}", counter);
        return counter;
    }

    public String save(String key, String value) {
        if (!stringRedisTemplate.hasKey(key)) {
            LOGGER.info("Saving: {} at {}", value, key);
            stringRedisTemplate.opsForValue().set(key, value);
            LOGGER.info("Saved: {} at {}", value, key);
            ShortUrl shortUrl = createShortUrl(key, value);
            shortUrlMongoRepository.save(shortUrl);
            LOGGER.info("Saved into database: {}", shortUrl.toString());

        } else {
            String redisValue = stringRedisTemplate.opsForValue().get(key);
            if (!redisValue.equals(value)) {
                Long counter = getCounter();
                key += ShortIdGen.createUniqueID(counter);
                LOGGER.info("Saving with counter: {} at {}", value, key);
                stringRedisTemplate.opsForValue().set(key, value);
                LOGGER.info("Saved with counter: {} at {}", value, key);
                ShortUrl shortUrl = createShortUrl(key, value);
                shortUrlMongoRepository.save(shortUrl);
                LOGGER.info("Saved into database: {}", shortUrl.toString());
            }
            else
                LOGGER.info("Key already exists: {}", key);

        }
        return key;
    }

    public String get(String key) {
        LOGGER.info("Retrieving at {}", key);
        String value = "";
        if (stringRedisTemplate.hasKey(key))
            value = stringRedisTemplate.opsForValue().get(key);
        else {
            LOGGER.info("Trying to retrieve the value,key from Database: {} at {}", value, key);
            ShortUrl shortUrl = shortUrlMongoRepository.findShortUrlByShortId(key);
            if (shortUrl != null) {
                value = shortUrl.getLongUrl();
                LOGGER.info("value sucessfully retrieved from Database:{} at {}", value, key);
                stringRedisTemplate.opsForValue().set(key, value);
                LOGGER.info("saved into redis sucessfully");
            }
        }
        return value;
    }
    private ShortUrl createShortUrl(String shortId, String longUrl) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId(shortId);
        shortUrl.setLongUrl(longUrl);
        shortUrl.setCreatedBy(shortUrlUser);
        shortUrl.setCreatedDate(Date.from(Instant.now()));
        return shortUrl;
    }
}
