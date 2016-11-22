package engineering.clientside.feign.completable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;

final class CompletableInvocationHandler implements InvocationHandler {

  private final Target<?> target;
  private final Map<Method, MethodHandler> dispatch;
  private final Executor executor;

  CompletableInvocationHandler(final Target<?> target,
      final Map<Method, MethodHandler> dispatch, final Executor executor) {
    this.target = target;
    this.dispatch = dispatch;
    this.executor = executor;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    if (method.getReturnType().equals(CompletableFuture.class)) {
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
    switch (method.getName()) {
      case "equals":
        if (args.length == 0 || args[0] == null) {
          return false;
        }
        try {
          final InvocationHandler other = Proxy.getInvocationHandler(args[0]);
          if (other.getClass().equals(CompletableInvocationHandler.class)) {
            final CompletableInvocationHandler that = (CompletableInvocationHandler) other;
            return target.equals(that.target);
          }
        } catch (final IllegalArgumentException e) {
          //
        }
        return false;
      case "hashCode":
        return hashCode();
      case "toString":
        return toString();
      default:
        return dispatch.get(method).invoke(args);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final CompletableInvocationHandler that = (CompletableInvocationHandler) obj;
    return target.equals(that.target);
  }

  @Override
  public int hashCode() {
    return target.hashCode();
  }

  @Override
  public String toString() {
    return target.toString();
  }
}
