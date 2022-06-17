package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketAddress;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthIntro {

	PacketHeader header;
	final byte act1 = 1;
	final byte act2 = 1;
	final byte act3 = 2;
	final byte act4 = 0;
	int clientToken;
	long deviceSerialNumber;
	final int uuid1 = 0xffffffff;
	final int uuid2 = 0xaceee2d3; // little endian: 0xd3e2eeac
	final int uuid3 = 0;
	final int uuid4 = 0x55c7fd6a; // little endian: 0x6afdc755
	Timestamp timestamp;

	public AuthIntro(int clientToken, long deviceSerialNumber) {
		header = new PacketHeader();
		header.headerCode = PacketHeader.HEADER_CODE_REQUEST;
		header.sequenceNumber = 0;
		header.clientSessionId = 0;
		header.deviceId = 0;
		header.commandCategory = PacketHeader.COMMAND_LOGIN;
		header.payloadLength = 40; // 40 bytes after packet header.

		this.clientToken = clientToken;
		this.deviceSerialNumber = deviceSerialNumber;
		this.timestamp = new Timestamp();
	}

	public void write(DataOutputStream out) throws IOException {
		header.write(out);
		out.writeByte(act1);
		out.writeByte(act2);
		out.writeByte(act3);
		out.writeByte(act4);
		out.writeInt(clientToken);
		out.writeLong(deviceSerialNumber);
		out.writeInt(uuid1);
		out.writeInt(uuid2);
		out.writeInt(uuid3);
		out.writeInt(uuid4);
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
		builder.append("clientToken", decAndHex(clientToken));
		builder.append("deviceSerialNumber", decAndHex(deviceSerialNumber));
		builder.append("uuid1", decAndHex(uuid1));
		builder.append("uuid2", decAndHex(uuid2));
		builder.append("uuid3", decAndHex(uuid3));
		builder.append("uuid4", decAndHex(uuid4));
		builder.append("timestamp", timestamp);
		return builder.toString();
	}
}
