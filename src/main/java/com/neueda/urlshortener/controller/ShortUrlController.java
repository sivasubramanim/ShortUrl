package com.neueda.urlshortener.controller;

import com.neueda.urlshortener.data.UrlRequestResponse;
import com.neueda.urlshortener.service.ShortUrlService;
import com.neueda.urlshortener.utilities.ShortUrlConstants;
import com.neueda.urlshortener.utilities.ShortUrlUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;


@RestController
public class ShortUrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlController.class);
    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/{id}")
    public ResponseEntity redirect(@PathVariable String id) {
        try {
            if (id == null || id.isEmpty()) {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
                LOGGER.error("[redirect]: id is empty or null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE + id);
            }
            LOGGER.info("[redirect]: Received short url for redirect with id: {}", id);
            String longUrl = shortUrlService.getLongUrl(id);
            if (longUrl == null || longUrl.isEmpty()) {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
                LOGGER.error("[redirect]: long url is empty or null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
            } else {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_SUCCESS_KEY).increment();
                LOGGER.info("[redirect]: Performing redirect to url: {} " , longUrl);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setLocation(new URI(longUrl));
                return new ResponseEntity(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
            }
        } catch (Exception ex) {
            meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
            LOGGER.error("[redirect]: {}",ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PostMapping(value = "/shorturl", consumes = {"application/json"})
    public @ResponseBody
    ResponseEntity<UrlRequestResponse> createShortUrl(@RequestBody UrlRequestResponse UrlRequest, HttpServletRequest request) {
        if (UrlRequest == null || UrlRequest.getUrl() == null || UrlRequest.getUrl().isEmpty()) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error("[createShortUrl]: {}",ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
        }
        LOGGER.info("[createShortUrl]: Received input for url shortening: {}" , UrlRequest.getUrl());
        if (!ShortUrlUtil.isUrlValid(UrlRequest.getUrl())) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error("[createShortUrl]: {}",ShortUrlConstants.INVALID_URL_MESSAGE + UrlRequest.getUrl());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.INVALID_URL_MESSAGE);
        }
        try {
            String shortUrl = shortUrlService.createShortUrl(getBaseUrl(request), UrlRequest.getUrl());
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_SUCCESS_KEY).increment();
            LOGGER.info("[createShortUrl]: Shortened url: {}" , shortUrl);
            return new ResponseEntity<>(new UrlRequestResponse(shortUrl) , HttpStatus.OK);
        } catch (Exception ex) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error("[createShortUrl]: {}",ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort() + "/";
        String contextPath = request.getContextPath();
        return scheme + serverName + serverPort + contextPath;
    }
}
