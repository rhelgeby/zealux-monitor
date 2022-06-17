package no.helgeby.zealux.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.helgeby.zealux.application.Configuration;
import no.helgeby.zealux.net.PacketHeader;
import no.helgeby.zealux.net.StatusResponse;

public class ConnectionManager implements Runnable {

	private static final Logger log = LogManager.getLogger(ConnectionManager.class);

	private Configuration configuration;
	private Session session;
	private ConnectionState state;
	private Instant lastResponse;
	private Duration mainPollingInterval;
	private Duration degradedPollingInterval;
	private Duration offlinePollingInterval;
	private boolean running;
	private int maxAttempts;
	private Duration responseTimeout;
	private Duration offlineTimeout;
	private ConcurrentLinkedQueue<StatusResponse> responseQueue;

	public ConnectionManager(Configuration configuration) {
		this.configuration = configuration;
		this.state = ConnectionState.DISCONNECTED;
		this.mainPollingInterval = configuration.getMainPollingInterval();
		this.degradedPollingInterval = configuration.getDegradedPollingInterval();
		this.offlinePollingInterval = configuration.getOfflinePollingInterval();
		this.maxAttempts = configuration.getMaxAttemptsPerPacket();
		this.responseTimeout = configuration.getResponseTimeout();
		this.offlineTimeout = configuration.getOfflineTimeout();
		this.responseQueue = new ConcurrentLinkedQueue<>();
	}

	private boolean connect() {
		if (session != null) {
			// Already connected.
			return true;
		}
		try {
			log.info("Connecting.");
			InetSocketAddress address = configuration.getAddress();
			long serialNumber = configuration.getSerialNumber();
			String password = configuration.getPassword();
			boolean packetInfoDump = configuration.isPacketInfoDumpEnabled();
			session = new Session(address, serialNumber, password, responseTimeout, maxAttempts, packetInfoDump);
			lastResponse = Instant.now();
			state = ConnectionState.CONNECTED;
			return true;
		} catch (IOException e) {
			disconnect();
			return false;
		}
	}

	@Override
	public void run() {
		running = true;
		while (running) {
			Duration loopInterval = updateConnectionState();

			boolean skipSleep = false;
			if (connect()) {
				skipSleep = pollStatus();
			} else {
				// Slow down a bit after failed re-connect.
				log.info("Not able to connect.");
				loopInterval = degradedPollingInterval;
			}

			try {
				if (!skipSleep) {
					log.info("Waiting: " + loopInterval);
					Thread.sleep(loopInterval.toMillis());
				}
			} catch (InterruptedException e) {
				log.warn("Thread was interrupted. Stopping.");
				running = false;
			}
		}
	}

	/**
	 * @return Whether it should immediately loop and re-try.
	 */
	private boolean pollStatus() {
		log.info("Polling status.");
		for (int i = 0; i < maxAttempts; i++) {
			try {
				StatusResponse statusResponse = session.queryStatus();

				byte action = statusResponse.getAction();
				if (action == PacketHeader.ACTION_CONNECTION_RESET) {
					log.info("Connection reset. Disconnecting.");
					disconnect();
					return true;
				}

				log.info("Status response received.");
				lastResponse = Instant.now();
				state = ConnectionState.CONNECTED;
				responseQueue.add(statusResponse);
				return false;
			} catch (IOException e) {
				if (i == maxAttempts - 1) {
					degradeState();
				}
			}
		}
		return false;
	}

	/**
	 * Determines the state of the current connection and returns the loop interval.
	 * 
	 * @return Loop interval to use on next iteration.
	 */
	private Duration updateConnectionState() {
		if (session == null) {
			state = ConnectionState.DISCONNECTED;
			log.debug("Not connected.");
			return mainPollingInterval;
		}

		switch (state) {
		case CONNECTED:
			if (isTimedOut(lastResponse, mainPollingInterval.multipliedBy(2))) {
				state = ConnectionState.DEGRADED;
				log.info("No response in a while. Degrading connection.");
				return degradedPollingInterval;
			} else {
				return mainPollingInterval;
			}
		case DEGRADED:
			if (isTimedOut(lastResponse, degradedPollingInterval.multipliedBy(2))) {
				state = ConnectionState.OFFLINE;
				log.info("Connection has been degraded for a while. Considering offline.");
				return offlinePollingInterval;
			} else {
				return degradedPollingInterval;
			}
		case DISCONNECTED:
			return mainPollingInterval;
		case OFFLINE:
			if (isTimedOut(lastResponse, offlineTimeout)) {
				disconnect();
				log.info("Offline for too long. Restart session.");
				return mainPollingInterval;
			}
			return offlinePollingInterval;
		}
		return mainPollingInterval;
	}

	/**
	 * Degrades states, but does not disconnect.
	 */
	private void degradeState() {
		if (state == ConnectionState.CONNECTED) {
			state = ConnectionState.DEGRADED;
			log.info("Connection state changed to degraded.");
		} else if (state == ConnectionState.DEGRADED) {
			state = ConnectionState.OFFLINE;
			log.info("Connection state changed to offline.");
		}
	}

	private void disconnect() {
		session = null;
		state = ConnectionState.DISCONNECTED;
		lastResponse = null;
	}

	private static boolean isTimedOut(Instant lastResponse, Duration timeout) {
		Instant timeoutSince = lastResponse.plus(timeout);
		return timeoutSince.isBefore(Instant.now());
	}

	public void stop() {
		running = false;
	}

	/**
	 * Gets the queue where status responses are stored.
	 * <p>
	 * Use another thread to poll this queue at a fixed interval, independent on the
	 * thread for the ConnectionManager. It will add status responses whenever it
	 * can.
	 * 
	 * @return Queue to poll status responses from.
	 */
	public Queue<StatusResponse> getResponseQueue() {
		return responseQueue;
	}
}
