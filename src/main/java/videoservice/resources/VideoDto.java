package videoservice.resources;

import static org.glassfish.jersey.linking.InjectLink.Style.ABSOLUTE;

import java.net.URI;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;

public class VideoDto {

  private String id;

  @InjectLink(style = ABSOLUTE, resource = VideoResource.class, bindings = {@Binding("${instance.id}")})
  private URI link;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public URI getLink() {
    return link;
  }

  public void setLink(final URI link) {
    this.link = link;
  }
}
