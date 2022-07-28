package no.helgeby.zealux.application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Configuration {

	private Properties properties = new Properties();

	private InetSocketAddress address;
	private long serialNumber;
	private String password;

	private Duration responseTimeout;
	private Duration offlineTimeout;
	private int maxAttemptsPerPacket;
	private Duration mainPollingInterval;
	private Duration degradedPollingInterval;
	private Duration offlinePollingInterval;

	private boolean databaseEnabled;
	private boolean databaseStatsInsertionEnabled;
	private String databaseDriverClassName;
	private String databaseUrl;
	private String databaseUsername;
	private String databasePassword;
	private String databaseSchema;

	private Duration chartDataUpdateInterval;
	private File chartDataFile;

	private boolean debugPacketInfoDump;

	public Configuration(File file) throws IOException {
		properties = new Properties();

		FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
		properties.load(fileReader);

		String host = getString("device.host");
		int port = getInt("device.port");
		address = new InetSocketAddress(host, port);
		serialNumber = getLong("device.serialNumber");
		password = getString("device.password");

		responseTimeout = getDurationMillis("net.responseTimeoutMillis");
		offlineTimeout = getDurationMinutes("net.offlineTimeoutMinutes");
		maxAttemptsPerPacket = getInt("net.maxAttemptsPerPacket");
		mainPollingInterval = getDurationMinutes("net.mainPollingIntervalMinutes");
		degradedPollingInterval = getDurationMinutes("net.degradedPollingIntervalMinutes");
		offlinePollingInterval = getDurationMinutes("net.offlinePollingIntervalMinutes");

		databaseEnabled = getBoolean("database.enabled");
		if (databaseEnabled) {
			databaseStatsInsertionEnabled = getBoolean("database.databaseStatsInsertionEnabled");
			databaseDriverClassName = getString("database.driverClassName");
			databaseUrl = getString("database.url");
			databaseUsername = getString("database.username");
			databasePassword = getString("database.password");
			databaseSchema = getString("database.schema");
		}

		chartDataUpdateInterval = getDurationMinutes("chart.updateInterval");
		chartDataFile = getFile("chart.dataFile");

		debugPacketInfoDump = getBoolean("debug.packetInfoDump");
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public long getSerialNumber() {
		return serialNumber;
	}

	public String getPassword() {
		return password;
	}

	public Duration getResponseTimeout() {
		return responseTimeout;
	}

	public Duration getOfflineTimeout() {
		return offlineTimeout;
	}

	public int getMaxAttemptsPerPacket() {
		return maxAttemptsPerPacket;
	}

	public Duration getMainPollingInterval() {
		return mainPollingInterval;
	}

	public Duration getDegradedPollingInterval() {
		return degradedPollingInterval;
	}

	public Duration getOfflinePollingInterval() {
		return offlinePollingInterval;
	}

	public boolean isDatabaseEnabled() {
		return databaseEnabled;
	}

	public boolean isDatabaseStatsInsertionEnabled() {
		return databaseStatsInsertionEnabled;
	}

	public String getDatabaseDriverClassName() {
		return databaseDriverClassName;
	}

	public String getDatabaseUrl() {
		return databaseUrl;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public Duration getChartDataUpdateInterval() {
		return chartDataUpdateInterval;
	}

	public boolean isChartUpdateEnabled() {
		return isDatabaseEnabled() && !chartDataUpdateInterval.isZero() && chartDataFile != null;
	}

	public File getChartDataFile() {
		return chartDataFile;
	}

	public boolean isPacketInfoDumpEnabled() {
		return debugPacketInfoDump;
	}

	private String getString(String key) {
		String value = properties.getProperty(key);
		if (StringUtils.isBlank(value)) {
			throw new IllegalArgumentException("Property '" + key + "' is required.");
		}
		return value;
	}

	private int getInt(String key) {
		String stringValue = getString(key);
		if (!NumberUtils.isDigits(stringValue)) {
			throw new IllegalArgumentException("Property '" + key + "' must be an integer.");
		}
		return Integer.valueOf(stringValue);
	}

	private long getLong(String key) {
		String stringValue = getString(key);
		if (!NumberUtils.isDigits(stringValue)) {
			throw new IllegalArgumentException("Property '" + key + "' must be a long.");
		}
		return Long.valueOf(stringValue);
	}

	private boolean getBoolean(String key) {
		String stringValue = getString(key);
		return BooleanUtils.toBoolean(stringValue);
	}

	private Duration getDurationMillis(String key) {
		long value = getLong(key);
		return Duration.ofMillis(value);
	}

	private Duration getDurationMinutes(String key) {
		long value = getLong(key);
		return Duration.ofMinutes(value);
	}

	private File getFile(String key) {
		String path = getString(key);
		File file = new File(path);
		if (file.isDirectory()) {
			throw new IllegalArgumentException("Property '" + key + "' must be a path to a file.");
		}
		return file;
	}
}
