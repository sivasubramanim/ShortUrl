package com.neueda.urlshortener.utilities;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortUrlException  extends RuntimeException{
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlException.class);

    public ShortUrlException(){
        super();
    }
    public ShortUrlException(String message){
        super(message);
    }
    public ShortUrlException(String message, Throwable cause){
        LOGGER.error(message);
    }

}
