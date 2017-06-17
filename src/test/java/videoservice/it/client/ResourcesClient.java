package videoservice.it.client;

import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;
import static org.glassfish.jersey.client.ClientProperties.CONNECT_TIMEOUT;
import static org.glassfish.jersey.client.ClientProperties.READ_TIMEOUT;
import static org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME;
import static org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import java.util.Arrays;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.http.Header;
import org.glassfish.jersey.logging.LoggingFeature;

public class ResourcesClient {

  private final Client client;
  private final ResourcesUrls resourcesUrls;

  public ResourcesClient(Environment environment, int port) {
    this.client = new JerseyClientBuilder(environment)
        .build(ResourcesClient.class.getName())
        .property(CONNECT_TIMEOUT, 2000)
        .property(READ_TIMEOUT, 3000)
        .register(new LoggingFeature(getLogger(DEFAULT_LOGGER_NAME), INFO, PAYLOAD_ANY, 2048));
    this.resourcesUrls = new ResourcesUrls(port);
  }

  public ResourcesUrls getResourcesUrls() {
    return resourcesUrls;
  }

  public Response getVideos() {
    return client.target(resourcesUrls.videosUrl()).request().get();
  }

  public Response getVideo(String videoId, Header... headers) {
    return getVideo(videoId, toMap(headers));
  }

  public Response getVideo(String videoId, MultivaluedMap<String, Object> headers) {
    return client.target(resourcesUrls.videoUrl(videoId)).request().headers(headers).get();
  }

  private MultivaluedMap<String, Object> toMap(Header... headers) {
    MultivaluedMap<String, Object> headersMap = new MultivaluedHashMap<>();
    Arrays.stream(headers).forEach(h -> headersMap.add(h.getName(), h.getValue()));
    return headersMap;
  }
}
