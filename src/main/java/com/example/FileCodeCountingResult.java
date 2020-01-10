package com.example;

/**
 * @author M.Skripnikov
 * 1/10/20
 */
public final class FileCodeCountingResult {
	private String fileName;
	private int locCount;

	public FileCodeCountingResult(final String fileName, final int locCount) {
		this.fileName = fileName;
		this.locCount = locCount;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLocCount() {
		return locCount;
	}
}
