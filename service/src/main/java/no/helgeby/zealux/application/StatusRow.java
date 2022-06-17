package no.helgeby.zealux.application;

import java.time.Instant;

import no.helgeby.zealux.controller.Parameter;

public class StatusRow extends ValueRow {

	public StatusRow(Parameter parameter, Instant probeTime) {
		super(parameter, probeTime);
		type = "status";
	}

}
