package videoservice.it;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.node.ArrayNode;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class VideosResourceIT extends BaseIT {

  @Test
  void returnVideos() {
    Response response = client.getVideos();
    assertThat(response.getStatus(), equalTo(200));
    ArrayNode videos = response.readEntity(ArrayNode.class);
    assertThat(videos.size(), equalTo(1));
    assertThat(videos.get(0).get("id").asText(), equalTo(Fixtures.videoId));
    assertThat(videos.get(0).get("link").asText(),
        equalTo(client.getResourcesUrls().videoUrl(Fixtures.videoId).toString()));
  }
}
