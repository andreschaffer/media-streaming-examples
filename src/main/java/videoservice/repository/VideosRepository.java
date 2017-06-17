package videoservice.repository;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VideosRepository {

  private final File videosDirectory;
  private final Mp4FileFilter mp4FileFilter;

  public VideosRepository(File videosDirectory) {
    this.videosDirectory = videosDirectory;
    this.mp4FileFilter = new Mp4FileFilter();
  }

  public List<File> list() {
    return Arrays.stream(videosDirectory.listFiles(mp4FileFilter)).collect(toList());
  }

  public Optional<File> findById(String id) {
    File file = videosDirectory.toPath().resolve(id).toFile();
    return file.exists() ? Optional.of(file) : Optional.empty();
  }
}
