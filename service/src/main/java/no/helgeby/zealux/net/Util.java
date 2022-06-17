package no.helgeby.zealux.net;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Hex;

public class Util {

	public static String decAndHex(byte value) {
		return value + " (0x" + Integer.toHexString((int) value & 0xff).toUpperCase() + ")";
	}

	public static String decAndHex(short value) {
		return value + " (0x" + Integer.toHexString((int) value & 0xffff).toUpperCase() + ")";
	}

	public static String decAndHex(int value) {
		return value + " (0x" + Integer.toHexString(value).toUpperCase() + ")";
	}

	public static String decAndHex(long value) {
		return value + " (0x" + Long.toHexString(value).toUpperCase() + ")";
	}

	public static String hex(byte value) {
		return "0x" + Integer.toHexString((int) value & 0xff).toUpperCase();
	}

	public static String hex(short value) {
		return "0x" + Integer.toHexString((int) value & 0xffff).toUpperCase();
	}

	public static String hex(int value) {
		return "0x" + Integer.toHexString(value).toUpperCase();
	}

	public static String hex(long value) {
		return "0x" + Long.toHexString(value).toUpperCase();
	}

	public static String hexWithPadding(int value, int paddingSize) {
		String hex = Integer.toHexString(value).toUpperCase();
		StringBuilder b = new StringBuilder();
		if (hex.length() < paddingSize) {
			int padding = paddingSize - hex.length();
			for (int i = 0; i < padding; i++) {
				b.append("0");
			}
		}
		b.append(hex);
		return b.toString();
	}

	public static String hexDump(byte[] data) {
		String hexString = Hex.encodeHexString(data, false);

		StringBuilder b = new StringBuilder();
		StringBuilder textLine = new StringBuilder();
		int column = 0;
		short offset = 0;

		for (int i = 0; i < hexString.length(); i += 2) {
			if (column == 0) {
				String lineNumber = hexWithPadding(offset, 4);
				b.append(lineNumber);
				b.append("  ");
			}

			b.append(hexString.substring(i, i + 2));
			textLine.append(character(data, i / 2));
			column++;

			if (column >= 16) {
				b.append("  ");
				b.append(textLine);
				if (i + 2 < hexString.length()) {
					b.append("\n");
					offset += 16;
				}
				column = 0;
				textLine = new StringBuilder();
			} else if (i + 2 < hexString.length()) {
				if (column == 8) {
					b.append("  ");
					textLine.append(" ");
				} else {
					b.append(" ");
				}
			}
			if (i == hexString.length() - 2) {
				// Last line. Add padding and text.
				int spaces = 50 - ((column * 2) + column);
				if (column <= 8) {
					// Extra space between groups.
					spaces++;
				}
				for (int s = 0; s < spaces; s++) {
					b.append(' ');
				}
				b.append(textLine);
			}
		}

		return b.toString();
	}

	private static String character(byte[] data, int start) {
		byte[] character = new byte[1];
		character[0] = data[start];
		if (character[0] >= 0 && character[0] < 32) {
			character[0] = '.';
		}
		return new String(character, StandardCharsets.ISO_8859_1);
	}
}
