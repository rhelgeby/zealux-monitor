package no.helgeby.zealux.net;

public interface PacketListener {

	void onPacketSend(Packet packet);

	void onPacketReceived(Packet packet);

}
