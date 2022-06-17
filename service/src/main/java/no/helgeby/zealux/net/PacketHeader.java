package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PacketHeader {

	public static final byte HEADER_SIZE_BYTES = 16;

	public static final short HEADER_CODE_REQUEST = 0x32;
	public static final short HEADER_CODE_RESPONSE = 0x30;

	public static final short COMMAND_LOGIN = 0xf2;
	public static final short COMMAND_STATUS = 0xf3;
	public static final short COMMAND_NORMAL = 0xf4;

	public static final byte ACTION_CONNECTION_RESET = 0x07;
	/**
	 * Query for information.
	 * 
	 * @see StatusQuery
	 */
	public static final byte ACTION_OBJECT_QUERY = 0x08;
	/**
	 * Set configuration parameters in the heat pump.
	 */
	public static final byte ACTION_SET_OBJECT = 0x09;
	/**
	 * Status update. Sent by heat pump, client must reply.
	 */
	public static final byte ACTION_STATUS_UPDATE = 0x0b;

	/**
	 * <ul>
	 * <li>0x32 - request
	 * <li>0x30 - reply to an earlier request of the same sequence number
	 * </ul>
	 */
	byte headerCode;

	byte padding;

	/**
	 * Monotonically increasing once session has been set up, otherwise 0.
	 */
	short sequenceNumber;

	/**
	 * Provided by server at login.
	 */
	int clientSessionId;

	/**
	 * Provided by server at login.
	 */
	int deviceId;

	/**
	 * <ul>
	 * <li>0xf2 - login and authentication messages
	 * <li>0xf3 - status and keep-alive
	 * <li>0xf4 - normal commands once session has been set
	 * </ul>
	 */
	short commandCategory;

	/**
	 * Payload length in bytes.
	 */
	short payloadLength;

	public boolean isReply() {
		return (headerCode & 2) == 0;
	}

	public PacketHeader() {

	}

	public PacketHeader(Packet in) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(in.getData());
		DataInputStream dataStream = new DataInputStream(byteStream);
		read(dataStream);
	}

	public PacketHeader(DataInputStream in) throws IOException {
		read(in);
	}

	public void read(DataInputStream in) throws IOException {
		headerCode = in.readByte();
		padding = in.readByte();
		sequenceNumber = in.readShort();
		clientSessionId = in.readInt();
		deviceId = in.readInt();
		commandCategory = in.readShort();
		payloadLength = in.readShort();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeByte(headerCode);
		out.writeByte(padding);
		out.writeShort(sequenceNumber);
		out.writeInt(clientSessionId);
		out.writeInt(deviceId);
		out.writeShort(commandCategory);
		out.writeShort(payloadLength);
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("headerCode", decAndHex(headerCode));
		builder.append("padding", decAndHex(padding));
		builder.append("sequenceNumber", decAndHex(sequenceNumber));
		builder.append("clientSessionId", decAndHex(clientSessionId));
		builder.append("deviceId", decAndHex(deviceId));
		builder.append("commandCategory", decAndHex(commandCategory));
		builder.append("payloadLength", decAndHex(payloadLength));
		return builder.toString();
	}

}
