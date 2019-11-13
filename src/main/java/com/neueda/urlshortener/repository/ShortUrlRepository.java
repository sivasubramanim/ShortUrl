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

    public ShortUrlRepository() {
        this.jedis = new Jedis();
        this.idKey = "id";
    }

    public ShortUrlRepository(Jedis jedis, String idKey) {
        this.jedis = jedis;
        this.idKey = idKey;
    }

    public Long getCounter() {
        Long counter = jedis.incr(idKey);
        LOGGER.info("[getCounter]: getting counter: {}", counter);
        return counter;
    }

    public String save(String key, String value) {
        if (stringRedisTemplate != null && isRedisActive(key) && stringRedisTemplate.hasKey(key)) {
            String redisValue = stringRedisTemplate.opsForValue().get(key);
            if (value.equals(redisValue)) {
                LOGGER.info("[save]: Key already exists in cache: {}", key);
            } else {
                Long counter = getCounter();
                key += ShortIdGen.createUniqueID(counter);
                saveUrlCache(key, value);
                saveUrlDb(key, value);
            }
        } else {
            String valueDb = getUrlFromDb(key);
            if (valueDb.isEmpty()) {
                saveUrlCache(key, value);
                saveUrlDb(key, value);
            } else {
                saveUrlCache(key, value);
            }
        }
        return key;
    }

    public String get(String key) {
        LOGGER.info("[get]: Retrieving at {}", key);
        String value;
        if (stringRedisTemplate != null && isRedisActive(key))
            value = stringRedisTemplate.opsForValue().get(key);
        else {
            value = getUrlFromDb(key);
        }
        return value;
    }

    private ShortUrl createShortUrl(String shortId, String longUrl) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortId(shortId);
        shortUrl.setLongUrl(longUrl);
        shortUrl.setCreatedBy(shortUrlUser);
        shortUrl.setCreatedDate(Date.from(Instant.now()));
        shortUrl.setModifiedBy(shortUrlUser);
        shortUrl.setModifiedDate(Date.from(Instant.now()));
        return shortUrl;
    }

    private String getUrlFromDb(String key) {
        String value = "";
        try {
            LOGGER.info("[getUrlFromDb]: Trying to retrieve the value from Database for key: {}", key);
            ShortUrl shortUrl = shortUrlMongoRepository.findShortUrlByShortId(key);
            if (shortUrl != null && shortUrl.getLongUrl() != null) {
                value = shortUrl.getLongUrl();
                LOGGER.info("[getUrlFromDb]: value sucessfully retrieved from Database:{} at {}", value, key);
                if (isRedisActive(key)) {
                    saveUrlCache(key, value);
                }
            }
        } catch (Exception ex) {
            LOGGER.error("[getUrlFromDb]: {}", ex.getMessage());
        }
        return value;
    }

    private void saveUrlDb(String key, String value) {
        try {
            ShortUrl shortUrl = createShortUrl(key, value);
            shortUrlMongoRepository.save(shortUrl);
            LOGGER.info("[saveUrlDb]: Saved into database: {}", shortUrl);
        } catch (Exception ex) {
            LOGGER.error("[saveUrlDb]: {}", ex.getMessage());
        }
    }

    private void saveUrlCache(String key, String value) {
        if (isRedisActive(key)) {
            LOGGER.info("[saveUrlCache]: Saving: {} at {}", value, key);
            stringRedisTemplate.opsForValue().set(key, value);
            LOGGER.info("[saveUrlCache]: Saved: {} at {}", value, key);
        }
    }

    private boolean isRedisActive(String key) {
        boolean isActive = false;
        try {
            stringRedisTemplate.hasKey(key);
            isActive = true;

        } catch (Exception ex) {
            LOGGER.error("[isRedisActive]: {}", ex.getMessage());
        }
        return isActive;

    }


}
