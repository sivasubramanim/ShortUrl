package com.neueda.urlshortener.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UrlRequestResponse {
    private String url;

    @JsonCreator
    public UrlRequestResponse() {

    }

    @JsonCreator
    public UrlRequestResponse(@JsonProperty("url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
