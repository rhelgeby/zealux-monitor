package no.helgeby.zealux.controller;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.helgeby.zealux.net.AuthChallenge;
import no.helgeby.zealux.net.AuthIntro;
import no.helgeby.zealux.net.AuthResponse;
import no.helgeby.zealux.net.ObjectHeader;
import no.helgeby.zealux.net.Packet;
import no.helgeby.zealux.net.PacketHeader;
import no.helgeby.zealux.net.PacketListener;
import no.helgeby.zealux.net.StatusQuery;
import no.helgeby.zealux.net.StatusResponse;

public class Session implements AutoCloseable, Closeable {

	private static final Logger log = LogManager.getLogger(Session.class);

	private static PacketListener listener = null;

	private InetSocketAddress target;
	private long serialNumber;
	private String password;
	private int maxRetries;

	private int clientSessionId;
	private int deviceId;
	private DatagramSocket socket;

	private boolean debugPacketDetails;

	public Session(InetSocketAddress target, long serialNumber, String password, Duration responseTimeout,
			int maxRetries, boolean debugPacketDetails) throws IOException {
		this.target = target;
		this.serialNumber = serialNumber;
		this.password = password;
		this.maxRetries = maxRetries;
		this.debugPacketDetails = debugPacketDetails;

		socket = new DatagramSocket();
		socket.setSoTimeout((int) responseTimeout.toMillis());
		connect();
	}

	private void connect() throws IOException {
		log.info("Authenticating.");
		int clientToken = createClientToken();
		AuthIntro auth = new AuthIntro(clientToken, serialNumber);
		logPacketDetails("Auth intro:\n" + auth);
		Packet authIntro = auth.toPacket(target);

		logPacketDetails("Sending auth packet:\n" + authIntro);
		Packet authResponse = sendAndReceive(authIntro, maxRetries);
		logPacketDetails("Response received:\n" + authResponse);

		AuthChallenge authChallenge = new AuthChallenge(authResponse);
		logPacketDetails("Auth challenge: " + authChallenge);

		clientSessionId = authChallenge.getClientSessionId();
		deviceId = authChallenge.getDeviceId();

		AuthResponse challengeResponse = authChallenge.createResponse(password, clientToken);
		logPacketDetails("Auth response: " + challengeResponse);
		Packet challengeResponsePacket = challengeResponse.toPacket(target);

		logPacketDetails("Sending challenge response packet:\n" + challengeResponsePacket);
		Packet packet = sendAndReceive(challengeResponsePacket, maxRetries);
		logPacketDetails("Challenge response received:\n" + packet);

		PacketHeader headersReceived = new PacketHeader(packet);
		logPacketDetails("Headers:\n" + headersReceived);

		// TODO: Check auth response.
		// Now it assumes given serial number and password is correct.
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	public StatusResponse queryStatus() throws IOException {
		StatusQuery statusQuery = new StatusQuery(ObjectHeader.SUB_TYPE_QUERY_ALL, clientSessionId, deviceId);
		logPacketDetails("Status query: " + statusQuery);

		Packet statusQueryPacket = statusQuery.toPacket(target);
		logPacketDetails("Status query packet:\n" + statusQueryPacket);
		Packet statusResponsePacket = sendAndReceive(statusQueryPacket, maxRetries);
		logPacketDetails("Status response received:\n" + statusResponsePacket);
		return new StatusResponse(statusResponsePacket);
	}

	private int createClientToken() {
		Random random = new Random();
		return random.nextInt();
	}

	private Packet sendAndReceive(Packet packet, int maxAttempts) throws IOException {
		int attempt = 1;

		Packet response = new Packet();
		do {
			try {
				if (listener != null) {
					listener.onPacketSend(packet);
				}
				socket.send(packet.datagramPacket());

				socket.receive(response.datagramPacket());
				if (listener != null) {
					listener.onPacketReceived(response);
				}

				return response;
			} catch (SocketTimeoutException e) {
				if (attempt >= maxAttempts) {
					log.error("Giving up. No response after " + attempt + " attempts.");
					throw e;
				}
				attempt++;
				log.info("Timed out. Trying again. Attempt " + attempt + " of " + maxAttempts + ".");
			}
		} while (attempt <= maxAttempts);

		throw new IllegalStateException("Completed loop without returning or throwing. Should not happen.");
	}

	private void logPacketDetails(String message) {
		if (debugPacketDetails) {
			log.info(message);
		}
	}

	public static void setListener(PacketListener listener) {
		Session.listener = listener;
	}

	public static void removeListener() {
		Session.listener = null;
	}
}
