package com.neueda.urlshortener;

import com.neueda.urlshortener.data.UrlRequestResponse;
import com.neueda.urlshortener.utilities.ShortUrlUtilTests;
import com.neueda.urlshortener.utilities.TestRedisConfiguration;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlshortenerApplicationTest {


    private ShortUrlUtilTests shortUrlUtilTests;


    @Autowired
    private TestRestTemplate restTemplate;

    UrlshortenerApplicationTest() {
        shortUrlUtilTests = new ShortUrlUtilTests();
    }



    @Test
    public void WrongUrlTest() {
        String url = "http://www.example.com";
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("/dummy/shorturl", new UrlRequestResponse(url), String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String output = responseEntity.getBody();
        System.out.println(output);
    }

    @Test
    public void NotAllowedVerbTest() {

           }

    @Test
    public void CreateShortUrlNullUrlTest() {
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("/shorturl", new UrlRequestResponse(null), String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String output = responseEntity.getBody();
        System.out.println(output);
    }

    @Test
    public void CreateShortUrlEmptyUrlTest() {
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("/shorturl", new UrlRequestResponse(""), String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String output = responseEntity.getBody();
        System.out.println(output);
    }

    @Test
    public void CreateShortUrlInvalidUrlTest() {
        String url="fsadfsafda";
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("/shorturl", new UrlRequestResponse(url), String.class);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        String output = responseEntity.getBody();
        System.out.println(output);
    }

    @Test
    public void CreateShortUrlValidUrlTest() {
        String url="https://www.neueda.com/about-us";
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("/shorturl", new UrlRequestResponse(url), String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String output = responseEntity.getBody();

        System.out.println(output);
    }

    @Test
    public void CreateShortUrlMaxLengthUrlTest() {
    }

   @Test
    public void CreateDynamicShortUrlsTest() {
        try {
            List<String> urls = shortUrlUtilTests.readFile("input1.txt");
            for (String url : urls) {
                ResponseEntity<String> responseEntity =
                        restTemplate.postForEntity("/shorturl", new UrlRequestResponse(url), String.class);
                assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                String output = responseEntity.getBody();
                System.out.println(output);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());

        }
    }

}
