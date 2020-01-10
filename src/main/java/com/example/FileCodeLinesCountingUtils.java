package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Class for counting lines of code in a particular file.
 *
 * @author M.Skripnikov 1/4/20
 */

public final class FileCodeLinesCountingUtils {

	private static final String LINE_COMMENT_TOKEN = "//";

	private static final String MULTILINE_COMMENT_OPENED_TOKEN = "/*";

	private static final String MULTILINE_COMMENT_CLOSED_TOKEN = "*/";

	private FileCodeLinesCountingUtils() {
		throw new IllegalAccessError("Do not call");
	}

	/**
	 * Counts lines of code in the java file
	 *
	 * @param file
	 * 		- particular java file
	 * @return - count of lines assumed to be a code (not comments, not blank etc)
	 * @throws IllegalArgumentException
	 * 		if file is null, not a file or not exists or not a java file
	 */
	public static LocCountingItem getLocCountForFile(final File file) {
		if (!isJavaFile(file)) {
			return null;
		}

		try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
			final int count = getLocCountForStream(reader);
			return LocCountingItem.of(file.getName(), count);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected static boolean isJavaFile(final File file) {
		return file != null && file.exists() && file.isFile()
				&& file.getName().toLowerCase().endsWith(".java");
	}

	protected static int getLocCountForStream(final Reader in) throws IOException {
		final BufferedReader reader = new BufferedReader(in);
		String rawLine;
		final LineEvaluationContext context = new LineEvaluationContext();
		int linesCount = 0;
		while ((rawLine = reader.readLine()) != null) {
			final String line = rawLine.trim();
			if (line.length() == 0) {
				continue;
			}
			context.resetForNewLine();
			evaluateLine(line, context);
			if (context.isLineHasCode()) {
				linesCount++;
			}
		}
		return linesCount;
	}

	protected static void evaluateLine(final String line, final LineEvaluationContext context) {
		boolean insideLineComment = false;
		boolean insideQuotes = false;
		if (line.length() == 1 && !context.isInsideMultilineComment()) {
			context.setLineHasCode(true);
			return;
		}
		for (int pos = 1; pos < line.length(); pos++) {
			final char prevChar = line.charAt(pos - 1);
			if (prevChar == '\"' && !insideLineComment && !context.isInsideMultilineComment()
					&& !insideQuotes) {
				insideQuotes = true;
				context.setLineHasCode(true);
				continue;
			}
			final char currentChar = line.charAt(pos);
			final String token = prevChar + "" + currentChar;
			if (insideQuotes) {
				if (currentChar == '\"' && !token.equals("\\\"")) {
					insideQuotes = false;
				}
				continue;
			}
			if (token.equals(LINE_COMMENT_TOKEN) && !context.isInsideMultilineComment()) {
				insideLineComment = true;
				pos++;
				continue;
			}
			if (token.equals(MULTILINE_COMMENT_OPENED_TOKEN)) {
				if (insideLineComment || context.isInsideMultilineComment()) {
					continue;
				}
				pos++;
				context.setInsideMultilineComment(true);
				continue;
			}
			if (token.equals(MULTILINE_COMMENT_CLOSED_TOKEN)) {
				context.setInsideMultilineComment(false);
				insideLineComment = false;
				pos++;
				continue;
			}
			if (!insideLineComment && !context.isInsideMultilineComment() && token.trim().length() > 0) {
				context.setLineHasCode(true);
			}
		}
	}

	protected static class LineEvaluationContext {

		private boolean lineHasCode = false;
		private boolean insideMultilineComment = false;

		public boolean isLineHasCode() {
			return lineHasCode;
		}

		public void setLineHasCode(final boolean lineHasCode) {
			this.lineHasCode = lineHasCode;
		}

		public boolean isInsideMultilineComment() {
			return insideMultilineComment;
		}

		public void setInsideMultilineComment(final boolean insideMultilineComment) {
			this.insideMultilineComment = insideMultilineComment;
		}

		public void resetForNewLine() {
			lineHasCode = false;
		}
	}
}
