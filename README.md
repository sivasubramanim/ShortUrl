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
The below diagram depicts a high level system view of the project.<br/>
![Architecture Diagram](https://user-images.githubusercontent.com/51107434/68769937-32a1cd80-064b-11ea-822a-9c1819009143.png)

Component View of the Backend Short Url Service:
* ShortUrlController - Manages all the Rest API endpoints of the application
* ShortUrlService - Manages the core functionalities such as short id creation and retrival of long url
* ShortUrlRepository - Manages all the data and builds the order of priority between redis cache and mongo db for data
* ShortUrlMongoRepository - Manages Mongo CRUD
* ShortIdUtilities - Manages all the application utilities

The solution is built in such a way that we can spin multiple instances of the backend service connected to the Distributed Redis cache server and MongoDB to serve multiple requests. The Redis cache server is introduced as a middle layer to improve the performance of the application.

## Technology Stack:
* [Spring Boot](http://spring.io/projects/spring-boot) for creating the RESTful Web Services. API for short URL creation and API for forwarding of short URLs to original ones
* [Redis Cache](https://redis.io/) used as a distributed in-memory data store. Application is mostly served by the cache server for high performance.
* [MongoDB](https://www.mongodb.com/) used as a persistent storage for the application. It can be used as a distributed database server. The application is built in a such a way that it can be switched to different databases.
* [Google Guava](https://github.com/google/guava) used for hashing functions. The short key generation in the application uses a combination of hash function and counter to generate unique short keys easily.
* [Spring Actuator](https://spring.io/guides/gs/actuator-service/) used for monitoring and managing your Spring Boot application. Using Actuator endpoints for gathering different statistics of the application. Currently the application is configured to gather statistics from health, info and metrics API. Introduced 4 new custom metrics as part of the implementation. We can extend this furthure more based on the application need.
* [Maven](https://maven.apache.org/) for building the projects
* [Junit](https://junit.org/) for writing unit, integration tests for the application. 
* [Mockito](https://site.mockito.org/) used for writing test cases for service layer with Repository mocks.
* [Embeded Redis Server](https://github.com/ozimov/embedded-redis) used in the test layer. All the application integration tests run using embeded redis server and does not require any installation on the test environment.
* [Embeded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo) used in the test layer. All the application integration tests run using embeded mongodb server and does not require any installation on the test environment.
* [Docker](https://www.docker.com/) for building and managing the application as images. The build and deploy phases are completed part of Docker.
* [TravisCI](https://travis-ci.org/) for continous integration.
## Build & Run Steps
#### Download
```sh
$ git clone https://github.com/sivasubramanim/ShortUrl.git
```
#### Build
```sh
$ Navigate to downloaded directory
$ docker-compose build
```
#### Deploy & Run
```sh
$ docker-compose up
```
With the default setup, the application should be available at http://localhost:8080/
## API Documentation
#### Short Url APP APIs

URI | HTTP Method | Description | Content-Type | Sample Input(Body) | Sample Output |
:---: | :--- | :---: | :--- | :--- | :--- | 
/shorturl | POST | API to find/create a short url | application/json | {"url":"https://www.google.com/"} | {"url": "http://localhost:8080/42d20732"} |
/{Id} | Get | API to forward short URLs to original ones | - | - | Response will be redirected to the original one with Http status as 301 |

#### Statistics APIs
URI | HTTP Method | Description | Content-Type | Sample Input(Body) | Sample Output |
:---: | :--- | :---: | :--- | :--- | :--- | 
/actuator | GET | API to list all the statistics API configured | - | - | Currently in this app configured info, health and metrics endpoint |
/actuator/info | GET | API to get general info about the application | - | - | {  "app": {"name": "urlshortener","description": "Application to create short Urls and implement forwarding of short Urls to original ones","version": "1.0.0-RELEASE","encoding": "UTF-8","java": { "version": "1.8.0_171" } }} |
/actuator/health | GET | API to get application health | - | - | {  "status": "UP",  "components": {    "diskSpace": {      "status": "UP",      "details": {        "total": 255157833728,        "free": 7633149952, "threshold": 10485760     }    },    "mongo": {      "status": "UP",      "details": {        "version": "4.2.1"      }    },    "ping": {      "status": "UP"    },    "redis": {      "status": "UP","details": {         "version": "2.8.19"      }    }  }}|
/actuator/metrics | GET | API to get different application metrics/stats | - | - | Metrics section contains more 30 metrics. each metric can be individually seen |
/actuator/metrics/{metricsname} | GET | API to get details about a specific metric | - | - | - |

###### Custom Metrics Name :
* shorturl.success - Metrics to capture number of successful short url creation</br>
* shorturl.failure - Metrics to capture number of failed scenarios in short url creation</br>
* redirect.success - Metrics to capture number of successful forwarding</br>
* redirect.failure - Metrics to capture number of failed scenarios in forwarding</br>                                                                   
## Salient Features
* Short URL backend service is highly independent service. The application can be tested independently without mongo db server and redis server installation
* In runtime, if Distributed Cache server or Database goes down, the application wil not fail. It will log the connection failure and continue to serve the users using only database or only cache server.
* Docker containers are used for build, test, deploy and run scenarios. Only Docker is required to be present in the nodes.
## Next Steps
I have taken care of the MVP requirements for this project. The project has scope for future enhancements and improvements such as:
* Enabling HTTPS for the application.
* Implementing Authentication and Authorisation for the service
* Implementing other APIs for the service
