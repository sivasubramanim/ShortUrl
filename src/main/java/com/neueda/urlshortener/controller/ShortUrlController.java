package com.neueda.urlshortener.controller;

import com.neueda.urlshortener.data.ShortUrlRequest;
import com.neueda.urlshortener.service.ShortUrlService;
import com.neueda.urlshortener.utilities.ShortUrlConstants;
import com.neueda.urlshortener.utilities.ShortUrlUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class ShortUrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlController.class);
    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/{id}")
    public void redirect(@PathVariable String id, HttpServletResponse httpResponse) {
        try {
            if (id == null || id.isEmpty()) {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
                LOGGER.error("id is empty or null");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE + id);
            }
            LOGGER.info("Received short url for redirect with id: {}", id);
            String longUrl = shortUrlService.getLongUrl(id);
            if (longUrl == null || longUrl.isEmpty()) {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
                LOGGER.error("long url is empty or null");
                httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                meterRegistry.counter(ShortUrlConstants.REDIRECT_SUCCESS_KEY).increment();
                LOGGER.info("Performing redirect to url: {} " , longUrl);
                httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                httpResponse.setHeader("Location", longUrl);
            }
        } catch (Exception ex) {
            meterRegistry.counter(ShortUrlConstants.REDIRECT_FAILURE_KEY).increment();
            LOGGER.error(ex.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }

    }

    @PostMapping(value = "/shorturl", consumes = {"application/json"})
    public @ResponseBody
    ResponseEntity<String> createShortUrl(@RequestBody ShortUrlRequest shortUrlRequest, HttpServletRequest request) {

        if (shortUrlRequest == null || shortUrlRequest.getUrl() == null || shortUrlRequest.getUrl().isEmpty()) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error(ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.NULL_EMPTY_URL_MESSAGE);
        }
        LOGGER.info("Received input for url shortening: {}" , shortUrlRequest.getUrl());
        if (!ShortUrlUtil.isUrlValid(shortUrlRequest.getUrl())) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error(ShortUrlConstants.INVALID_URL_MESSAGE + shortUrlRequest.getUrl());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ShortUrlConstants.INVALID_URL_MESSAGE);
        }
        try {
            String shortUrl = shortUrlService.createShortUrl(getBaseUrl(request), shortUrlRequest.getUrl());
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_SUCCESS_KEY).increment();
            LOGGER.info("Shortened url: {}" , shortUrl);
            return new ResponseEntity<>(shortUrl, HttpStatus.OK);
        } catch (Exception ex) {
            meterRegistry.counter(ShortUrlConstants.CREATE_SHORT_URL_FAILURE_KEY).increment();
            LOGGER.error(ex.getMessage());
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
