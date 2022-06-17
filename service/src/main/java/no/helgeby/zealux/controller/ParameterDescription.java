package no.helgeby.zealux.controller;

import no.helgeby.zealux.net.ObjectHeader;

public class ParameterDescription {

	public final int index;
	/**
	 * See sub type constants in {@link ObjectHeader}.
	 */
	public final int type;
	public final String label;
	public final String description;
	public final String unit;
	/**
	 * Whether the raw value is a decimal multiplied by 10.
	 */
	public final boolean decimal;

	public ParameterDescription(int index, short type, String label, String description, String unit, boolean decimal) {
		this.index = index;
		this.type = type;
		this.label = label;
		this.description = description;
		this.unit = unit;
		this.decimal = decimal;
	}

}
