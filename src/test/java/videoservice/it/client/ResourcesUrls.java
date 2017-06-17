package videoservice.it.client;

import static javax.ws.rs.core.UriBuilder.fromUri;

import java.net.URI;

public class ResourcesUrls {

  private int port;

  public ResourcesUrls(final int port) {
    this.port = port;
  }

  public URI videosUrl() {
    return fromUri(baseUrl()).path("videos").build();
  }

  public URI videoUrl(final String videoId) {
    return fromUri(baseUrl()).path("videos").path("{id}").build(videoId);
  }

  private URI baseUrl() {
    return fromUri("http://localhost").port(port).build();
  }
}
