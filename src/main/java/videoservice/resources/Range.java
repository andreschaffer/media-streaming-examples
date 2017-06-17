package videoservice.resources;

import static com.google.common.base.Preconditions.checkArgument;

public class Range {

  private final long min;
  private final long max;

  private Range(long min, long max) {
    checkArgument(min >= 0);
    checkArgument(max >= min);
    this.min = min;
    this.max = max;
  }

  public static Range closed(long lowerEndpoint, long upperEndpoint) {
    return new Range(lowerEndpoint, upperEndpoint);
  }

  public static Range leftClosed(long lowerEndpoint, long upperEndpoint) {
    return new Range(lowerEndpoint, upperEndpoint - 1);
  }

  public long getMin() {
    return min;
  }

  public long getMax() {
    return max;
  }

  public long getLength() {
    return max - min + 1;
  }
}
