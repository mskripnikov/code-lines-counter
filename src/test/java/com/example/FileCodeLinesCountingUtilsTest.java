package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class FileCodeLinesCountingUtilsTest {

  @Test
  public void getLOCCountForNull() {
    assertNull(FileCodeLinesCountingUtils.getLocCountForFile((File) null));
  }

  @Test
  public void getLOCCountForDir() {
    assertNull(FileCodeLinesCountingUtils.getLocCountForFile(new File(System.getProperty("java.io.tmpdir"))));
  }

  @Test
  public void getLOCCountForNotExistingFile() {
    File file = null;
    //make sure file not exists
    while (file == null || file.exists()) {
      file = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
    }
    assertNull(FileCodeLinesCountingUtils.getLocCountForFile(file));
  }

  @Test
  public void evaluateLineCommentOpened() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("/*", context);
    assertTrue(context.isInsideMultilineComment());
    assertFalse(context.isLineHasCode());
  }

  @Test
  public void evaluateLineCommentOpenedInQuotes() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("(\"Hello/*\")", context);
    assertFalse(context.isInsideMultilineComment());
    assertTrue(context.isLineHasCode());
  }

  @Test
  public void evaluateLineCommentLine() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("//asd", context);
    assertFalse(context.isInsideMultilineComment());
    assertFalse(context.isLineHasCode());
  }

  @Test
  public void evaluateLineCommentNotOpened() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("//*****", context);
    assertFalse(context.isInsideMultilineComment());
    assertFalse(context.isLineHasCode());
  }

  @Test
  public void evaluateLineCommentClosed() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    context.setInsideMultilineComment(true);
    FileCodeLinesCountingUtils.evaluateLine(" *****/", context);
    assertFalse(context.isInsideMultilineComment());
    assertFalse(context.isLineHasCode());
  }

  @Test
  public void evaluateLineCommentClosedInLineComment() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    context.setInsideMultilineComment(true);
    FileCodeLinesCountingUtils.evaluateLine(" //*****/", context);
    assertFalse(context.isInsideMultilineComment());
    assertFalse(context.isLineHasCode());
  }

  @Test
  public void evaluateLineForCode() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("//*/int a=0;/**///", context);
    assertFalse(context.isInsideMultilineComment());
    assertTrue(context.isLineHasCode());
  }

  @Test
  public void evaluateLineForBraces() {
    final FileCodeLinesCountingUtils.LineEvaluationContext context = new FileCodeLinesCountingUtils.LineEvaluationContext();
    FileCodeLinesCountingUtils.evaluateLine("    }", context);
    assertFalse(context.isInsideMultilineComment());
    assertTrue(context.isLineHasCode());
  }

  @Test
  public void testLOCCountForMultiline() throws IOException {
    final String text = "\t /*//\n" +
        "    //*/int a=0;/**///\n" +
        "\t//*";
    assertEquals(1, FileCodeLinesCountingUtils.getLocCountForStream(new StringReader(text)));
  }

  @Test
  public void testLOCCountForStream() throws IOException {
    try (final InputStreamReader resourceAsStream = new InputStreamReader(
        FileCodeLinesCountingUtils.class.getResourceAsStream("/test-file.java"))) {
      assertEquals(6, FileCodeLinesCountingUtils.getLocCountForStream(resourceAsStream));
    }
  }

  @Test
  public void testLOCCountForFile() throws IOException {
    File tempFile = null;
    try (final InputStreamReader resourceAsStream = new InputStreamReader(
        FileCodeLinesCountingUtils.class.getResourceAsStream("/test-file.java"))) {
      tempFile = File.createTempFile("LOC-Counter-Test", ".java");
      tempFile.deleteOnExit();
      try (final FileWriter fileWriter = new FileWriter(tempFile)) {
        IOUtils.copy(resourceAsStream, fileWriter);
      }

      final LocCountingItem item = FileCodeLinesCountingUtils.getLocCountForFile(tempFile);
      assertNotNull(item);
      assertEquals(6, item.getLocCount());
      assertEquals("\n" + tempFile.getName() + " : 6", item.toString());

    } finally {
      if (tempFile != null) {
        Files.delete(tempFile.toPath());
      }
    }
  }
}
