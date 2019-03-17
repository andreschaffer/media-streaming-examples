package videoservice.it;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Following RFC 1123, datetime is truncated to seconds.
 * */
public class HttpDateFormat {

  private final DateFormat dateFormat;

  public HttpDateFormat() {
    dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public void parse(String dateTime) throws ParseException {
    dateFormat.parse(dateTime);
  }

  public String format(long dateTimeInMillis) {
    return dateFormat.format(dateTimeInMillis);
  }
}
