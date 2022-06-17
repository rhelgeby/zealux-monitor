package no.helgeby.zealux.experimental;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.helgeby.zealux.application.Configuration;
import no.helgeby.zealux.controller.ConnectionManager;
import no.helgeby.zealux.controller.ReportWriter;
import no.helgeby.zealux.controller.Session;
import no.helgeby.zealux.net.StatusResponse;

public class Sandbox {

	private static final Logger log = LogManager.getLogger(Sandbox.class);

	public static void main(String[] args) {
		threaded();
//		simple();
	}

	public static void simple() {
		log.info("Initializing.");

		File configurationFile = new File("dev-configuration.properties");
		Configuration configuration;
		try {
			configuration = new Configuration(configurationFile);
		} catch (IOException e) {
			log.error("Error when reading configuration.", e);
			return;
		}

		InetSocketAddress address = configuration.getAddress();
		log.info("Address: " + address);

		String password = configuration.getPassword();
		long serialNumber = configuration.getSerialNumber();
		Duration responseTimeout = configuration.getResponseTimeout();
		int maxRetries = configuration.getMaxAttemptsPerPacket();
		boolean debugPacketDetails = configuration.isPacketInfoDumpEnabled();

		PacketLogger packetLogger = new PacketLogger(new File("packetLog"));
		Session.setListener(packetLogger);

		try (Session session = new Session(address, serialNumber, password, responseTimeout, maxRetries,
				debugPacketDetails)) {
			StatusResponse statusResponse = session.queryStatus();
			String report = ReportWriter.prettyPrint(statusResponse);
			log.info("Report\n" + report);

			log.info("Stopping.");
		} catch (Exception e) {
			log.error("Unhandled exception, stopping.", e);
		}
	}

	public static void threaded() {
		log.info("Initializing.");
		File configurationFile = new File("dev-configuration.properties");
		Configuration configuration;
		try {
			configuration = new Configuration(configurationFile);
		} catch (IOException e) {
			log.error("Error when reading configuration.", e);
			return;
		}

		ConnectionManager connectionManager = new ConnectionManager(configuration);
		Thread connectionThread = new Thread(connectionManager, "ConnectionManager");
		connectionThread.start();

		Queue<StatusResponse> responseQueue = connectionManager.getResponseQueue();
		int numResponses = 0;
		while (true) {
			try {
				StatusResponse statusResponse = responseQueue.poll();
				if (statusResponse != null) {
					numResponses++;
					String report = ReportWriter.prettyPrint(statusResponse);
					log.info("Report (" + numResponses + " responses)\n" + report);
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
