package com.example;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class FileCodeLinesCounterTest {

    @Test
    public void evaluateLineCommentOpened() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        FileCodeLinesCounter.evaluateLine("/*****", context);
        assertTrue(context.isInsideMultilineComment());
        assertFalse(context.isLineHasCode());
    }

    @Test
    public void evaluateLineCommentLine() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        FileCodeLinesCounter.evaluateLine("//asd", context);
        assertFalse(context.isInsideMultilineComment());
        assertFalse(context.isLineHasCode());
    }

    @Test
    public void evaluateLineCommentNotOpened() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        FileCodeLinesCounter.evaluateLine("//*****", context);
        assertFalse(context.isInsideMultilineComment());
        assertFalse(context.isLineHasCode());
    }

    @Test
    public void evaluateLineCommentClosed() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        context.setInsideMultilineComment(true);
        FileCodeLinesCounter.evaluateLine(" *****/", context);
        assertFalse(context.isInsideMultilineComment());
        assertFalse(context.isLineHasCode());
    }

    @Test
    public void evaluateLineCommentClosedInLineComment() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        context.setInsideMultilineComment(true);
        FileCodeLinesCounter.evaluateLine(" //*****/", context);
        assertFalse(context.isInsideMultilineComment());
        assertFalse(context.isLineHasCode());
    }

    @Test
    public void evaluateLineForCode() {
        FileCodeLinesCounter.LineEvaluationContext context = new FileCodeLinesCounter.LineEvaluationContext();
        FileCodeLinesCounter.evaluateLine("//*/int a=0;/**///", context);
        assertFalse(context.isInsideMultilineComment());
        assertTrue(context.isLineHasCode());
    }

    @Test
    public void testLOCCountForMultiline() throws IOException {
        final String text = "\t /*//\n" +
                "    //*/int a=0;/**///\n" +
                "\t//*";
        assertEquals(1, FileCodeLinesCounter.getLOCCount(new StringReader(text)));
    }

    @Test
    public void testLOCCountForFile() throws IOException {
        try (InputStreamReader resourceAsStream = new InputStreamReader(FileCodeLinesCounter.class.getResourceAsStream("/test.file"))) {
            assertEquals(6, FileCodeLinesCounter.getLOCCount(resourceAsStream));
        }
    }
}
