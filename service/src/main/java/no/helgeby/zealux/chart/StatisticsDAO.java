package no.helgeby.zealux.chart;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

public class StatisticsDAO {

	private JdbcTemplate jdbcTemplate;

	public StatisticsDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<StatsRow> detailStats(Duration amount) {
		Timestamp since = durationFromNow(amount);

		String sql = """
				SELECT *
				FROM stats
				WHERE probe_time >= ?
				ORDER BY probe_time
				""";

		Object[] args = { since };
		int[] argTypes = { Types.TIMESTAMP };

		return jdbcTemplate.query(sql, args, argTypes, new StatsRowMapper());
	}

	public List<StatsRow> hourlyStats(Duration amount) {
		Timestamp since = durationFromNow(amount);

		String sql = """
				SELECT
					date_trunc('hour', probe_time) as hour,
					avg(water_in_temp) as avg_water_in_temp,
					avg(water_out_temp) as avg_water_out_temp,
					avg(comp_current) as avg_comp_current,
					avg(exhaust_temp) as avg_exhaust_temp,
					avg(ambient_temp) as avg_ambient_temp
				FROM stats
				WHERE probe_time >= ?
				GROUP BY date_trunc('hour', probe_time)
				ORDER BY "hour"
				""";

		Object[] args = { since };
		int[] argTypes = { Types.TIMESTAMP };

		return jdbcTemplate.query(sql, args, argTypes, new AvgStatsRowMapper());
	}

	public List<StatsRow> dailyStats(Duration amount) {
		Timestamp since = durationFromNow(amount);

		String sql = """
				SELECT
					date(date_trunc('day', probe_time)) as "date",
					avg(water_in_temp) as avg_water_in_temp,
					avg(water_out_temp) as avg_water_out_temp,
					avg(comp_current) as avg_comp_current,
					avg(exhaust_temp) as avg_exhaust_temp,
					avg(ambient_temp) as avg_ambient_temp
				FROM stats
				WHERE probe_time >= ?
				GROUP BY date_trunc('day', probe_time)
				ORDER BY "date"
				""";

		Object[] args = { since };
		int[] argTypes = { Types.TIMESTAMP };

		return jdbcTemplate.query(sql, args, argTypes, new AvgStatsRowMapper(true));
	}

	public List<PowerStatsRow> powerStats(Duration amount) {
		Timestamp since = durationFromNow(amount);

		final int volts = 230;

		String sql = """
				SELECT
					date(date_trunc('day', cph.hour)) AS "date",
				    sum(cph.avg_current) AS ah,
				    sum(cph.avg_current) * ? / 1000 AS kwh
				FROM (
					SELECT
						date_trunc('hour', probe_time) as hour,
						avg(comp_current) as avg_current
					FROM stats
					WHERE probe_time > ?
					GROUP BY date_trunc('hour', probe_time)
				) cph
				GROUP BY (date_trunc('day', cph.hour))
				ORDER BY "date"
				""";

		Object[] args = { volts, since };
		int[] argTypes = { Types.NUMERIC, Types.TIMESTAMP };

		return jdbcTemplate.query(sql, args, argTypes, new PowerStatsRowMapper());
	}

	public List<StatsRow> statsPastHour() {
		return detailStats(Duration.ofHours(1));
	}

	private Timestamp durationFromNow(Duration amount) {
		Instant since = Instant.now().minus(amount);
		return Timestamp.from(since);
	}
}
