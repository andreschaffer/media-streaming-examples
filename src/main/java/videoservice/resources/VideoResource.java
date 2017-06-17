package videoservice.resources;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.PARTIAL_CONTENT;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import videoservice.repository.VideosRepository;

@Produces("video/mp4")
@Path("/videos/{id}")
public class VideoResource {

  private static final long defaultPartialLength = 1024L * 1024L;
  private static final DateTimeFormatter httpDateFormat = RFC_1123_DATE_TIME.withZone(UTC);

  private final VideosRepository videosRepository;

  public VideoResource(VideosRepository videosRepository) {
    this.videosRepository = videosRepository;
  }

  @GET
  public Response get(@PathParam("id") String id, @Context Request request,
                      @HeaderParam("Range") Optional<String> range,
                      @HeaderParam("If-Range") Optional<String> ifRange) {

    Optional<File> video = videosRepository.findById(id);
    if (!video.isPresent()) return Response.status(NOT_FOUND).build();

    Optional<Response> notModifiedResponse = evaluateRequestPreconditions(video.get(), request);
    if (notModifiedResponse.isPresent()) return notModifiedResponse.get();

    if (range.isPresent() && (!ifRange.isPresent() || ifRangePreconditionMatches(video.get(), ifRange.get())))
      return videoPartResponse(video.get(), range.get());

    return fullVideoResponse(video.get());
  }

  private Optional<Response> evaluateRequestPreconditions(File file, Request request) {
    ResponseBuilder notModifiedResponseBuilder = request.evaluatePreconditions(new Date(file.lastModified()));
    return Optional.ofNullable(notModifiedResponseBuilder).map(ResponseBuilder::build);
  }

  private boolean ifRangePreconditionMatches(File file, String ifRangeHeader) {
    ZonedDateTime ifRangeLastModified = ZonedDateTime.parse(ifRangeHeader, httpDateFormat);
    ZonedDateTime fileLastModified = ZonedDateTime.ofInstant(new Date(file.lastModified()).toInstant(), UTC);
    return ifRangeLastModified.equals(fileLastModified);
  }

  private Response fullVideoResponse(File file) {
    StreamingOutput stream = createStream(Files.asByteSource(file));
    return Response.ok()
        .entity(stream)
        .header("Accept-Ranges", "bytes")
        .header("Content-Length", file.length())
        .header("Last-Modified", new Date(file.lastModified()))
        .build();
  }

  private Response videoPartResponse(File file, String rangeHeader) {
    Range range = calculateRange(file, rangeHeader);
    ByteSource filePart = sliceFile(file, range);
    StreamingOutput stream = createStream(filePart);
    return Response.status(PARTIAL_CONTENT)
        .entity(stream)
        .header("Accept-Ranges", "bytes")
        .header("Content-Range", toContentRange(range, file.length()))
        .header("Content-Length",range.getLength())
        .header("Last-Modified", new Date(file.lastModified()))
        .build();
  }

  private StreamingOutput createStream(final ByteSource filePart) {
    return outputStream -> {
      try (InputStream inputStream = filePart.openBufferedStream()) {
        ByteStreams.copy(inputStream, outputStream);
      }
    };
  }

  private Range calculateRange(File file, String rangeHeader) {
    String[] ranges = rangeHeader.split("=")[1].split("-");
    long min = Long.parseLong(ranges[0]);
    return ranges.length == 2 ?
           Range.closed(min, Long.parseLong(ranges[1])) :
           Range.leftClosed(min, Long.min(file.length(), min + defaultPartialLength));
  }

  private ByteSource sliceFile(File file, Range range) {
    return Files.asByteSource(file).slice(range.getMin(), range.getLength());
  }

  private String toContentRange(Range range, long fullContentLength) {
    return String.format("bytes %d-%d/%d", range.getMin(), range.getMax(), fullContentLength);
  }
}
