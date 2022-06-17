package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Authentication challenge sent by the heat pump.
 */
public class AuthChallenge {

	PacketHeader header;
	byte act1;
	byte act2;
	byte act3;
	byte act4;
	int serverToken;

	public AuthChallenge() {
	}

	public AuthChallenge(Packet in) throws IOException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(in.getData());
		DataInputStream dataStream = new DataInputStream(byteStream);
		read(dataStream);
	}

	public AuthChallenge(DataInputStream in) throws IOException {
		read(in);
	}

	public void read(DataInputStream in) throws IOException {
		header = new PacketHeader(in);
		act1 = in.readByte();
		act2 = in.readByte();
		act3 = in.readByte();
		act4 = in.readByte();
		serverToken = in.readInt();
	}

	public AuthResponse createResponse(String password, int clientToken) {
		byte[] passwordHash = DigestUtils.md5(password);
		MessageDigest md5 = DigestUtils.getMd5Digest();
		md5.update(ByteBuffer.allocate(4).putInt(clientToken).array());
		md5.update(ByteBuffer.allocate(4).putInt(serverToken).array());
		md5.update(passwordHash);
		byte[] challengeResponseHash = md5.digest();
		return new AuthResponse(header.clientSessionId, header.deviceId, challengeResponseHash);
	}

	public int getClientSessionId() {
		return header.clientSessionId;
	}

	public int getDeviceId() {
		return header.deviceId;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("header", header);
		builder.append("act1", decAndHex(act1));
		builder.append("act2", decAndHex(act2));
		builder.append("act3", decAndHex(act3));
		builder.append("act4", decAndHex(act4));
		builder.append("serverToken", decAndHex(serverToken));
		return builder.toString();
	}
}
