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

	private ItemType itemType;

	private final List<LocCountingItem> items = new ArrayList<>();

	private LocCountingItem(final String name, final ItemType itemType, final int locCount) {
		this.name = name;
		this.itemType = itemType;
		this.locCount = locCount;
	}

	public int getLocCount() {
		return locCount;
	}

	public static LocCountingItem ofDir(final String name) {
		return new LocCountingItem(name, ItemType.DIR, 0);
	}

	public static LocCountingItem ofFile(final String name, final int locCount) {
		return new LocCountingItem(name, ItemType.DIR, locCount);
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
		if (itemType == ItemType.FILE) {
			return String.format(OUTPUT_TEMPLATE, name, locCount);
		}
		final StringBuilder res = new StringBuilder(String.format(OUTPUT_TEMPLATE, name, locCount));
		items.forEach(item -> res.append(item.toString()));
		return res.toString();
	}
}
