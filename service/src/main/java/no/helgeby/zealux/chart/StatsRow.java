package no.helgeby.zealux.chart;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static no.helgeby.zealux.chart.DateFormats.DATE_HOUR;
import static no.helgeby.zealux.chart.DateFormats.TIME_OF_DAY;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class StatsRow {

	Timestamp probeTime;
	BigDecimal waterInTemp;
	BigDecimal waterOutTemp;
	BigDecimal compressorCurrent;
	BigDecimal compressorFrequency;
	BigDecimal eevSteps;
	BigDecimal fanSpeed;
	BigDecimal heatingPipeTemp;
	BigDecimal exhaustTemp;
	BigDecimal ambientTemp;

	public String timeOfDay() {
		return TIME_OF_DAY.format(zonedDateTime());
	}

	public String date() {
		return ISO_LOCAL_DATE.format(zonedDateTime());
	}

	public String dateHour() {
		return DATE_HOUR.format(zonedDateTime());
	}

	public ZonedDateTime zonedDateTime() {
		return ZonedDateTime.ofInstant(probeTime.toInstant(), ZoneOffset.systemDefault());
	}
}
