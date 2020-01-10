package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author M.Skripnikov
 * 1/10/20
 */
public final class LocCountingItem {

	private static final String OUTPUT_TEMPLATE = "\n%s : %s";

	private int locCount;

	private String name;

	private final List<LocCountingItem> items = new ArrayList<>();

	private LocCountingItem(final String name, final int locCount) {
		this.name = name;
		this.locCount = locCount;
	}

	public int getLocCount() {
		return locCount;
	}

	public static LocCountingItem of(final String name) {
		return new LocCountingItem(name, 0);
	}

	public static LocCountingItem of(final String name, final int locCount) {
		return new LocCountingItem(name, locCount);
	}

	public void aggregate(final LocCountingItem item) {
		if (item == null) {
			return;
		}
		items.add(item);
		locCount += item.getLocCount();
	}

	@Override
	public String toString() {
		if (locCount == 0) {
			return "";
		}
		final StringBuilder res = new StringBuilder(String.format(OUTPUT_TEMPLATE, name, locCount));
		items.forEach(item -> res.append(item.toString()));
		return res.toString();
	}
}
