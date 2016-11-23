package engineering.clientside.feign.completable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import feign.InvocationHandlerFactory.MethodHandler;
import feign.Target;

final class CompletableInvocationHandler implements InvocationHandler {

  private final Target<?> target;
  private final Map<Method, MethodHandler> dispatch;
  private final Runnable beforeHook;
  private final FutureMethodCallFactory futureFactory;
  private final Executor executor;

  CompletableInvocationHandler(final Target<?> target, final Map<Method, MethodHandler> dispatch,
      final Runnable beforeHook, final FutureMethodCallFactory futureFactory,
      final Executor executor) {
    this.target = target;
    this.dispatch = dispatch;
    this.beforeHook = beforeHook;
    this.futureFactory = futureFactory;
    this.executor = executor;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    if (Future.class.isAssignableFrom(method.getReturnType())) {
      return futureFactory.create(dispatch, method, args, executor);
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
        final MethodHandler methodHandler = dispatch.get(method);
        beforeHook.run();
        return methodHandler.invoke(args);
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
