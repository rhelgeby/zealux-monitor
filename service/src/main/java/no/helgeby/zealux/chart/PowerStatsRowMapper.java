package no.helgeby.zealux.chart;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PowerStatsRowMapper implements RowMapper<PowerStatsRow> {

	@Override
	public PowerStatsRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		PowerStatsRow row = new PowerStatsRow();
		row.date = rs.getDate("date");
		row.ah = rs.getBigDecimal("ah");
		row.kwh = rs.getBigDecimal("kwh");
		return row;
	}

}
