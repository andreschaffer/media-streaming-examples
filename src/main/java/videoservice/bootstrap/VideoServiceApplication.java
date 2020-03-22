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
  public void run(VideoServiceConfiguration config, Environment env) {
    registerHypermediaSupport(env);
    registerResources(config, env);
    configureOutboundContentLengthBuffer(env);
  }

  private void configureOutboundContentLengthBuffer(Environment env) {
    env.jersey().getResourceConfig().property(OUTBOUND_CONTENT_LENGTH_BUFFER, 5120 * 1024);
  }

  private void registerHypermediaSupport(Environment env) {
    env.jersey().getResourceConfig().register(DeclarativeLinkingFeature.class);
  }

  private void registerResources(VideoServiceConfiguration config, Environment env) {
    VideosRepository videosRepository = new VideosRepository(config.videosDirectory);
    env.jersey().register(new VideosResource(videosRepository));
    env.jersey().register(new VideoResource(videosRepository));
  }
}
