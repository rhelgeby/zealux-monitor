package no.helgeby.zealux.experimental;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import no.helgeby.zealux.net.Packet;
import no.helgeby.zealux.net.PacketListener;

public class PacketLogger implements PacketListener {

	private static final Logger log = LogManager.getLogger(PacketLogger.class);

	private File basePath;
	private File todaysFolder;

	public PacketLogger(File basePath) {
		if (basePath.exists() && !basePath.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory: " + basePath);
		}
		if (!basePath.exists() && !basePath.mkdirs()) {
			throw new IllegalArgumentException("Could not create directories for: " + basePath);
		}
		this.basePath = basePath;
		initTodaysFolder();
	}

	private void initTodaysFolder() {
		LocalDate now = LocalDate.now();
		String isoDate = now.toString();
		File todaysFolder = new File(basePath, isoDate + File.separatorChar);
		if (!todaysFolder.exists()) {
			todaysFolder.mkdir();
		}
		this.todaysFolder = todaysFolder;
	}

	@Override
	public void onPacketSend(Packet packet) {
		File packetFile = getFile(false);
		writePacketBytes(packetFile, packet);
	}

	@Override
	public void onPacketReceived(Packet packet) {
		File packetFile = getFile(true);
		writePacketBytes(packetFile, packet);
	}

	private File getFile(boolean response) {
		LocalTime time = LocalTime.now();
		String fileName = time.toString();
		fileName = fileName.replace(':', '-');
		if (response) {
			fileName += "-response.packet";
		} else {
			fileName += "-request.packet";
		}
		return new File(todaysFolder, fileName);
	}

	private void writePacketBytes(File file, Packet packet) {
		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(packet.getData());
		} catch (FileNotFoundException e) {
			log.warn(e.getMessage());
		} catch (IOException e) {
			log.warn("Could not write packet file.", e);
		}
	}
}
