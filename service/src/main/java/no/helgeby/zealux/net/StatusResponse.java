package no.helgeby.zealux.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StatusResponse {

	PacketHeader header;
	byte action;
	byte numObjects;
	short padding;
	List<ParameterBlock> parameterBlocks;
	Instant timestamp;

	public StatusResponse(DataInputStream in) throws IOException {
		parameterBlocks = new ArrayList<>();
		timestamp = Instant.now();
		read(in);
	}

	public StatusResponse(Packet in) throws IOException {
		parameterBlocks = new ArrayList<>();
		timestamp = Instant.now();
		ByteArrayInputStream byteStream = new ByteArrayInputStream(in.getData());
		DataInputStream dataStream = new DataInputStream(byteStream);
		read(dataStream);
	}

	public void read(DataInputStream in) throws IOException {
		header = new PacketHeader(in);
		action = in.readByte();
		numObjects = in.readByte();
		padding = in.readShort();

		if (numObjects > 0) {
			for (int i = 0; i < numObjects; i++) {
				parameterBlocks.add(new ParameterBlock(in));
			}
		}
	}

	public byte getAction() {
		return action;
	}

	public List<ParameterBlock> getParameterBlocks() {
		return parameterBlocks;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("header", header);
		builder.append("action", Util.decAndHex(action));
		builder.append("numObjects", Util.decAndHex(numObjects));
		builder.append("parameterBlocks", parameterBlocks);
		builder.append("timestamp", timestamp);
		return builder.toString();
	}
}
