package engineering.clientside.feign.completable;

import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import feign.Contract;
import feign.InvocationHandlerFactory;
import feign.RequestLine;
import feign.Response;
import feign.Util;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class CompletableFeignBuilderTest {

  @Rule
  public final MockWebServer server = new MockWebServer();

  @Test
  public void testDefaults() throws Exception {
    server.enqueue(new MockResponse().setBody("response data"));
    String url = "http://localhost:" + server.getPort();
    TestInterface api = CompletableFeign.builder().target(TestInterface.class, url);
    Response response = api.codecPost("request data");
    assertEquals("response data", Util.toString(response.body().asReader()));
    assertEquals("request data", server.takeRequest().getBody().readString(UTF_8));
  }

  @Test
  public void testProvideContract() throws Exception {
    server.enqueue(new MockResponse().setBody("response data"));
    String url = "http://localhost:" + server.getPort();
    TestInterface api = CompletableFeign.builder().contract(new Contract.Default())
        .target(TestInterface.class, url);
    Response response = api.codecPost("request data");
    assertEquals("response data", Util.toString(response.body().asReader()));
    assertEquals("request data", server.takeRequest().getBody().readString(UTF_8));
    server.enqueue(new MockResponse().setBody("response data"));
    CompletableFuture<Response> responseFuture = api.get();
    assertEquals("response data", Util.toString(responseFuture.join().body().asReader()));
  }

  @Test
  public void testProvideExecutor() throws Exception {
    final ExecutorService exec = Executors.newSingleThreadExecutor();
    server.enqueue(new MockResponse().setBody("response data"));
    String url = "http://localhost:" + server.getPort();
    TestInterface api = CompletableFeign.builder().executor(exec).target(TestInterface.class, url);
    CompletableFuture<Response> response = api.get();
    assertEquals("response data", Util.toString(response.join().body().asReader()));
    exec.shutdown();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testProvideInvocationHandlerFactory() throws Exception {
    final InvocationHandlerFactory delegate = new InvocationHandlerFactory.Default();
    final InvocationHandlerFactory factory =
        (target, dispatch) -> delegate.create(target, dispatch);
    CompletableFeign.builder().invocationHandlerFactory(factory);
  }

  interface TestInterface {

    @RequestLine("GET")
    CompletableFuture<Response> get();

    @RequestLine("POST /")
    Response codecPost(String data);
  }
}
