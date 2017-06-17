package videoservice.it;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HttpDateFormat {

  private final DateFormat dateFormat;

  public HttpDateFormat() {
    dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  public Date parse(String dateTime) throws ParseException {
    return dateFormat.parse(dateTime);
  }

  public String format(long dateTimeInMillis) {
    return dateFormat.format(dateTimeInMillis);
  }
}
