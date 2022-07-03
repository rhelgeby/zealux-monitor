package no.helgeby.zealux.chart;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

public class DateFormats {

	public static final DateTimeFormatter TIME_OF_DAY;
	static {
		TIME_OF_DAY = new DateTimeFormatterBuilder() //
				.appendValue(HOUR_OF_DAY, 2) //
				.appendLiteral(':') //
				.appendValue(MINUTE_OF_HOUR, 2) //
				.toFormatter(Locale.getDefault());
	}

	public static final DateTimeFormatter DATE_HOUR;
	static {
		DATE_HOUR = new DateTimeFormatterBuilder() //
				.appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-') //
                .appendValue(MONTH_OF_YEAR, 2) //
                .appendLiteral('-') //
                .appendValue(DAY_OF_MONTH, 2) //
                .appendLiteral(' ') //
				.appendValue(HOUR_OF_DAY, 2) //
				.toFormatter(Locale.getDefault());
	}

}
