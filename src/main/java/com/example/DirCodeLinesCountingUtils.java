package com.example;

import java.io.File;

public final class DirCodeLinesCountingUtils {

  private static final File[] EMPTY_ARRAY = new File[0];

  private DirCodeLinesCountingUtils() {
    throw new IllegalAccessError("Do not call");
  }

  /**
   * Counts lines of code in the java file
   *
   * @param folder - particular folder
   * @return - count of lines assumed to be a code (not comments, not blank etc)
   * @throws IllegalArgumentException if file is null, not a file or not exists or not a java file
   */
  public static int getLocCount(final File folder) {
    if (folder == null || !folder.exists()) {
      return 0;
    }
    final File[] files = listAcceptableFiles(folder);
    int count = 0;
    for (final File file : files) {
      if (file.isDirectory()) {
        count += getLocCount(file);
      } else {
        count += FileCodeLinesCountingUtils.getLocCountForFile(file);
      }
    }
    return count;
  }

  protected static File[] listAcceptableFiles(final File folder) {
    final File[] files = folder
        .listFiles(f -> f.isDirectory() || FileCodeLinesCountingUtils.isJavaFile(f));
    return files == null ? EMPTY_ARRAY : files;
  }
}
