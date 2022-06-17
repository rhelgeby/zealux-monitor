package no.helgeby.zealux.application;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import no.helgeby.zealux.controller.Parameter;

public abstract class ValueRow {

	Timestamp probeTime;
	int index;
	BigDecimal value;
	protected String type;

	public ValueRow(Parameter parameter, Instant probeTime) {
		this.probeTime = Timestamp.from(probeTime);
		value = parameter.getNumericValue();
		index = parameter.getIndexValue();
	}

	public Timestamp getProbeTime() {
		return probeTime;
	}

	public int getIndex() {
		return index;
	}

	public BigDecimal getValue() {
		return value;
	}
}
