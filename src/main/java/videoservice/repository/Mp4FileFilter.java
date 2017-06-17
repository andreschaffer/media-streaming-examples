package videoservice.repository;

import java.io.File;
import java.io.FilenameFilter;

class Mp4FileFilter implements FilenameFilter {

  @Override
  public boolean accept(File dir, String name) {
    return name.toLowerCase().endsWith(".mp4");
  }
}
