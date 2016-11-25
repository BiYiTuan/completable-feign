# Completable Feign [![Build Status](https://travis-ci.org/client-side/completable-feign.svg?branch=master)](https://travis-ci.org/client-side/completable-feign) [![JCenter](https://api.bintray.com/packages/client-side/clients/completable-feign/images/download.svg) ](https://bintray.com/client-side/clients/completable-feign/_latestVersion) [![License](http://img.shields.io/badge/license-Apache--2-blue.svg?style=flat) ](http://www.apache.org/licenses/LICENSE-2.0) [![codecov](https://codecov.io/gh/client-side/completable-feign/branch/master/graph/badge.svg)](https://codecov.io/gh/client-side/completable-feign)

>Provides asynchronous Feign method execution using core Java constructs.

## Usage

```java
interface YourApi {

  @RequestLine("GET /yourtype/{id}")
  CompletableFuture<YourType> getYourTypeAsync(@Param("id") String id);

  default CompletableFuture<YourType> getYourTypeAsync() {
    return getYourTypeAsync("defaultId");
  }

  @RequestLine("GET /yourtype/{id}")
  YourType getYourType(@Param("id") String id);
}

YourApi api = CompletableFeign.builder()
  .executor(ForkJoinPool.commonPool()) // default
  .target(YourApi.class, "https://example.com");

CompletableFuture<YourType> responseAFuture = api.getYourTypeAsync("a");
YourType responseA = responseAFuture.join();

YourType responseB = api.getYourType("b");
```

Note: Methods that do *not* return a [`CompletableFuture`]() are *not* wrapped and *are* executed directly using the same strategy as feign-core.

## Customize Future Creation

By default, a `CompletableFuture` is created for any API method that returns a `Future`.  This behavior can be customized by providing a [`FutureMethodCallFactory`](src/engineering.clientside.completable_feign/java/engineering/clientside/feign/completable/FutureMethodCallFactory.java#L12) via the [`CompletableFeign.Builder`](src/engineering.clientside.completable_feign/java/engineering/clientside/feign/completable/CompletableFeign.java#L31).

## Provide Encoders & Decoders via ServiceLoader

When building a module to handle the decoding or encoding for a specific API it may be convenient to provide that implementation without forcing the user to manually reference your implementation in source code.  

The `CompletableFeign.Builder` has convenience methods to use the Java [`ServiceLoader`](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html#the-serviceloader-class) to allow for loading `feign.codec.Encoder` and `feign.codec.Decoder` implementations configured in META-INF/services.

#### Example Encoder Configuration

In your project that provides an Encoder implementation, add a file named `resources/META-INF/services/feign.codec.Encoder` to your resources directory.  Then, add a single line declaring your implementation class, e.g., `feign.codec.Encoder$Default`.

Now, as long as your module is on the classpath as a dependency, you can load your implementation like so:
```java
YourApi api = CompletableFeign.builder()
  .encoder(Encoder.class)
  .target(YourApi.class, "https://example.com");
```

## Configure Target URLs via System Properties

```java
// Sets the system property 'com.company.yourapi.target_url' to https://example.com
FeignProperties.TARGET_URL.setProperty(com.company.YourApi.class, "https://example.com");
YourApi api = CompletableFeign.builder().target(YourApi.class);
```
