package engineering.clientside.feign.completable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import feign.InvocationHandlerFactory;

public interface FutureMethodCallFactory {

  default Future<?> create(final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch,
      final Method method, final Object[] args, final Executor executor) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return dispatch.get(method).invoke(args);
      } catch (final RuntimeException re) {
        throw re;
      } catch (final Throwable throwable) {
        throw new CompletionException(throwable);
      }
    }, executor);
  }

  final class Default implements FutureMethodCallFactory {

  }
}
