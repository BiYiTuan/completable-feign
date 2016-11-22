# Completable Feign [![Build Status](https://travis-ci.org/client-side/completable-feign.svg?branch=master)](https://travis-ci.org/client-side/completable-feign) [![JCenter](https://api.bintray.com/packages/client-side/clients/completable-feign/images/download.svg) ](https://bintray.com/client-side/clients/completable-feign/_latestVersion) [![License](http://img.shields.io/badge/license-Apache--2-blue.svg?style=flat) ](http://www.apache.org/licenses/LICENSE-2.0) [![codecov](https://codecov.io/gh/client-side/completable-feign/branch/master/graph/badge.svg)](https://codecov.io/gh/client-side/completable-feign)

>Provides asynchronous Feign method execution using core Java constructs.

## Usage 

```java
interface YourApi {
  
  @RequestLine("GET /yourtype/{id}")
  CompletableFuture<YourType> getYourType(@Param("id") String id);

  @RequestLine("GET /yourtype/{id}")
  YourType getYourTypeSynchronous(@Param("id") String id);
}

YourApi api = CompletableFeign.builder()
  .executor(ForkJoinPool.commonPool()) // default
  .target(YourApi.class, "https://example.com");

CompletableFuture<YourType> responseAFuture = api.getYourType("a");
YourType responseA = responseAFuture.join();

YourType responseB = api.getYourTypeSynchronous("b");
```

Note: Methods that do *not* return a [`CompletableFuture`]() are *not* wrapped and *are* executed directly using the same strategy as feign-core.

## Customize Future Creation

By default, a `CompletableFuture` is created for any API method that returns a `Future`.  This behavior can customized by providing a [`FutureMethodCallFactory`](src/engineering.clientside.completable_feign/java/engineering/clientside/feign/completable/FutureMethodCallFactory.java#L12) via the [`CompletableFeign.Builder`](src/engineering.clientside.completable_feign/java/engineering/clientside/feign/completable/CompletableFeign.java#L31).
