package com.neueda.urlshortener.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Base64;

public class ShortUrlUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlUtil.class);
    public static boolean isUrlValid(String url){
        try{
            new URL(url).toURI();
            return true;
        }catch (Exception ex){
            return false;

        }
    }
    public static String urlEncode(String url){
        return Base64.getUrlEncoder().encodeToString(url.getBytes());
    }
    public static String urlDecode(String encodedUrl){
        return new String(Base64.getUrlDecoder().decode(encodedUrl));
    }
    public static String BuildShortUrl(String serverHost, String shortUrl){
           return serverHost + shortUrl;
    }

}
