package no.helgeby.zealux.application;

import java.time.Instant;

import no.helgeby.zealux.controller.Parameter;

public class ConfigRow extends ValueRow {

	public ConfigRow(Parameter parameter, Instant probeTime) {
		super(parameter, probeTime);
		type = "config";
	}

}
