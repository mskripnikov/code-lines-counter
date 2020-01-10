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
	 * @param folder
	 * 		- particular folder
	 * @return - count of lines assumed to be a code (not comments, not blank etc)
	 * @throws IllegalArgumentException
	 * 		if file is null, not a file or not exists or not a java file
	 */
	public static LocCountingItem getLocCount(final File folder) {
		if (folder == null || !folder.exists() || !folder.isDirectory()) {
			return null;
		}
		final LocCountingItem item = LocCountingItem.of(folder.getName());
		final File[] files = listAcceptableFiles(folder);
		for (final File file : files) {
			if (file.isDirectory()) {
				item.aggregate(getLocCount(file));
			} else {
				item.aggregate(FileCodeLinesCountingUtils.getLocCountForFile(file));
			}
		}
		return item;
	}

	protected static File[] listAcceptableFiles(final File folder) {
		final File[] files = folder
				.listFiles(f -> f.isDirectory() || FileCodeLinesCountingUtils.isJavaFile(f));
		return files == null ? EMPTY_ARRAY : files;
	}

}
