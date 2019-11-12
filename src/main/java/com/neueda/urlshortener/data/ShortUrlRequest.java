package com.neueda.urlshortener.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortUrlRequest {
    private String url;

    @JsonCreator
    public ShortUrlRequest() {

    }

    @JsonCreator
    public ShortUrlRequest(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
