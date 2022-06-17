package no.helgeby.zealux.net;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Wrapper class with helper methods for DatagramPacket.
 */
public class Packet {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	private DatagramPacket packet;

	public Packet() {
		this(DEFAULT_BUFFER_SIZE);
	}

	public Packet(int bufferSize) {
		byte[] buffer = new byte[bufferSize];
		packet = new DatagramPacket(buffer, bufferSize);
	}

	public Packet(DatagramPacket packet) {
		this.packet = packet;
	}

	public DatagramPacket datagramPacket() {
		return packet;
	}

	public byte[] getData() {
		int length = packet.getLength();
		byte[] data = Arrays.copyOfRange(packet.getData(), 0, length);
		return data;
	}

	public String toString() {
		byte[] data = getData();
		StringBuilder b = new StringBuilder();
		b.append("Payload size: ");
		b.append(data.length);
		b.append(" bytes\n");
		b.append("Address: ");
		b.append(packet.getSocketAddress());
		b.append("\nData:\n");
		b.append(Util.hexDump(data));
		return b.toString();
	}

}
