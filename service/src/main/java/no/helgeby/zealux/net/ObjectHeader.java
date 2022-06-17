package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ObjectHeader {

	/**
	 * Query status or config values.
	 */
	public static final int OBJECT_TYPE_STATUS_CONFIG = 0x0002002e;

	public static final short SUB_TYPE_STATUS = 1;
	public static final short SUB_TYPE_CONFIG = 2;
	public static final short SUB_TYPE_QUERY_ALL = (short) 0xffff;

	int objectType;
	short subType;
	short dataSize;

	public ObjectHeader() {

	}

	public ObjectHeader(DataInputStream in) throws IOException {
		read(in);
	}

	public void read(DataInputStream in) throws IOException {
		objectType = in.readInt();
		subType = in.readShort();
		dataSize = in.readShort();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeInt(objectType);
		out.writeShort(subType);
		out.writeShort(dataSize);
	}

	public int getObjectType() {
		return objectType;
	}

	public short getSubType() {
		return subType;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("objectType", decAndHex(objectType));
		builder.append("subType", decAndHex(subType));
		builder.append("dataSize", decAndHex(dataSize));
		return builder.toString();
	}
}
