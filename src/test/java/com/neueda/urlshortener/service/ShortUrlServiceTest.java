package com.neueda.urlshortener.service;

import com.neueda.urlshortener.repository.ShortUrlMongoRepository;
import com.neueda.urlshortener.repository.ShortUrlRepository;
import com.neueda.urlshortener.utilities.ShortUrlException;
import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class ShortUrlServiceTest {

    @InjectMocks
    private ShortUrlService shortUrlService;
    @Mock
    private ShortUrlRepository shortUrlRepository;
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void EmptyRequestCreateShortUrlTest(){
        Assertions.assertThrows(ShortUrlException.class, () -> {
            shortUrlService.createShortUrl("","https://www.google.com/");
        });

    }

    @Test
    public void EmptyLongUrlCreateShortUrlTest(){
        Assertions.assertThrows(ShortUrlException.class, () -> {
            shortUrlService.createShortUrl("http://localhost/","");
        });

    }

    @Test
    public void CreateShortUrlTest(){
        String shortId="c0a0206a";
        String requestUrl="http://localhost/";
        Mockito.when(shortUrlRepository.save(anyString(),anyString())).thenReturn(shortId);
        String url =  shortUrlService.createShortUrl(requestUrl,"https://www.google.com/");
        assertEquals(url,requestUrl+shortId);
    }

    @Test
    public void EmptyGetLongUrlTest(){

        Assertions.assertThrows(ShortUrlException.class, () -> {
            shortUrlService.getLongUrl("");
        });


    }
    @Test
    public void NullGetLongUrlTest(){
        Assertions.assertThrows(ShortUrlException.class, () -> {
            shortUrlService.getLongUrl(null);
        });

    }

    @Test
    public void ValidGetLongUrlTest(){
        String shortId = "c0a0206a";
        String encodedUrl ="aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8=";
        String decodedUrl ="https://www.google.com/";
        Mockito.when(shortUrlRepository.get(anyString())).thenReturn(encodedUrl);
        String url = shortUrlService.getLongUrl(shortId);
        assertEquals(url,decodedUrl);
    }
}
