package com.neueda.urlshortener.service;

import com.google.common.hash.Hashing;
import com.neueda.urlshortener.repository.ShortUrlRepository;
import com.neueda.urlshortener.utilities.ShortUrlConstants;
import com.neueda.urlshortener.utilities.ShortUrlException;
import com.neueda.urlshortener.utilities.ShortUrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;


@Service
public class ShortUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlService.class);
    @Autowired
    ShortUrlRepository shortUrlRepository;

    public String createShortUrl(String requestUrl,String longUrl) {
        LOGGER.info("creating short url for: {}" , longUrl);
        if(requestUrl == null || requestUrl.isEmpty() || longUrl == null || longUrl.isEmpty() ){
            LOGGER.error(ShortUrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL_MESSAGE + requestUrl + longUrl);
            throw new ShortUrlException(ShortUrlConstants.NULL_EMPTY_REQUEST_URL_SHORT_URL_MESSAGE + requestUrl + longUrl);
        }
        String shortHash = Hashing.murmur3_32().hashString(longUrl, StandardCharsets.UTF_8).toString();
        String shortId = shortUrlRepository.save(shortHash, ShortUrlUtil.urlEncode(longUrl));
        String shortUrl  = ShortUrlUtil.BuildShortUrl(requestUrl,shortId);
        LOGGER.info("created short url is: {}" , shortUrl);
        return shortUrl;
    }
     public String getLongUrl(String shortId) {
         if(shortId == null || shortId.isEmpty()) {
             LOGGER.error("short id is null or empty");
             throw new ShortUrlException(ShortUrlConstants.NULL_EMPTY_SHORT_ID_MESSAGE + shortId);
         }
        LOGGER.info("getting url for shortId: {}" , shortId);
        String longurl = shortUrlRepository.get(shortId);
        if(longurl == null || longurl.isEmpty()) {
            LOGGER.error("long url is null or empty");
            throw new ShortUrlException(ShortUrlConstants.INVALID_URL_MESSAGE + shortId);
        }
       else {
           String longUrl = ShortUrlUtil.urlDecode(longurl);
           LOGGER.info("long url is: {}" , longUrl);
            return longUrl;
        }
    }
}
