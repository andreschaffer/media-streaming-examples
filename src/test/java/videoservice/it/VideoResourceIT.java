package videoservice.it;

import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.io.ByteSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import javax.ws.rs.core.Response;
import org.apache.http.message.BasicHeader;
import org.junit.Test;
import videoservice.resources.Range;

public class VideoResourceIT extends BaseIT {

  private static final HttpDateFormat httpDateFormat = new HttpDateFormat();
  private static final long defaultPartialLength = 1024L * 1024L;

  @Test
  public void returnVideoNotFound() throws Exception {
    String wrongVideoId = "wrong-video.mp4";
    Response response = client.getVideo(wrongVideoId);
    response.close();
    assertThat(response.getStatus(), equalTo(404));
  }

  @Test
  public void returnFullVideoWhenRangeIsNotPresent() throws Exception {
    byte[] expectedVideo = readVideoFileBytes(Fixtures.videoId);

    Response response = client.getVideo(Fixtures.videoId);

    assertThat(response.getStatus(), equalTo(200));
    byte[] actualVideo = response.readEntity(byte[].class);
    assertTrue(Arrays.equals(actualVideo, expectedVideo));
    assertThat(response.getHeaderString("Accept-Ranges"), equalTo("bytes"));
    httpDateFormat.parse(response.getHeaderString("Last-Modified"));
  }

  @Test
  public void returnVideoPartWithDefaultLengthForRangeWithoutMaxValue() throws Exception {
    byte[] expectedVideo = readVideoFileBytes(Fixtures.videoId);
    Range expectedRange = Range.leftClosed(0L, defaultPartialLength);
    byte[] expectedVideoPart = sliceFile(expectedVideo, expectedRange);
    
    Response response = client.getVideo(Fixtures.videoId, new BasicHeader("Range", "bytes=0-"));
    
    assertThat(response.getStatus(), equalTo(206));
    byte[] actualVideoPart = response.readEntity(byte[].class);
    assertTrue(Arrays.equals(actualVideoPart, expectedVideoPart));
    verifyPartialContentHeaders(response, expectedRange, expectedVideo.length);
    httpDateFormat.parse(response.getHeaderString("Last-Modified"));
  }

  @Test
  public void returnVideoPartAccordingToRange() throws Exception {
    byte[] expectedVideo = readVideoFileBytes(Fixtures.videoId);
    long min = 1024L * 1024L;
    long max = 1024L * 2048L;
    Range expectedRange = Range.closed(min, max);
    byte[] expectedVideoPart = sliceFile(expectedVideo, expectedRange);

    Response response = client.getVideo(Fixtures.videoId, new BasicHeader("Range", format("bytes=%d-%d", min, max)));

    assertThat(response.getStatus(), equalTo(206));
    byte[] actualVideoPart = response.readEntity(byte[].class);
    assertTrue(Arrays.equals(actualVideoPart, expectedVideoPart));
    verifyPartialContentHeaders(response, expectedRange, expectedVideo.length);
    httpDateFormat.parse(response.getHeaderString("Last-Modified"));
  }

  @Test
  public void returnVideoPartRespectingEndOfFileForRangeWithoutMaxValue() throws Exception {
    byte[] expectedVideo = readVideoFileBytes(Fixtures.videoId);
    long max = expectedVideo.length;
    long min = max - 1000L;
    Range expectedRange = Range.leftClosed(min, max);
    byte[] expectedVideoPart = sliceFile(expectedVideo, expectedRange);

    Response response = client.getVideo(Fixtures.videoId, new BasicHeader("Range", format("bytes=%d-", min)));

    assertThat(response.getStatus(), equalTo(206));
    byte[] actualVideoPart = response.readEntity(byte[].class);
    assertTrue(Arrays.equals(actualVideoPart, expectedVideoPart));
    verifyPartialContentHeaders(response, expectedRange, expectedVideo.length);
    httpDateFormat.parse(response.getHeaderString("Last-Modified"));
  }

