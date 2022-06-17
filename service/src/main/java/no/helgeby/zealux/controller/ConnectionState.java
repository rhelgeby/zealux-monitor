package no.helgeby.zealux.controller;

public enum ConnectionState {

	/**
	 * Not connected.
	 */
	DISCONNECTED,

	/**
	 * Connected and has a recent response.
	 */
	CONNECTED,

	/**
	 * Connected, but no recent response. Slow down the polling interval.
	 */
	DEGRADED,

	/**
	 * Degraded for too long. Slowing down polling interval even more.
	 */
	OFFLINE

}
