# Completable Feign

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

Note: Methods that do *not* return a [`CompletableFuture`]() are not wrapped and are executed directly using the same strategy as feign-core.
