package no.helgeby.zealux.net;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ParameterBlock {

	ObjectHeader objectHeader;
	short startIndex;
	short numParameters;
	short[] values;

	public ParameterBlock(DataInputStream in) throws IOException {
		read(in);
	}

	public void read(DataInputStream in) throws IOException {
		objectHeader = new ObjectHeader(in);
		startIndex = in.readShort();
		numParameters = in.readShort();
		if (numParameters > 0) {
			values = new short[numParameters];
			for (int i = 0; i < numParameters; i++) {
				values[i] = in.readShort();
			}
		}
	}

	public ObjectHeader getObjectHeader() {
		return objectHeader;
	}

	public short getStartIndex() {
		return startIndex;
	}

	public short[] getValues() {
		return values;
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("startIndex", Util.decAndHex(startIndex));
		builder.append("numParameters", Util.decAndHex(numParameters));
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				builder.append("values[" + i + "]", Util.decAndHex(values[i]));
			}
		} else {
			builder.append("values", "(null)");
		}
		return builder.toString();
	}
}
