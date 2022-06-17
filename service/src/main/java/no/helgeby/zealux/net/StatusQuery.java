package no.helgeby.zealux.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StatusQuery {

	PacketHeader header;
	final byte action = PacketHeader.ACTION_OBJECT_QUERY;
	final byte numObjects = 1;
	final short padding = 0;
	ObjectHeader objectHeader;

	public StatusQuery(short subType, int clientSessionId, int deviceId) {
		header = new PacketHeader();
		header.headerCode = PacketHeader.HEADER_CODE_REQUEST;
		header.sequenceNumber = 0;
		header.clientSessionId = clientSessionId;
		header.deviceId = deviceId;
		header.commandCategory = PacketHeader.COMMAND_NORMAL;
		header.payloadLength = 12; // 12 bytes after packet header.

		objectHeader = new ObjectHeader();
		objectHeader.objectType = ObjectHeader.OBJECT_TYPE_STATUS_CONFIG;
		objectHeader.subType = subType;
		// Always 0 for queries.
		objectHeader.dataSize = 0;
	}

	public void write(DataOutputStream out) throws IOException {
		header.write(out);
		out.writeByte(action);
		out.writeByte(numObjects);
		out.writeShort(padding);
		objectHeader.write(out);
	}

	public Packet toPacket(SocketAddress address) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		write(dataOutputStream);
		byte[] packetBytes = outputStream.toByteArray();
		DatagramPacket datagramPacket = new DatagramPacket(packetBytes, packetBytes.length);
		datagramPacket.setSocketAddress(address);
		return new Packet(datagramPacket);
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("header", header);
		builder.append("action", Util.decAndHex(action));
		builder.append("numObjects", Util.decAndHex(numObjects));
		builder.append("objectHeader", objectHeader);
		return builder.toString();
	}
}
