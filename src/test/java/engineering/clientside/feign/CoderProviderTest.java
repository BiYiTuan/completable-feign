package engineering.clientside.feign;

import org.junit.Test;

import feign.codec.Decoder;
import feign.codec.Encoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CoderProviderTest {

  @Test
  public void testGetCoder() {
    final Coder coder = CoderProvider.getCoder(Coder.class);
    assertEquals(TestCoder.class, coder.getClass());
  }

  @Test
  public void testGetEncoder() {
    final Encoder encoder = CoderProvider.getEncoder(Encoder.class);
    assertEquals(Encoder.Default.class, encoder.getClass());
  }

  @Test
  public void testGetDecoder() {
    final Decoder decoder = CoderProvider.getDecoder(Decoder.class);
    assertEquals(Decoder.Default.class, decoder.getClass());
  }

  @Test
  public void testNotProvidedCoder() {
    NotProvidedCoder coder = CoderProvider.getCoder(NotProvidedCoder.class);
    assertNull(coder);
    coder = CoderProvider.getEncoder(NotProvidedCoder.class);
    assertNull(coder);
    coder = CoderProvider.getDecoder(NotProvidedCoder.class);
    assertNull(coder);
  }

  public interface NotProvidedCoder extends Encoder, Decoder {

  }
}
