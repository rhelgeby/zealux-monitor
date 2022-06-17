package no.helgeby.zealux.controller;

import static no.helgeby.zealux.net.ObjectHeader.SUB_TYPE_CONFIG;
import static no.helgeby.zealux.net.ObjectHeader.SUB_TYPE_STATUS;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the details and descriptions about parameters in the heat pump.
 * <p>
 * Hard coded for now, but could be rewritten to read descriptions from a file.
 * <p>
 * System config bitmasks:
 * 
 * <pre>
 * Sys config 1:
 *
 * Bits 0-1:   Operating mode: 0 = cool, 1 = heat, 2 = Auto
 * Bit 2:      Timer on enabled
 * Bit 3:      Water pump running mode (see manual, 0 = heat pump controls water pump, 1 = always on)
 * Bit 4:      Electronic Valve style ?
 * Bit 5:      On/off
 * Bit 6:      Debug mode (0 = auto, 1 = manual config C6, C7, C8 in effect)
 * Bit 7:      Timer off enabled
 *
 * Sys config 2:
 * Bit 0:      Manual Defrost
 * Bit 1:      Reset to factory settings OR set Valve mode, you'll never know!
 * Bit 2:
 * </pre>
 */
public class ParameterDescriptions {

	public static final ParameterDescription UNKNOWN_PARAMETER = new ParameterDescription(9999, (short) 0, "(unknown)", "Unkown parameter.", null, false);
	public static final ParameterDescription UNKNOWN_STATUS_PARAMETER = new ParameterDescription(9998, SUB_TYPE_STATUS, "(unknown)", "Unkown status parameter.", null, false);
	public static final ParameterDescription UNKNOWN_CONFIG_PARAMETER = new ParameterDescription(9997, SUB_TYPE_CONFIG, "(unknown)", "Unkown config parameter.", null, false);

	private static List<ParameterDescription> parameters = initParameters();