  @Test
  public void returnNotModifiedWhenIfModifiedSincePreconditionIsFalse() throws Exception {
    File video = videoFilePath(Fixtures.videoId).toFile();
    String ifModifiedSince = httpDateFormat.format(video.lastModified());
    long min = 1024L * 1024L;
    long max = 1024L * 2048L;

    Response response = client.getVideo(Fixtures.videoId,
        new BasicHeader("Range", format("bytes=%d-%d", min, max)),
        new BasicHeader("If-Modified-Since", ifModifiedSince));

    response.close();
    assertThat(response.getStatus(), equalTo(304));
  }

  @Test
  public void returnVideoPartWhenIfModifiedSincePreconditionIsTrue() throws Exception {
    File video = videoFilePath(Fixtures.videoId).toFile();
    String ifModifiedSince = httpDateFormat.format(video.lastModified() - 1);
    long min = 1024L * 1024L;
    long max = 1024L * 2048L;
    Range expectedRange = Range.closed(min, max);

    Response response = client.getVideo(Fixtures.videoId,
        new BasicHeader("Range", format("bytes=%d-%d", min, max)),
        new BasicHeader("If-Modified-Since", ifModifiedSince));

    response.close();
    assertThat(response.getStatus(), equalTo(206));
    verifyPartialContentHeaders(response, expectedRange, video.length());
    assertThat(httpDateFormat.parse(response.getHeaderString("Last-Modified")).getTime(), equalTo(video.lastModified()));
  }

  @Test
  public void returnVideoPartWhenIfRangePreconditionIsTrue() throws Exception {
    File video = videoFilePath(Fixtures.videoId).toFile();
    String ifRangeLastModified = httpDateFormat.format(video.lastModified());
    long min = 1024L * 1024L;
    long max = 1024L * 2048L;
    Range expectedRange = Range.closed(min, max);

    Response response = client.getVideo(Fixtures.videoId,
        new BasicHeader("Range", format("bytes=%d-%d", min, max)),
        new BasicHeader("If-Range", ifRangeLastModified));

    response.close();
    assertThat(response.getStatus(), equalTo(206));
    verifyPartialContentHeaders(response, expectedRange, video.length());
    assertThat(httpDateFormat.parse(response.getHeaderString("Last-Modified")).getTime(),
        equalTo(video.lastModified()));
  }

  @Test
  public void returnFullVideoWhenIfRangePreconditionIsFalse() throws Exception {
    File video = videoFilePath(Fixtures.videoId).toFile();
    String ifRangeLastModified = httpDateFormat.format(video.lastModified() - 1);
    long min = 1024L * 1024L;
    long max = 1024L * 2048L;

    Response response = client.getVideo(Fixtures.videoId,
        new BasicHeader("Range", format("bytes=%d-%d", min, max)),
        new BasicHeader("If-Range", ifRangeLastModified));

    response.close();
    assertThat(response.getStatus(), equalTo(200));
    assertThat(response.getHeaderString("Accept-Ranges"), equalTo("bytes"));
    assertThat(httpDateFormat.parse(response.getHeaderString("Last-Modified")).getTime(),
        equalTo(video.lastModified()));
  }

  private byte[] readVideoFileBytes(String id) throws IOException {
    try {
      return readAllBytes(videoFilePath(id));
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
  }

  private Path videoFilePath(String id) throws URISyntaxException {
    return Paths.get(getResource("videos/" + id).toURI());
  }

  private byte[] sliceFile(byte[] video, Range range) throws IOException {
    return ByteSource.wrap(video).slice(range.getMin(), range.getLength()).read();
  }

  private void verifyPartialContentHeaders(Response response, Range expectedRange,
                                           long expectedFullContentLength) throws ParseException {

    assertThat(response.getHeaderString("Accept-Ranges"), equalTo("bytes"));

    assertThat(response.getHeaderString("Content-Range"),
        equalTo(toContentRangeHeader(expectedRange, expectedFullContentLength)));
  }

  private String toContentRangeHeader(Range range, long fullContentLength) {
    return format("bytes %d-%d/%d", range.getMin(), range.getMax(), fullContentLength);
  }
}
