package videoservice.resources;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.File;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import videoservice.repository.VideosRepository;

@Produces(APPLICATION_JSON)
@Path("/videos")
public class VideosResource {

  private final VideosRepository videosRepository;

  public VideosResource(VideosRepository videosRepository) {
    this.videosRepository = videosRepository;
  }

  @GET
  public Response get() {
    List<VideoDto> videos = videosRepository.list().stream().map(this::toDto).collect(toList());
    return Response.ok(videos).build();
  }

  private VideoDto toDto(File videoFile) {
    VideoDto dto = new VideoDto();
    dto.setId(videoFile.getName());
    return dto;
  }
}
