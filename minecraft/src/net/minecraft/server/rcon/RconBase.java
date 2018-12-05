package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.UncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RconBase implements Runnable {
	private static final Logger field_14430 = LogManager.getLogger();
	private static final AtomicInteger field_14428 = new AtomicInteger(0);
	protected boolean running;
	protected DedicatedServer field_14425;
	protected final String description;
	protected Thread thread;
	protected int field_14427 = 5;
	protected List<DatagramSocket> sockets = Lists.<DatagramSocket>newArrayList();
	protected List<ServerSocket> serverSockets = Lists.<ServerSocket>newArrayList();

	protected RconBase(DedicatedServer dedicatedServer, String string) {
		this.field_14425 = dedicatedServer;
		this.description = string;
		if (this.field_14425.isDebuggingEnabled()) {
			this.warn("Debugging is enabled, performance maybe reduced!");
		}
	}

	public synchronized void start() {
		this.thread = new Thread(this, this.description + " #" + field_14428.incrementAndGet());
		this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(field_14430));
		this.thread.start();
		this.running = true;
	}

	public boolean isRunning() {
		return this.running;
	}

	protected void log(String string) {
		this.field_14425.log(string);
	}

	protected void info(String string) {
		this.field_14425.info(string);
	}

	protected void warn(String string) {
		this.field_14425.warn(string);
	}

	protected void logError(String string) {
		this.field_14425.logError(string);
	}

	protected int getCurrentPlayerCount() {
		return this.field_14425.getCurrentPlayerCount();
	}

	protected void registerSocket(DatagramSocket datagramSocket) {
		this.log("registerSocket: " + datagramSocket);
		this.sockets.add(datagramSocket);
	}

	protected boolean closeSocket(DatagramSocket datagramSocket, boolean bl) {
		this.log("closeSocket: " + datagramSocket);
		if (null == datagramSocket) {
			return false;
		} else {
			boolean bl2 = false;
			if (!datagramSocket.isClosed()) {
				datagramSocket.close();
				bl2 = true;
			}

			if (bl) {
				this.sockets.remove(datagramSocket);
			}

			return bl2;
		}
	}

	protected boolean closeSocket(ServerSocket serverSocket) {
		return this.closeSocket(serverSocket, true);
	}

	protected boolean closeSocket(ServerSocket serverSocket, boolean bl) {
		this.log("closeSocket: " + serverSocket);
		if (null == serverSocket) {
			return false;
		} else {
			boolean bl2 = false;

			try {
				if (!serverSocket.isClosed()) {
					serverSocket.close();
					bl2 = true;
				}
			} catch (IOException var5) {
				this.warn("IO: " + var5.getMessage());
			}

			if (bl) {
				this.serverSockets.remove(serverSocket);
			}

			return bl2;
		}
	}

	protected void forceClose() {
		this.forceClose(false);
	}

	protected void forceClose(boolean bl) {
		int i = 0;

		for (DatagramSocket datagramSocket : this.sockets) {
			if (this.closeSocket(datagramSocket, false)) {
				i++;
			}
		}

		this.sockets.clear();

		for (ServerSocket serverSocket : this.serverSockets) {
			if (this.closeSocket(serverSocket, false)) {
				i++;
			}
		}

		this.serverSockets.clear();
		if (bl && 0 < i) {
			this.warn("Force closed " + i + " sockets");
		}
	}
}
