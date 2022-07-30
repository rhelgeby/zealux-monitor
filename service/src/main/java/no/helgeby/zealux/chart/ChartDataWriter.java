package no.helgeby.zealux.chart;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.helgeby.zealux.application.Configuration;

public class ChartDataWriter implements Runnable {

	private static final Logger log = LogManager.getLogger(ChartDataWriter.class);

	private Configuration configuration;
	private StatisticsDAO statisticsDAO;

	private boolean running;
	private ChartData chartData;

	public ChartDataWriter(Configuration configuration, StatisticsDAO statisticsDAO) {
		this.configuration = configuration;
		this.statisticsDAO = statisticsDAO;
	}

	@Override
	public void run() {
		running = true;
		Duration loopInterval = configuration.getChartDataUpdateInterval();
		while (running) {
			try {
				update();
				Thread.sleep(loopInterval.toMillis());
			} catch (InterruptedException e) {
				log.warn("Thread was interrupted. Stopping.");
				running = false;
			}
		}
	}

	private void update() {
		try {
			if (chartData == null) {
				chartData = new ChartData();
			}

			log.info("Fetching statistics.");

			chartData.pastDay = getDetails(Duration.ofDays(1));
			chartData.pastTwoDays = getDetails(Duration.ofDays(2));
			chartData.pastWeek = getHourly(Duration.ofDays(7));
			chartData.pastMonth = getHourly(Duration.ofDays(30));

			Instant seasonStartTime = configuration.getChartSeasonStartTime();
			final Duration span = Duration.between(seasonStartTime, Instant.now());
			chartData.allTimeDaily = getDaily(span);

			updateJson();
		} catch (DataAccessException e) {
			// Let it loop and try again later.
			log.warn("Error when updating chart data.", e);
		}
	}

	private void updateJson() {
		try {
			log.info("Writing statistics to file.");
			ObjectMapper mapper = new ObjectMapper();
			File file = configuration.getChartDataFile();
			if (!file.exists()) {
				file.getParentFile().mkdir();
				file.createNewFile();
			}
			mapper.writeValue(file, chartData);
			log.info("File updated: " + file);
		} catch (JsonProcessingException e) {
			log.error("Error when processing JSON data.", e);
		} catch (IOException e) {
			log.error("Error when writing chart data file.", e);
		}
	}

	private ChartDatasetGroup getDetails(Duration amount) {
		List<StatsRow> stats = statisticsDAO.detailStats(amount);

		ChartDatasetGroup group = new ChartDatasetGroup();

		for (StatsRow row : stats) {
			group.labels.add(row.timeOfDay());
			group.waterInTemp.add(row.waterInTemp);
			group.waterOutTemp.add(row.waterOutTemp);
			group.compressorCurrent.add(row.compressorCurrent);
			group.compressorFrequency.add(row.compressorFrequency);
			group.eevSteps.add(row.eevSteps);
			group.fanSpeed.add(row.fanSpeed);
			group.heatingPipeTemp.add(row.heatingPipeTemp);
			group.exhaustTemp.add(row.exhaustTemp);
			group.ambientTemp.add(row.ambientTemp);
		}

		return group;
	}

	private ChartDatasetGroup getHourly(Duration amount) {
		List<StatsRow> stats = statisticsDAO.hourlyStats(amount);

		ChartDatasetGroup group = new ChartDatasetGroup();

		for (StatsRow row : stats) {
			group.labels.add(row.dateHour());
			group.waterInTemp.add(row.waterInTemp);
			group.waterOutTemp.add(row.waterOutTemp);
			group.compressorCurrent.add(row.compressorCurrent);
			group.exhaustTemp.add(row.exhaustTemp);
			group.ambientTemp.add(row.ambientTemp);
		}

		return group;
	}

	private ChartDatasetGroup getDaily(Duration amount) {
		List<StatsRow> stats = statisticsDAO.dailyStats(amount);
		List<PowerStatsRow> powerStats = statisticsDAO.powerStats(amount);

		ChartDatasetGroup group = new ChartDatasetGroup();

		for (StatsRow row : stats) {
			group.labels.add(row.date());
			group.waterInTemp.add(row.waterInTemp);
			group.waterOutTemp.add(row.waterOutTemp);
			group.compressorCurrent.add(row.compressorCurrent);
			group.exhaustTemp.add(row.exhaustTemp);
			group.ambientTemp.add(row.ambientTemp);
		}

		for (PowerStatsRow row : powerStats) {
			group.ah.add(row.ah);
			group.kwh.add(row.kwh);
		}

		return group;
	}

	public void stop() {
		running = false;
	}
}
