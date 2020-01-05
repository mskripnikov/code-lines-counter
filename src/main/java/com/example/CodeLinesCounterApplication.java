package com.example;

import java.io.File;

/**
 * Command line application for counting lines of code in java files
 *
 * @author M.Skripnikov 1/3/20
 */
public class CodeLinesCounterApplication {

  private static final String OUTPUT_TEMPLATE = "\n%s : %s";

  private static final String NO_FILE_OR_FOLDER_PATH_MESSAGE =
      "Error:  File '.java' or folder path expected as an argument";

  private static final String NOT_A_JAVA_FILE_MESSAGE =
      "Error: File is not a '.java' file or a folder";

  /**
   * Main method for calling application from the command line
   *
   * @param args - list of files or folders provided
   */
  public static void main(final String[] args) {
    if (args.length == 0) {
      System.out.println(NO_FILE_OR_FOLDER_PATH_MESSAGE);
      return;
    }
    final File file = new File(args[0]);
    if (FileCodeLinesCountingUtils.isJavaFile(file)) {
      outFormatted(file.getName(), FileCodeLinesCountingUtils.getLocCountForFile(file));
    } else if (file.isDirectory()) {
      processFolder(file);
    } else {
      outFormatted(file.getName(), NOT_A_JAVA_FILE_MESSAGE);
    }
  }

  private static void processFolder(final File file) {
    final File[] dirFiles = DirCodeLinesCountingUtils.listAcceptableFiles(file);
    for (final File dirFile : dirFiles) {
      final int locCount = dirFile.isDirectory() ? DirCodeLinesCountingUtils.getLocCount(dirFile)
          : FileCodeLinesCountingUtils.getLocCountForFile(dirFile);
      outFormatted(dirFile.getName(), locCount);
    }
  }

  private static void outFormatted(final String fileName, final int value) {
    outFormatted(fileName, String.valueOf(value));
  }

  private static void outFormatted(final String fileName, final String value) {
    System.out.println(String.format(OUTPUT_TEMPLATE, fileName, value));
  }
}
