package videoservice.resources;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RangeTest {

  @Test
  public void closed() throws Exception {
    Range range = Range.closed(0L, 2L);
    assertThat(range.getMin(), equalTo(0L));
    assertThat(range.getMax(), equalTo(2L));
    assertThat(range.getLength(), equalTo(3L));
  }

  @Test
  public void leftClosed() throws Exception {
    Range range = Range.leftClosed(0L, 2L);
    assertThat(range.getMin(), equalTo(0L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(2L));
  }

  @Test
  public void closedWithSinglePoint() throws Exception {
    Range range = Range.closed(1L, 1L);
    assertThat(range.getMin(), equalTo(1L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(1L));
  }

  @Test
  public void leftClosedWithSinglePoint() throws Exception {
    Range range = Range.leftClosed(1L, 2L);
    assertThat(range.getMin(), equalTo(1L));
    assertThat(range.getMax(), equalTo(1L));
    assertThat(range.getLength(), equalTo(1L));
  }

  @Test(expected = IllegalArgumentException.class)
  public void closedRequiresNonNegativeLowerEndpoint() throws Exception {
    Range.closed(-1L, 2L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void leftClosedRequiresNonNegativeLowerEndpoint() throws Exception {
    Range.leftClosed(-1L, 2L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void closedRequiresUpperEndpointGreaterThanOrEqualToLowerEndpoint() throws Exception {
    Range.closed(1L, 0L);
  }

  @Test(expected = IllegalArgumentException.class)
  public void leftClosedRequiresUpperEndpointGreaterThanLowerEndpoint() throws Exception {
    Range.leftClosed(1L, 1L);
  }
}
