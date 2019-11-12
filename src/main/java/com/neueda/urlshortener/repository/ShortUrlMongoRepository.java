package com.neueda.urlshortener.repository;

import com.neueda.urlshortener.data.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShortUrlMongoRepository extends MongoRepository<ShortUrl, String> {
    ShortUrl findShortUrlByShortId(String shortId);

}
