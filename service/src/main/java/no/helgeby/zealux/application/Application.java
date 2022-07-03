package no.helgeby.zealux.application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.helgeby.zealux.controller.ConnectionManager;
import no.helgeby.zealux.controller.Parameter;
import no.helgeby.zealux.controller.ParameterDescription;
import no.helgeby.zealux.controller.ParameterDescriptions;
import no.helgeby.zealux.controller.ReportWriter;
import no.helgeby.zealux.net.ObjectHeader;
import no.helgeby.zealux.net.ParameterBlock;
import no.helgeby.zealux.net.StatusResponse;

public class Application {

	private static final Logger log = LogManager.getLogger(Application.class);

	private ConnectionManager connectionManager;
	private Configuration configuration;
	private Queue<StatusResponse> responseQueue;

	private Thread connectionThread;
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		Options options = new Options();
		Option configOption = Option.builder("c") //
				.longOpt("config") //
				.argName("configFile") //
				.hasArg() //
				.required() //
				.desc("Properties file with application settings.") //
				.build();
		options.addOption(configOption);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter helper = new HelpFormatter();

		try {
			CommandLine cmd = parser.parse(options, args);
			File configFile = new File(cmd.getOptionValue(configOption));
			Configuration configuration = new Configuration(configFile);
			new Application(configuration);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			helper.printHelp("java -jar zealux-monitor.jar [OPTION]...", options);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.warn("Error when parsing configuration file.", e);
		} catch (StartupException e) {
			System.out.println(e.getMessage());
			log.warn("Error when starting application.", e);
		}
	}

	public Application(Configuration configuration) throws StartupException {
		this.configuration = configuration;
		startConnectionThread();
		createShutdownHook();

		if (configuration.isDatabaseEnabled()) {
			this.dataSource = createDataSource();
			this.jdbcTemplate = createJdbcTemplate();
			updateDatabase();
		}

		responseCollectorLoop();
	}

	private void startConnectionThread() {
		log.info("Starting connection thread.");
		connectionManager = new ConnectionManager(configuration);
		connectionThread = new Thread(connectionManager, "connection");
		connectionThread.start();

		responseQueue = connectionManager.getResponseQueue();
	}

	private DataSource createDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(configuration.getDatabaseUrl());
		dataSource.setUsername(configuration.getDatabaseUsername());
		dataSource.setPassword(configuration.getDatabasePassword());
		dataSource.setDefaultSchema(configuration.getDatabaseSchema());
		return dataSource;
	}

	private JdbcTemplate createJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	private void updateDatabase() throws StartupException {
		try {
			Connection connection = dataSource.getConnection();
			ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
			JdbcConnection jdbcConnection = new JdbcConnection(connection);
			final String changeLogFile = "dbchangelog.xml";

			try (Liquibase liquibase = new Liquibase(changeLogFile, resourceAccessor, jdbcConnection)) {
				liquibase.update(new Contexts());
			}
		} catch (SQLException e) {
			throw new StartupException("Error when getting database connection.", e);
		} catch (LiquibaseException e) {
			throw new StartupException("Error when updating database.", e);
		}
	}

	private void responseCollectorLoop() {
		log.info("Starting response collector loop.");
		while (true) {
			try {
				StatusResponse statusResponse;
				List<StatusResponse> responses = new ArrayList<>();
				do {
					statusResponse = responseQueue.poll();
					if (statusResponse != null) {
						responses.add(statusResponse);
					}
				} while (statusResponse != null);
				if (!responses.isEmpty()) {
					processResponses(responses);
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private void processResponses(List<StatusResponse> responses) {
		log.info("Processing " + responses.size() + " responses.");

		try {
			for (StatusResponse response : responses) {
				if (configuration.isDatabaseStatsInsertionEnabled()) {
					List<Parameter> parameters = getParameters(response);
					Instant probeTime = response.getTimestamp();

					List<StatusRow> statusRows = getStatusRows(parameters, probeTime);
					List<ConfigRow> configRows = getConfigRows(parameters, probeTime);

					insertStatusRows(statusRows);
					insertConfigRows(configRows);
				} else {
					log.info("Database insertions disabled, showing report instead.");
					String report = ReportWriter.prettyPrint(response);
					log.info("Status report:\n" + report);
				}
			}
		} catch (DataAccessException e) {
			log.error("Error when inserting responses.", e);
		}
	}

	private List<StatusRow> getStatusRows(List<Parameter> parameters, Instant probeTime) {
		List<StatusRow> statusRows = new ArrayList<>();

		for (Parameter parameter : parameters) {
			if (parameter.getParameterDescription().type != ObjectHeader.SUB_TYPE_STATUS) {
				continue;
			}
			statusRows.add(new StatusRow(parameter, probeTime));
		}

		return statusRows;
	}

	private List<ConfigRow> getConfigRows(List<Parameter> parameters, Instant probeTime) {
		List<ConfigRow> configRows = new ArrayList<>();

		for (Parameter parameter : parameters) {
			if (parameter.getParameterDescription().type != ObjectHeader.SUB_TYPE_STATUS) {
				continue;
			}
			configRows.add(new ConfigRow(parameter, probeTime));
		}

		return configRows;
	}

	private void insertStatusRows(List<StatusRow> statusRows) {
		if (statusRows.isEmpty()) {
			return;
		}

		String sql = "INSERT INTO status (probe_time, index, value) VALUES (?, ?, ?)";

		int size = statusRows.size();
		log.info("Inserting " + size + " status values.");

		jdbcTemplate.batchUpdate(sql, statusRows, size, new ParameterizedPreparedStatementSetter<StatusRow>() {
			@Override
			public void setValues(PreparedStatement ps, StatusRow argument) throws SQLException {
				ps.setTimestamp(1, argument.getProbeTime());
				ps.setInt(2, argument.getIndex());
				ps.setObject(3, argument.getValue(), Types.NUMERIC);
			}
		});

		log.info("Status values inserted.");
	}

	private void insertConfigRows(List<ConfigRow> configRows) {
		if (configRows.isEmpty()) {
			return;
		}

		String sql = "INSERT INTO config (probe_time, index, value) VALUES (?, ?, ?)";

		int size = configRows.size();
		log.info("Inserting " + size + " config values.");

		jdbcTemplate.batchUpdate(sql, configRows, size, new ParameterizedPreparedStatementSetter<ConfigRow>() {
			@Override
			public void setValues(PreparedStatement ps, ConfigRow argument) throws SQLException {
				ps.setTimestamp(1, argument.getProbeTime());
				ps.setInt(2, argument.getIndex());
				ps.setObject(3, argument.getValue(), Types.NUMERIC);
			}
		});

		log.info("Config values inserted.");
	}

	private List<Parameter> getParameters(StatusResponse response) {
		List<Parameter> parameters = new ArrayList<>();
		List<ParameterBlock> parameterBlocks = response.getParameterBlocks();
		for (ParameterBlock block : parameterBlocks) {
			ObjectHeader header = block.getObjectHeader();
			short subType = header.getSubType();
			short startIndex = block.getStartIndex();
			short[] values = block.getValues();
			for (int i = 0; i < values.length; i++) {
				int index = startIndex + i;
				ParameterDescription description = ParameterDescriptions.getParameter(index, subType);
				if (ParameterDescriptions.isUnknown(description)) {
					continue;
				}

				parameters.add(new Parameter(index, values[i], description));
			}

		}
		return parameters;
	}

	private void createShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				connectionManager.stop();
				connectionThread.interrupt();
			}
		}, "Shutdown"));
	}
}
