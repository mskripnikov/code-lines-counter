package com.example;

import java.io.*;

/*
 * @author M.Skripnikov
 * 1/4/20
 //*/

public class FileCodeLinesCounter {

	private static final String LINE_COMMENT_TOKEN = "//";
	private static final String MULTILINE_COMMENT_OPENED_TOKEN = "/*";
	private static final String MULTILINE_COMMENT_CLOSED_TOKEN = "*/";

	public static int getLOCCount(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File is null");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("File does not exists");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("File is not a file");
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			return getLOCCount(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	protected static int getLOCCount(Reader in) throws IOException {
		final BufferedReader reader = new BufferedReader(in);
		String aLine;
		LineEvaluationContext context = new LineEvaluationContext();
		int linesCount = 0;
		while ((aLine = reader.readLine()) != null) {
			String line = aLine.trim();
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

	protected static void evaluateLine(String line, LineEvaluationContext context) {
		boolean insideLineComment = false;
		for (int pos = 1; pos < line.length(); pos++) {
			char prevChar = line.charAt(pos - 1);
			String token = prevChar + "" + line.charAt(pos);
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

	public static class LineEvaluationContext {
		private boolean lineHasCode = false;
		private boolean insideMultilineComment = false;

		public boolean isLineHasCode() {
			return lineHasCode;
		}

		public void setLineHasCode(boolean lineHasCode) {
			this.lineHasCode = lineHasCode;
		}

		public boolean isInsideMultilineComment() {
			return insideMultilineComment;
		}

		public void setInsideMultilineComment(boolean insideMultilineComment) {
			this.insideMultilineComment = insideMultilineComment;
		}

		public void resetForNewLine() {
			lineHasCode = false;
		}
	}
}
