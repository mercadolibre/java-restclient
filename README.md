# Rest Client

Lightweight REST client implementation for Java 1.7+.<br/>

For questions and support please contact [services@mercadolibre.com](mailto:services@mercadolibre.com)

# Contents

* [Dependencies](#dependencies)
* [Building a REST Client](#building-a-rest-client)
* [Understanding RESTPool](#understanding-restpool)
* [Basic Usage](#basic-usage)
    * [Basic Requests](#basic-requests)
        * [Response Handling](#response-handling)
    * [Default RestClient](#default-restclient)
    * [Defaults List](#defaults-list)
* [Advanced Usage](#advanced-usage)
    * [Customizing Requests](#customizing-requests)
        * [Specifying a Pool](#specifying-a-pool)
        * [Adding a Proxy](#adding-a-proxy)
        * [Adding Basic Authentication](#adding-basic-authentication)
    * [Using Interceptors](#using-interceptors)
    * [Downloading Data to a Stream](#downloading-data-to-a-stream)
    * [Uploading Multipart Data](#uploading-multipart-data)
    * [Retry Strategies](#retry-strategies)
        * [Simple Retry Strategy](#simple-retry-strategy)
        * [Exponential Backoff Retry Strategy](#exponential-backoff-retry-strategy)
* [Using Caches](#using-caches)
    * [Local Cache](#local-cache)
    * [Memcached Cache](#memcached-cache)
    * [KVS Cache](#kvs-cache)
* [Async API](#async-api)
* [Serializers](#serializers)
* [Metrics](#metrics)
* [Logging](#logging)
* [Mocks](#mocks)
    * [Dependency](#dependency)
    * [Usage Example](#usage-example)

# Dependencies

You must define the repository resolver

```xml
<repository>
    <id>java-restclient-mvn-repo</id>
    <url>https://raw.github.com/mercadolibre/java-restclient/mvn-repo/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

and the dependency itself

```xml
<dependency>
    <groupId>com.mercadolibre.restclient</groupId>
    <artifactId>restclient-default</artifactId>
    <version>0.0.1</version>
</dependency>
```

# Building a REST Client

A [RestClient](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/RestClient.html) instance uses a collection of `RESTPool` to route its requests.

So, first you have to build as many instances of `RESTPool` as you need using its builder. Please take also a moment to read about `RESTPool` [concept](#understanding-restpool).
```java
RESTPool aPool = RESTPool.builder()
    .withName("my_pool")
    .build();
```

For all the options available please take a look at the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/RESTPool.html).

Once you have built all of your pool instances, you can build your `RestClient` instance.
Every `RestClient` instance also contains a default `RESTPool` implementation, with typical parameters. It´ll be used when no pool is defined in a request.

```java
RestClient restClient = RestClient.builder()
    .withPool(aPool, bPool)
    .build();
```

This instance is thread safe and it should be shared across resources in your application.
For typical uses, youd only need just one instance of `RestClient` in your entire application.

# Understanding RESTPool

A `RESTPool` is a collection of HTTP resource definitions, _it´s not just a connection pool_.<br />
You can think of it as a separate client, intended to handle some group of resources and that is part of a `RestClient`, which instead is intended to bind many `RESTPool` instances and abstract HTTP calls.<br />
It groups together connection pool definitions (max connections, socket timeout, etc) with high level features such as proxy, basic authentication, interceptors and so on.

For every request you make you must [explicitly specify](#specifying-a-pool) which `RESTPool` should be used, in case you don´t want your request to be handled by the provided default `RESTPool`.

Some typical uses are

- Fast and slow resources. You could create a `RESTPool` instance for each one, the former with short timeouts and the latter with longer ones.
- Internal and external resources. You could create an instance of `RESTPool` with no proxy for internal usage and another with a proxy for the rest.
- Different throughput. If you expect large number of requests to a particular group of resources, you can create a `RESTPool` with a larger number of connections to handle them.
- Poor SLA. If you know some resources are in some way unreliable (bad network, pool external implementation, etc) you may define a dedicated `RESTPool` with a more aggressive `RetryStrategy`.

# Basic Usage

## Basic Requests

You can use your `RestClient` instance right away, by routing requests through its default pool.

```java
Response response = restClient.get("http://yourdomain.com/resource");
```
You may want to take a look at the `Response` [javadoc](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/Response.html).

In a similar way, you can add custom headers to your request

```java
Headers headers = new Headers().add("Content-Type", "text/plain");
Response response = restClient.get("http://yourdomain.com/resource", headers);
```

You can find another ways of building headers by looking at its [javadoc](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/http/Headers.html).


In case you want to send a request with body, you should do it as a byte array.

```java
byte[] body = "{\"text\":\"hello\"}".getBytes();
Response response = restClient.post("http://yourdomain.com/resource", body);
```

Headers are allowed in a similar way
```java
Headers headers = new Headers().add("Content-Type", "text/plain");
byte[] body = "hello".getBytes();
Response response = restClient.post("http://yourdomain.com/resource", headers, body);
```

### Response Handling

A `Response` object provides methods to get HTTP response data

```java
Response response = restClient.get("http://yourdomain.com/resource");

int status = response.getStatus();
int headers = response.getHeaders();
byte[] body = response.getBytes();
```

Also, if you have a `Serializer` [registered](#serializers) for current Content-Type, you can get its marshaled data as `Object` or as another you provide.

```java
Response response = restClient.get("http://yourdomain.com/get_item");

Object body = response.getData();
Item body = response.getData(Item.class);
```

## Default RestClient

In case you would like to route your requests only through the default pool, you could use the provided `RestClient` default implementation.
```java
Response response = RestClient.getDefault().get("http://yourdomain.com/resource");
```

## Defaults List

When you use a default implementation the following features apply

- Pool parameters tailored for average usage
- application/json as Content-Type and Accept headers
- gzip/deflate handling for incoming data


# Advanced Usage

## Customizing Requests

It's possible to specify many request scoped features for sync and async calls. Many of then can also be specified poolwise.
For the complete reference, please visit the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/RequestBuilder.html).

### Specifying a Pool

```java
Response response = restClient.withPool(aPool).get("http://yourdomain.com/resource");
```

### Adding a Proxy

```java
Response response = restClient.withProxy("http://proxy",80).get("http://yourdomain.com/resource");
```

### Adding Basic Authentication

```java
Response response = restClient.withAuthentication("http://auth", 80, "user", "pass").get("http://yourdomain.com/resource");
```

## Using Interceptors

An interceptor applies over a request, just before sending it; or over a response, right after receiving it. In both cases, they are stored in a deque, so you can easily manage the order in which they are applied.

To intercept a request, just implement a `RequestInterceptor`, or maybe one of the [provided](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/interceptor/RequestInterceptor.html) may help.

```java
Response response = restClient.withInterceptorFirst(new YourRequestInterceptor()).get("http://yourdomain.com/resource");
```

```java
Response response = restClient.withInterceptorLast(new YourRequestInterceptor()).get("http://yourdomain.com/resource");
```

As for response interceptors, provide your own by implementing `ResponseInterceptor`. You can take a look at the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/interceptor/ResponseInterceptor.html).

```java
Response response = restClient.withInterceptorFirst(new YourResponseInterceptor()).get("http://yourdomain.com/resource");
```

```java
Response response = restClient.withInterceptorLast(new YourResponseInterceptor()).get("http://yourdomain.com/resource");
```

## Downloading Data to a Stream

You can provide an `OutputStream` where you want your data to be streamed. Notice that trying to also fetch data from associated response will be null.
Remember to close stream upon completion.

```java
Response response = restClient.get("http://yourdomain.com/resource", yourOutputStream);
```

## Uploading Multipart Data

To upload multipart data, just add parts and make your request. Available parts can be found in the [javadoc](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/multipart/package-summary.html).

```java
Response response = restClient
    .withPart(aPart)
    .withPart(bPart)
    .post("http://yourdomain.com/resource");
```

## Retry Strategies

You may define a retry strategy that specifies when a response is to be considered a failure, and an action thatll be run on each retry call.

We provide two basic strategies

### Simple Retry Strategy

Itll just retry for a fixed number of times and wait for a fixed interval between runs

```java
Response response = restClient
    .withRetryStrategy(new SimpleRetryStrategy(MAX_RETRIES, WAIT_MS))
    .get("http://yourdomain.com/resource");
```

### Exponential Backoff Retry Strategy

It´ll increase wait time between retries between a min and a max value, growing exponentially between runs

```java
Response response = restClient
    .withRetryStrategy(new ExponentialBackoffRetryStrategy(MIN_MS, MAX_MS))
    .get("http://yourdomain.com/resource");
```

For additional information and parameters, please take a look at the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/retry/package-summary.html).

# Using Caches

We provide local, memcached and KVS cache implementations, thatll cache requests based on their Cache-Control header info.

All cache implementations can be chained, to be made multilevel. Every constructor has a version with the next level as the last argument.

For more information about Cache-Control you can take a look [here](https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9) and [here](https://tools.ietf.org/html/rfc5861).

## Local Cache

```java
RESTCache cache = new RESTLocalCache("my_cache", MAX_ELEMENTS);
Response response = restClient
    .withCache(cache)
    .get("http://yourdomain.com/resource");
```

## Memcached Cache

In this case, you must provide a wrapper over your Memcached client

```java
RESTMemcachedClient client = new MyRESTMemcachedClient();
RESTCache cache = new RESTMemcachedCache("my_cache", client);

Response response = restClient
    .withCache(cache)
    .get("http://yourdomain.com/resource");
```

## KVS Cache

In a similar way, you must wrap your current KVS client.

```java
RESTKvsClient client = new MyRESTKvsClient();
RESTCache cache = new RESTKvsCache("my_cache", client);

Response response = restClient
    .withCache(cache)
    .get("http://yourdomain.com/resource");
```

# Async API

Asynchronous calls are handled similar to their synchronous counterpart, we just return a `Future<Response>` as a promise of call completion.
A `RestException` is raised if request could not be built up, but every other exception will be wrapped around Future´s `ExecutionException` upon get.

Caching and retries are handled under the hood. Both of them, as well as call themselves, are handled in a non blocking way.

As an example, an async GET could be made as

```java
Future<Response> response = restClient.asyncGet("http://yourdomain.com/resource");
int status = response.get().getStatus();
```

There's also the possibility to specify an instance of `Callback<Response>` as a completion callback for current requst, instead of getting a `Future` instance.
```java
restClient.asyncGet("http://yourdomain.com/resource", myCallback);
```

For a complete reference on both operation modes please take a look at the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/RestClient.html).<br/>

`Callback` is an interface over which you can build your own implementation, you can find its documentation [here](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/async/Callback.html).

# Serializers

When you obtain a `Response`, you can get its raw data as a byte array by calling its `getBytes()` method. Also you can parse its content according to received Content-Type header, if you previously had registered a serializer capable of handling it.

You just have to implement a `Serializer` that can handle a specific `ContentType` and register it. You can take a look at `Serializer` [interface](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/serialization/Serializers.html) and `ContentType` [definition](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/http/ContentType.html).

```java
Serializers.register(ContentType.APPLICATION_JSON, mySerializer);
```

A Jackson based JSON parser is shipped with `restclient-default` package, as well as parsers for usual contents. You can override them by registering a custom `Serializer` as shown.

# Metrics

`RestClient` can automatically collect relevant metrics to be sent to Datadog. You just have to include metrics package and you will see them appear in [Fury](https://app.datadoghq.com/dash/203757/java-meli-toolkit---rest-client-metrics?live=true) or [Melicloud](https://app.datadoghq.com/dash/203783/java-meli-toolkit---rest-client-melicloud-metrics?live=true) dashboards.
```xml
<dependency>
    <groupId>com.mercadolibre.metrics</groupId>
    <artifactId>datadog-metric-wrapper</artifactId>
    <version>0.0.1</version>
<dependency>
```

For RestClient versions <= 0.0.9 use instead
```xml
<dependency>
    <groupId>com.mercadolibre.restclient</groupId>
    <artifactId>restclient-datadog</artifactId>
    <version>0.0.1</version>
<dependency>
```

# Logging

We use [slf4j](http://www.slf4j.org) as a common interface for different logging implementations. If you want to collect Rest Client logs, first you must add the binding for your current logging implementation.

For example, if you use log4j 1.2.x, you should add
```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.21</version>
</dependency>
```

Then you should specify which level you want to apply to `restClientLogger` logger.

- ERROR. For failed requests (not 5xx, just in case of an exception is thrown) and cache failures.
- WARN. Exceptions in maintenance components (i.e. staled connections evictor, etc).
- DEBUG. Component initialization and termination.
- TRACE. Request status and time. Retries.

# Mocks

We provide a mock server for testing, which is immediately available in test environment. You can specify which response should be given for a specific request, or if the client should fail instead. <br/>
You can forget about mock and cache cleanup after each test, if you make your tests extend from `RestClientTestBase`.

For a more detailed explanation on mock matching criteria, please take a look at the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/MockResponse.html).

## Dependency
```xml
<dependency>
    <groupId>com.mercadolibre.restclient</groupId>
    <artifactId>restclient-core</artifactId>
    <version>0.0.1</version>
    <classifier>tests</classifier>
    <scope>test</scope>
</dependency>
```

## Usage Example

In this simple example, we add a response with status code 200 and a plain text response body for any GET to "http://localhost/endpoint""

All available options are specified in the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/MockResponse.Builder.html).
```java
MockResponse.builder()
    .withURL("http://localhost/endpoint")
    .withMethod(GET)
    .withStatusCode(200)
    .withResponseHeader(ContentType.HEADER_NAME, ContentType.TEXT_PLAIN.toString())
    .withResponseHeader("Cache-Control", "max-age=3600")
    .withResponseBody("ok")
    .build();
```

A simple in-memory cache is also available for testing purposes. Please refer to the [javadocs](https://mercadolibre.github.io/java-meli-toolkit/restclient/restclient-core/com/mercadolibre/restclient/cache/DummyCache.html) for full reference.
