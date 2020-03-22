package videoservice.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RangeTest {

  @Test
  void closed() {
    Range range = Range.closed(0L, 2L);
    assertThat(range.getMin(), equalTo(0L));
    assertThat(range.getMax(), equalTo(2L));
    assertThat(range.getLength(), equalTo(3L));
  }

  @Test
  void leftClosed() {
    Range range = Range.leftClosed(0L, 2L);
    assertThat(range.getMin(), equalTo(0L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(2L));
  }

  @Test
  void closedWithSinglePoint() {
    Range range = Range.closed(1L, 1L);
    assertThat(range.getMin(), equalTo(1L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(1L));
  }

  @Test
  void leftClosedWithSinglePoint() {
    Range range = Range.leftClosed(1L, 2L);
    assertThat(range.getMin(), equalTo(1L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(1L));
  }

  @Test
  void closedRequiresNonNegativeLowerEndpoint() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Range.closed(-1L, 2L)
    );
  }

  @Test
  void leftClosedRequiresNonNegativeLowerEndpoint() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Range.leftClosed(-1L, 2L)
    );
  }

  @Test
  void closedRequiresUpperEndpointGreaterThanOrEqualToLowerEndpoint() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Range.closed(1L, 0L)
    );
  }

  @Test
  void leftClosedRequiresUpperEndpointGreaterThanLowerEndpoint() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Range.leftClosed(1L, 1L)
    );
  }
}
