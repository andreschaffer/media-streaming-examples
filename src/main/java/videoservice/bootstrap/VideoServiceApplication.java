package videoservice.bootstrap;

import static org.glassfish.jersey.CommonProperties.OUTBOUND_CONTENT_LENGTH_BUFFER;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import videoservice.repository.VideosRepository;
import videoservice.resources.VideoResource;
import videoservice.resources.VideosResource;

public class VideoServiceApplication extends Application<VideoServiceConfiguration> {

  public static void main(String[] args) throws Exception {
    new VideoServiceApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<VideoServiceConfiguration> bootstrap) {
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
        bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(true)));
  }

  @Override
  public void run(VideoServiceConfiguration configuration, Environment environment) throws Exception {
    registerHypermediaSupport(environment);
    registerResources(configuration, environment);
    configureOutboundContentLengthBuffer(environment);
  }

  private void configureOutboundContentLengthBuffer(final Environment environment) {
    environment.jersey().getResourceConfig().property(OUTBOUND_CONTENT_LENGTH_BUFFER, 5120 * 1024);
  }

  private void registerHypermediaSupport(Environment environment) {
    environment.jersey().getResourceConfig().register(DeclarativeLinkingFeature.class);
  }

  private void registerResources(VideoServiceConfiguration configuration,
                                 Environment environment) {
    VideosRepository videosRepository = new VideosRepository(configuration.videosDirectory);
    environment.jersey().register(new VideosResource(videosRepository));
    environment.jersey().register(new VideoResource(videosRepository));
  }
}
