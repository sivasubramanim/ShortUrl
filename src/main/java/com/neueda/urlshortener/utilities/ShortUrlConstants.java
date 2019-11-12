package com.neueda.urlshortener.utilities;

public class ShortUrlConstants {
    public static final String NULL_EMPTY_URL_MESSAGE = "url is null or empty: ";
    public static final String INVALID_URL_MESSAGE="url is not valid: ";
    public static final String NULL_EMPTY_REQUEST_URL_SHORT_URL_MESSAGE = "request url or short url is null or empty: ";
    public static final String NULL_EMPTY_SHORT_ID_MESSAGE="url is not valid: ";

    //constants for metric registry
    public static final String REDIRECT_FAILURE_KEY="redirect.failure";
    public static final String REDIRECT_SUCCESS_KEY="redirect.success";
    public static final String CREATE_SHORT_URL_SUCCESS_KEY="shorturl.success";
    public static final String CREATE_SHORT_URL_FAILURE_KEY="shorturl.failure";

    public static final String FILE_NOT_FOUND_EX_MESSAGE=" file not found ";
    public static final String GENERAL_EX_MESSAGE=" exception occured with message: ";

}