	private static List<ParameterDescription> initParameters() {
		List<ParameterDescription> parameters = new ArrayList<>();

		parameters.add(new ParameterDescription(16, SUB_TYPE_STATUS, "Water in temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(17, SUB_TYPE_STATUS, "Water out temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(18, SUB_TYPE_STATUS, "Ambient temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(19, SUB_TYPE_STATUS, "Cold pipe temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(20, SUB_TYPE_STATUS, "Heating pipe temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(21, SUB_TYPE_STATUS, "IPM module temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(22, SUB_TYPE_STATUS, "Fan speed", null, "RPM", false));
		parameters.add(new ParameterDescription(23, SUB_TYPE_STATUS, "Exhaust temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(24, SUB_TYPE_STATUS, "Compressor input temperature?", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(25, SUB_TYPE_STATUS, "EEV steps", "Electronic exhaust valve opening setting.", "steps", false));
		parameters.add(new ParameterDescription(26, SUB_TYPE_STATUS, "Compressor current", null, "A", false));
		parameters.add(new ParameterDescription(27, SUB_TYPE_STATUS, "Compressor frequency", null, "Hz", false));
		parameters.add(new ParameterDescription(33, SUB_TYPE_STATUS, "Compressor speed mode", null, null, false));
		parameters.add(new ParameterDescription(34, SUB_TYPE_STATUS, "Frequency limit code", null, null, false));
		parameters.add(new ParameterDescription(48, SUB_TYPE_STATUS, "Alarm code 1", null, null, false));
		parameters.add(new ParameterDescription(49, SUB_TYPE_STATUS, "Alarm code 2", null, null, false));
		parameters.add(new ParameterDescription(50, SUB_TYPE_STATUS, "Alarm code 3", null, null, false));
		parameters.add(new ParameterDescription(51, SUB_TYPE_STATUS, "Alarm code 4", null, null, false));
		parameters.add(new ParameterDescription(52, SUB_TYPE_STATUS, "System status code", null, null, false));
		parameters.add(new ParameterDescription(53, SUB_TYPE_STATUS, "System running code", null, null, false));
		parameters.add(new ParameterDescription(54, SUB_TYPE_STATUS, "Device status code", null, null, false));
		parameters.add(new ParameterDescription(55, SUB_TYPE_STATUS, "Heating max temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(56, SUB_TYPE_STATUS, "Cooling min temperature", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(64, SUB_TYPE_STATUS, "Device type", null, null, false));
		parameters.add(new ParameterDescription(65, SUB_TYPE_STATUS, "Main board HW revision", null, null, false));
		parameters.add(new ParameterDescription(66, SUB_TYPE_STATUS, "Main board SW revision", null, null, false));
		parameters.add(new ParameterDescription(67, SUB_TYPE_STATUS, "Manual HW code", null, null, false));
		parameters.add(new ParameterDescription(68, SUB_TYPE_STATUS, "Manual SW code", null, null, false));

		parameters.add(new ParameterDescription(1, SUB_TYPE_CONFIG, "Heating mode target temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(2, SUB_TYPE_CONFIG, "Cooling mode target temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(3, SUB_TYPE_CONFIG, "Auto mode target temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(4, SUB_TYPE_CONFIG, "System config 1", "See system config bitmask 1.", null, false));
		parameters.add(new ParameterDescription(5, SUB_TYPE_CONFIG, "System config 2", "See system config bitmask 2.", null, false));
		parameters.add(new ParameterDescription(6, SUB_TYPE_CONFIG, "Manual frequency setting", null, "Hz", false));
		parameters.add(new ParameterDescription(7, SUB_TYPE_CONFIG, "Manual EEV setting", null, "steps", false));
		parameters.add(new ParameterDescription(8, SUB_TYPE_CONFIG, "Manual fan speed setting", null, "RPM", false));
		parameters.add(new ParameterDescription(9, SUB_TYPE_CONFIG, "Defrost in temp", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(10, SUB_TYPE_CONFIG, "Defrost out temp", "Heating pipe temperature threshold required to end defrost.", "\u00B0C", true));
		parameters.add(new ParameterDescription(11, SUB_TYPE_CONFIG, "Water in temp calibration", null, "\u00B0C", true));
		parameters.add(new ParameterDescription(12, SUB_TYPE_CONFIG, "Defrost in time", "Minimum time between defrost sequences.", "min", false));
		parameters.add(new ParameterDescription(13, SUB_TYPE_CONFIG, "Defrost out time", "Max defrost time?", "min", false));
		parameters.add(new ParameterDescription(14, SUB_TYPE_CONFIG, "Hot over", null, "\u00B0C", false));
		parameters.add(new ParameterDescription(15, SUB_TYPE_CONFIG, "Cold over", null, "\u00B0C", false));
		parameters.add(new ParameterDescription(16, SUB_TYPE_CONFIG, "Power mode", null, null, false));
		parameters.add(new ParameterDescription(32, SUB_TYPE_CONFIG, "Current time", "High byte: hours, low byte: minutes", null, false));
		parameters.add(new ParameterDescription(33, SUB_TYPE_CONFIG, "Timer on time", "High byte: hours, low byte: minutes", null, false));
		parameters.add(new ParameterDescription(34, SUB_TYPE_CONFIG, "Timer off time", "High byte: hours, low byte: minutes", null, false));

		return parameters;
	}

	public static ParameterDescription getParameter(int index, int type) {
		for (ParameterDescription parameter : parameters) {
			if (parameter.index == index && parameter.type == type) {
				return parameter;
			}
		}
		return UNKNOWN_PARAMETER;
	}

	public static ParameterDescription getStatusParameter(int index) {
		for (ParameterDescription parameter : parameters) {
			if (parameter.index == index && parameter.type == SUB_TYPE_STATUS) {
				return parameter;
			}
		}
		return UNKNOWN_STATUS_PARAMETER;
	}

	public static ParameterDescription getConfigParameter(int index) {
		for (ParameterDescription parameter : parameters) {
			if (parameter.index == index && parameter.type == SUB_TYPE_CONFIG) {
				return parameter;
			}
		}
		return UNKNOWN_CONFIG_PARAMETER;
	}

	public static boolean isUnknown(ParameterDescription parameter) {
		return parameter == UNKNOWN_PARAMETER || parameter == UNKNOWN_STATUS_PARAMETER
				|| parameter == UNKNOWN_STATUS_PARAMETER;
	}
}
