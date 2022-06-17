package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthResponse {

	PacketHeader header;
	final byte act1 = 4;
	final byte act2 = 0;
	final byte act3 = 0;
	final byte act4 = 3;
	byte[] responseMd5;
	Timestamp timestamp;

	public AuthResponse(int clientSessionId, int deviceId, byte[] responseMd5) {
		header = new PacketHeader();
		header.headerCode = PacketHeader.HEADER_CODE_REQUEST;
		header.sequenceNumber = 0;
		header.clientSessionId = clientSessionId;
		header.deviceId = deviceId;
		header.commandCategory = PacketHeader.COMMAND_LOGIN;
		header.payloadLength = 28; // 28 bytes after packet header.

		if (responseMd5.length != 16) {
			throw new IllegalArgumentException("MD5 hash must be 16 bytes.");
		}

		this.responseMd5 = responseMd5;
		this.timestamp = new Timestamp();
	}

	public void write(DataOutputStream out) throws IOException {
		header.write(out);
		out.writeByte(act1);
		out.writeByte(act2);
		out.writeByte(act3);
		out.writeByte(act4);
		for (int i = 0; i < responseMd5.length; i++) {
			out.writeByte(responseMd5[i]);
		}
		timestamp.write(out);
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
		builder.append("act1", decAndHex(act1));
		builder.append("act2", decAndHex(act2));
		builder.append("act3", decAndHex(act3));
		builder.append("act4", decAndHex(act4));
		builder.append("responseMd5", Hex.encodeHexString(responseMd5, false));
		builder.append("timestamp", timestamp);
		return builder.toString();
	}
}
