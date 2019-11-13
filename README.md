[![Build Status](https://travis-ci.org/sivasubramanim/ShortUrl.svg?branch=master)](https://travis-ci.org/sivasubramanim/ShortUrl)

# ShortUrl
Application to create short Urls and implement forwarding of short Urls to original ones

## Problem Summary:
Most of us are familiar with seeing URLs like bit.ly or t.co on our Twitter or Facebook feeds. These are examples of shortened URLs, which are a short alias or pointer to a longer page link. 
#### Mandatory Requirements
Design and implement an API for short URL creation<br/>
Implement forwarding of short URLs to the original ones<br/>
There should be some form of persistent storage<br/>
The application should be distributed as one or more Docker images<br/>
The application should be readable, maintainable, and extensible where appropriate.<br/>

#### Additional Requirements
Design and implement an API for gathering different statistics

## Solution Overview
The below depicts a high level system view of the project.<br/>
![Architecture Diagram](https://user-images.githubusercontent.com/51107434/68769937-32a1cd80-064b-11ea-822a-9c1819009143.png)

The solution is built in such a way that we have spin multiple instances of the backend service connected to the Distributed Redis cache server to serve multiple requests. The Redis cache server is introduced as a middle layer to improve the performance of the application.

##Technology Stack:
* [Spring Boot](http://spring.io/projects/spring-boot) for creating the RESTful Web Services. API for short URL creation and API for forwarding of short URLs to original ones
* [Redis Cache](https://redis.io/) used as a distributed in-memory data store. Application is mostly served by the cache server for high performance.
* [MongoDB](https://www.mongodb.com/) used as a persisten storage for the application. It can be used as a distributed database server. The application is built in a such a way that it cab be switched to different databases.
* [Google Guava](https://github.com/google/guava) used for hashing functions. The short key generation in the application uses a combination of hash function and counter to generate unique short keys easily.
* [Spring Actuator](https://spring.io/guides/gs/actuator-service/) used for monitoring and managing your Spring Boot application. Using Actuator endpoints for gathering different statistics of the application. Currently the application is configured to gather statistics from health, info and metrics API. Introduced 4 new custom metrics as part of the implementation. We can extend this furthure more based on the application need.
* [Maven](https://maven.apache.org/) for building the projects
* [Junit](https://junit.org/) for writing unit, integration tests for the application. 
* [Mockito](https://site.mockito.org/) used for writing test cases for service layer with Repository mocks.
* [Embeded Redis Server](https://github.com/ozimov/embedded-redis) used in the test layer. All the application integration tests run using embeded redis server and does not require any installation on the test environment.
* [Embeded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) used in the test layer. All the application integration tests run using embeded mongodb server and does not require any installation on the test environment.
* [Docker](https://www.docker.com/) for building and managing the application as images. The build and deploy phases are completed part of Docker.

