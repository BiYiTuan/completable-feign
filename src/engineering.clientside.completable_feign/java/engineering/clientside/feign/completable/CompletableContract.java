package engineering.clientside.feign.completable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import feign.Contract;
import feign.MethodMetadata;

import static feign.Util.resolveLastTypeParameter;

final class CompletableContract implements Contract {

  private final Contract delegate;

  CompletableContract(final Contract delegate) {
    this.delegate = delegate;
  }

  @Override
  public List<MethodMetadata> parseAndValidatateMetadata(final Class<?> targetType) {
    final List<MethodMetadata> metadataList = delegate.parseAndValidatateMetadata(targetType);
    for (final MethodMetadata metadata : metadataList) {
      final Type type = metadata.returnType();
      if (type instanceof ParameterizedType
          && ((ParameterizedType) type).getRawType().equals(CompletableFuture.class)) {
        metadata.returnType(resolveLastTypeParameter(type, CompletableFuture.class));
      }
    }
    return metadataList;
  }
}
