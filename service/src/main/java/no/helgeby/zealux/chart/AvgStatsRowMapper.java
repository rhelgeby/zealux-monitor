package no.helgeby.zealux.chart;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AvgStatsRowMapper implements RowMapper<StatsRow> {

	private boolean daily;

	public AvgStatsRowMapper() {
	}

	public AvgStatsRowMapper(boolean daily) {
		this.daily = daily;
	}
	@Override
	public StatsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		StatsRow row = new StatsRow();
	
		if (daily) {
			row.probeTime = rs.getTimestamp("date");
		}
		else {
			row.probeTime = rs.getTimestamp("hour");
		}
		row.waterInTemp = rs.getBigDecimal("avg_water_in_temp");
		row.waterOutTemp = rs.getBigDecimal("avg_water_out_temp");
		row.compressorCurrent = rs.getBigDecimal("avg_comp_current");
		row.exhaustTemp = rs.getBigDecimal("avg_exhaust_temp");
		row.ambientTemp = rs.getBigDecimal("avg_ambient_temp");
		return row;
	}

}
