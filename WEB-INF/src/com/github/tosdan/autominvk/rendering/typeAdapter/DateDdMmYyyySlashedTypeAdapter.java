package com.github.tosdan.autominvk.rendering.typeAdapter;

/**
 * Adapter per interpretare le date nel formato dd/MM/yyyy
 * @author Daniele
 *
 */
public class DateDdMmYyyySlashedTypeAdapter extends DateAbstractTypeAdapter {
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	public DateDdMmYyyySlashedTypeAdapter() {
		super(DD_MM_YYYY);
	}
}
