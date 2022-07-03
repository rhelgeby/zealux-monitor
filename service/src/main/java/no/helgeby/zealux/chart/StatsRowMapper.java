package no.helgeby.zealux.chart;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StatsRowMapper implements RowMapper<StatsRow> {

	@Override
	public StatsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		StatsRow row = new StatsRow();
		row.probeTime = rs.getTimestamp("probe_time");
		row.waterInTemp = rs.getBigDecimal("water_in_temp");
		row.waterOutTemp = rs.getBigDecimal("water_out_temp");
		row.compressorCurrent = rs.getBigDecimal("comp_current");
		row.compressorFrequency = rs.getBigDecimal("comp_frequency");
		row.eevSteps = rs.getBigDecimal("eev_steps");
		row.fanSpeed = rs.getBigDecimal("fan_speed");
		row.heatingPipeTemp = rs.getBigDecimal("heating_pipe_temp");
		row.exhaustTemp = rs.getBigDecimal("exhaust_temp");
		row.ambientTemp = rs.getBigDecimal("ambient_temp");
		return row;
	}

}
