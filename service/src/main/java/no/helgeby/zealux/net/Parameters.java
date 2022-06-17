package no.helgeby.zealux.net;

import static no.helgeby.zealux.net.Util.decAndHex;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Parameters {

	short startIndex;
	short numParameters;
	short[] values;

	public Parameters(DataInputStream in) throws IOException {
		read(in);
	}

	public void read(DataInputStream in) throws IOException {
		startIndex = in.readShort();
		numParameters = in.readShort();

		if (numParameters < 0) {
			throw new IOException("Number of parameters is negative. Overflow?");
		}

		values = new short[numParameters];

		for (int i = 0; i < numParameters; i++) {
			values[i] = in.readShort();
		}
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("startIndex", decAndHex(startIndex));
		builder.append("numParameters", decAndHex(numParameters));
		for (int i = 0; i < numParameters; i++) {
			builder.append("values[" + i + "]", decAndHex(values[i]));
		}
		return builder.toString();
	}
}
