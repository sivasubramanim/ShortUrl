package com.neueda.urlshortener.service;

import com.neueda.urlshortener.repository.ShortUrlMongoRepository;
import com.neueda.urlshortener.repository.ShortUrlRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.anyString;

public class ShortUrlServiceTests {

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
        String shortId="c0a0206a";
        String url =  shortUrlService.createShortUrl("","https://www.google.com/");
        assertEquals(url,"");
    }

    @Test
    public void EmptyLongUrlCreateShortUrlTest(){
        String shortId="c0a0206a";
        String url =  shortUrlService.createShortUrl("http://localhost/","");
        assertEquals(url,"");
    }

    @Test
    public void CreateShortUrlTest(){
        String shortId="c0a0206a";
        String requestUrl="http://localhost/";
        Mockito.when(shortUrlRepository.save(anyString(),anyString())).thenReturn(shortId);
        String url =  shortUrlService.createShortUrl(requestUrl,"https://www.google.com/");
        assertEquals(url,requestUrl+shortId);
    }

    @Test(expected = ResponseStatusException.class)
    public void EmptyGetLongUrlTest(){
        String encodedUrl ="aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8=";
        Mockito.when(shortUrlRepository.get(anyString())).thenReturn(encodedUrl);
       shortUrlService.getLongUrl("");

    }
    @Test(expected = ResponseStatusException.class)
    public void NullGetLongUrlTest(){
        String encodedUrl ="aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8=";
        Mockito.when(shortUrlRepository.get(anyString())).thenReturn(encodedUrl);
       shortUrlService.getLongUrl(null);
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
