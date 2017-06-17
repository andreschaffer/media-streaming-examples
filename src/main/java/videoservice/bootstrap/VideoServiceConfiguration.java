package videoservice.bootstrap;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import java.io.File;
import javax.validation.constraints.NotNull;

public class VideoServiceConfiguration extends Configuration {

  @NotNull
  final File videosDirectory;

  public VideoServiceConfiguration(@JsonProperty("videosDirectory") String videosDirectory) throws Exception {
    checkNotNull(videosDirectory, "videosDirectory may not be null");
    this.videosDirectory = new DirectoryFinder().find(videosDirectory);
  }
}
