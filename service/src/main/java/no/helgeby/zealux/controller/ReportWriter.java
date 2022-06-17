package no.helgeby.zealux.controller;

import java.util.List;

import no.helgeby.zealux.net.ObjectHeader;
import no.helgeby.zealux.net.ParameterBlock;
import no.helgeby.zealux.net.StatusResponse;

public class ReportWriter {

	public static String prettyPrint(StatusResponse statusResponse) {
		StringBuilder builder = new StringBuilder();

		List<ParameterBlock> blocks = statusResponse.getParameterBlocks();
		if (blocks.isEmpty()) {
			builder.append("(no parameter blocks)");
			return builder.toString();
		}

		for (ParameterBlock block : blocks) {
			prettyPrint(builder, block);
		}

		return builder.toString();
	}

	private static void prettyPrint(StringBuilder builder, ParameterBlock block) {
		ObjectHeader header = block.getObjectHeader();
		short subType = header.getSubType();

		switch (subType) {
		case ObjectHeader.SUB_TYPE_STATUS:
		case ObjectHeader.SUB_TYPE_CONFIG:
		case ObjectHeader.SUB_TYPE_QUERY_ALL:
			prettyPrint(builder, block.getStartIndex(), block.getValues(), subType);
			break;
		default:
			// Skip unknown types.
		}
	}

	private static void prettyPrint(StringBuilder builder, short startIndex, short values[], short type) {
		switch (type) {
		case ObjectHeader.SUB_TYPE_STATUS:
			builder.append("Status values:");
			break;
		case ObjectHeader.SUB_TYPE_CONFIG:
			builder.append("Config parameters:");
			break;
		default:
			// Skip unknown types.
			return;
		}
		builder.append(System.lineSeparator());

		for (int i = 0; i < values.length; i++) {
			int index = startIndex + i;
			ParameterDescription description = ParameterDescriptions.getParameter(index, type);
			if (ParameterDescriptions.isUnknown(description)) {
				continue;
			}

			Parameter parameter = new Parameter(index, values[i], description);
			builder.append(parameter);
			builder.append(System.lineSeparator());
		}
	}
}
