# Completable Feign [![Build Status](https://travis-ci.org/client-side/completable-feign.svg)](https://travis-ci.org/client-side/completable-feign) [![JCenter](https://api.bintray.com/packages/client-side/clients/completable-feign/images/download.svg) ](https://bintray.com/client-side/clients/completable-feign/_latestVersion) [![License](http://img.shields.io/badge/license-Apache--2-blue.svg?style=flat) ](http://www.apache.org/licenses/LICENSE-2.0) [![codecov](https://codecov.io/gh/client-side/completable-feign/branch/master/graph/badge.svg)](https://codecov.io/gh/client-side/completable-feign)

This module utilizes the [`CompletableFuture#supplyAsync(Supplier<U> supplier, Executor executor)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) method to execute Feign HTTP requests asynchronously.  An executor may be supplied via the [`CompletableFeign.Builder`](src/engineering.clientside.completable_feign/java/engineering/clientside/feign/completable/CompletableFeign.java#L18), otherwise it defaults to the `ForkJoinPool.commonPool()`.

## Usage 

```java
interface YourApi {
  
  @RequestLine("GET /yourtype/{id}")
  CompletableFuture<YourType> getYourType(@Param("id") String id);

  @RequestLine("GET /yourtype/{id}")
  YourType getYourTypeSynchronous(@Param("id") String id);
}

YourApi api = CompletableFeign.builder()
  .target(YourApi.class, "https://example.com");

CompletableFuture<YourType> responseAFuture = api.getYourType("a");
YourType responseA = responseAFuture.join();

YourType responseB = api.getYourTypeSynchronous("b");
```

Note: Methods that do *not* return a [`CompletableFuture`]() are *not* wrapped and are executed directly using the same strategy as feign-core.
