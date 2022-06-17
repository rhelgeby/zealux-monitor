package no.helgeby.zealux.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import no.helgeby.zealux.net.Util;

public class Parameter {

	int index;
	short rawValue;
	ParameterDescription description;

	public Parameter(int index, short rawValue, ParameterDescription description) {
		this.index = index;
		this.rawValue = rawValue;
		this.description = description;
	}

	@Override
	public String toString() {
		String format = "%4s %-32s %10s %-8s %8s %-8s %s";
		return String.format(format, //
				getIndex(), //
				getLabel(), //
				getValue(), //
				getUnit(), //
				getDecValue(), //
				getHexValue(), //
				getDescription());
	}

	private String getIndex() {
		return String.valueOf(index);
	}

	public int getIndexValue() {
		return index;
	}

	private String getLabel() {
		return description.label;
	}

	public String getValue() {
		if (description.decimal) {
			int value = rawValue / 10;
			int fraction = rawValue % 10;
			return String.valueOf(value) + "." + String.valueOf(fraction);
		} else {
			return String.valueOf(rawValue);
		}
	}

	public BigDecimal getNumericValue() {
		BigDecimal rawDecimal = BigDecimal.valueOf(rawValue);
		if (description.decimal) {
			return rawDecimal.divide(BigDecimal.valueOf(10), 1, RoundingMode.UNNECESSARY);
		} else {
			return rawDecimal;
		}
	}

	private String getUnit() {
		if (description.unit != null) {
			return description.unit;
		} else {
			return "";
		}
	}

	private String getDescription() {
		if (description.description != null) {
			return description.description;
		} else {
			return "";
		}
	}

	private String getDecValue() {
		return String.valueOf(rawValue);
	}

	private String getHexValue() {
		return Util.hex(rawValue);
	}

	public ParameterDescription getParameterDescription() {
		return description;
	}
}
