package videoservice.it;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import videoservice.bootstrap.VideoServiceApplication;
import videoservice.bootstrap.VideoServiceConfiguration;
import videoservice.it.client.ResourcesClient;

public class BaseIT {

  @ClassRule
  public static final DropwizardAppRule<VideoServiceConfiguration> SERVICE =
      new DropwizardAppRule<>(VideoServiceApplication.class, resourceFilePath("integration.yml"));

  protected static ResourcesClient client;

  @BeforeClass
  public static void setUp() throws Exception {
    client = new ResourcesClient(SERVICE.getEnvironment(), SERVICE.getLocalPort());
  }
}
