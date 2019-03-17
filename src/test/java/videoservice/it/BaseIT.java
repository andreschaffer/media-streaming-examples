package videoservice.it;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import videoservice.bootstrap.VideoServiceApplication;
import videoservice.bootstrap.VideoServiceConfiguration;
import videoservice.it.client.ResourcesClient;

@ExtendWith(DropwizardExtensionsSupport.class)
class BaseIT {

  protected static final DropwizardAppExtension<VideoServiceConfiguration> SERVICE =
      new DropwizardAppExtension<>(VideoServiceApplication.class, resourceFilePath("integration.yml"));

  protected static ResourcesClient client;

  @BeforeAll
  static void setUp() {
    client = new ResourcesClient(SERVICE.getEnvironment(), SERVICE.getLocalPort());
  }
}
