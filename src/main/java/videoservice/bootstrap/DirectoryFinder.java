package videoservice.bootstrap;

import static java.lang.String.format;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

class DirectoryFinder {

  File find(String path) throws Exception {
    return firstNonNull(
        getDirectoryFromClassLocation(path).orElse(null),
        getDirectoryFromClassloader(path).orElse(null),
        format("Could not find directory '%s' or it is not a directory", path)
    );
  }

  private File firstNonNull(File file1, File file2, String errorMessage) {
    if (file1 != null) return file1;
    if (file2 != null) return file2;
    throw new IllegalArgumentException(errorMessage);
  }

  private Optional<File> getDirectoryFromClassLocation(String path) throws URISyntaxException {
    File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().resolve(path));
    return file.isDirectory() ? Optional.of(file) : Optional.empty();
  }

  private Optional<File> getDirectoryFromClassloader(String path) throws URISyntaxException {
    URL url = Thread.currentThread().getContextClassLoader().getResource(path);
    if (url == null) {
      return Optional.empty();
    }
    File file = new File(url.toURI());
    return file.isDirectory() ? Optional.of(file) : Optional.empty();
  }
}
