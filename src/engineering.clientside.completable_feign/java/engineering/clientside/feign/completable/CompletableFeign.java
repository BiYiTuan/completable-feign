package engineering.clientside.feign.completable;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;

public final class CompletableFeign {

  private CompletableFeign() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Feign.Builder {

    private Contract contract = new Contract.Default();
    private Executor executor = ForkJoinPool.commonPool();

    public Builder executor(final Executor executor) {
      this.executor = executor;
      return this;
    }

    @Override
    public Builder invocationHandlerFactory(
        final InvocationHandlerFactory invocationHandlerFactory) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Builder contract(final Contract contract) {
      this.contract = contract;
      return this;
    }

    @Override
    public Feign build() {
      super.invocationHandlerFactory((target, dispatch) ->
          new CompletableInvocationHandler(target, dispatch, executor));
      super.contract(new CompletableContract(contract));
      return super.build();
    }
  }
}
