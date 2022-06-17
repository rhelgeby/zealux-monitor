package no.helgeby.zealux.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Timestamp {

	short year;
	byte month;
	byte day;
	byte hour;
	byte min;
	byte sec;
	byte tz;

	public Timestamp() {
		now();
	}

	public Timestamp(DataInputStream in) throws IOException {
		read(in);
	}

	private void now() {
		ZonedDateTime now = ZonedDateTime.now();
		year = (short) now.getYear();
		month = (byte) now.getMonthValue();
		day = (byte) now.getDayOfMonth();
		hour = (byte) now.getHour();
		min = (byte) now.getMinute();
		sec = (byte) now.getSecond();

		Duration offsetDuration = Duration.ofSeconds(now.getOffset().getTotalSeconds());
		byte offsetHours = (byte) (offsetDuration.toHours());
		tz = offsetHours;
	}

	public void read(DataInputStream in) throws IOException {
		year = in.readShort();
		month = in.readByte();
		day = in.readByte();
		hour = in.readByte();
		min = in.readByte();
		sec = in.readByte();
		tz = in.readByte();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeShort(year);
		out.writeByte(month);
		out.writeByte(day);
		out.writeByte(hour);
		out.writeByte(min);
		out.writeByte(sec);
		out.writeByte(tz);
	}

	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("year", year);
		builder.append("month", month);
		builder.append("day", day);
		builder.append("hour", hour);
		builder.append("min", min);
		builder.append("sec", sec);
		builder.append("tz", tz);
		return builder.toString();
	}
}
