package videoservice.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.node.ArrayNode;
import javax.ws.rs.core.Response;
import org.junit.Test;

public class VideosResourceIT extends BaseIT {

  @Test
  public void returnVideos() throws Exception {
    Response response = client.getVideos();
    assertThat(response.getStatus(), equalTo(200));
    ArrayNode videos = response.readEntity(ArrayNode.class);
    assertThat(videos.size(), equalTo(1));
    assertThat(videos.get(0).get("id").asText(), equalTo(Fixtures.videoId));
    assertThat(videos.get(0).get("link").asText(),
        equalTo(client.getResourcesUrls().videoUrl(Fixtures.videoId).toString()));
  }
}
