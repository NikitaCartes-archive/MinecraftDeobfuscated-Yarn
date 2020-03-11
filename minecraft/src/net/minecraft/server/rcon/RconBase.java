package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.logging.UncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RconBase implements Runnable {
	private static final Logger field_14430 = LogManager.getLogger();
	private static final AtomicInteger field_14428 = new AtomicInteger(0);
	protected boolean running;
	protected final DedicatedServer server;
	protected final String description;
	protected Thread thread;
	protected final int field_14427 = 5;
	protected final List<DatagramSocket> sockets = Lists.<DatagramSocket>newArrayList();
	protected final List<ServerSocket> serverSockets = Lists.<ServerSocket>newArrayList();

	protected RconBase(DedicatedServer dedicatedServer, String string) {
		this.server = dedicatedServer;
		this.description = string;
		if (this.server.isDebuggingEnabled()) {
			this.warn("Debugging is enabled, performance maybe reduced!");
		}
	}

	public synchronized void start() {
		this.thread = new Thread(this, this.description + " #" + field_14428.incrementAndGet());
		this.thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler(field_14430));
		this.thread.start();
		this.running = true;
	}

	public synchronized void stop() {
		this.running = false;
		if (null != this.thread) {
			int i = 0;

			while (this.thread.isAlive()) {
				try {
					this.thread.join(1000L);
					if (5 <= ++i) {
						this.warn("Waited " + i + " seconds attempting force stop!");
						this.forceClose(true);
					} else if (this.thread.isAlive()) {
						this.warn("Thread " + this + " (" + this.thread.getState() + ") failed to exit after " + i + " second(s)");
						this.warn("Stack:");

						for (StackTraceElement stackTraceElement : this.thread.getStackTrace()) {
							this.warn(stackTraceElement.toString());
						}

						this.thread.interrupt();
					}
				} catch (InterruptedException var6) {
				}
			}

			this.forceClose(true);
			this.thread = null;
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	protected void log(String string) {
		this.server.log(string);
	}

	protected void info(String string) {
		this.server.info(string);
	}

	protected void warn(String string) {
		this.server.warn(string);
	}

	protected void logError(String string) {
		this.server.logError(string);
	}

	protected int getCurrentPlayerCount() {
		return this.server.getCurrentPlayerCount();
	}

	protected void registerSocket(DatagramSocket datagramSocket) {
		this.log("registerSocket: " + datagramSocket);
		this.sockets.add(datagramSocket);
	}

	protected boolean closeSocket(DatagramSocket socket, boolean bl) {
		this.log("closeSocket: " + socket);
		if (null == socket) {
			return false;
		} else {
			boolean bl2 = false;
			if (!socket.isClosed()) {
				socket.close();
				bl2 = true;
			}

			if (bl) {
				this.sockets.remove(socket);
			}

			return bl2;
		}
	}

	protected boolean closeSocket(ServerSocket serverSocket) {
		return this.closeSocket(serverSocket, true);
	}

	protected boolean closeSocket(ServerSocket socket, boolean bl) {
		this.log("closeSocket: " + socket);
		if (null == socket) {
			return false;
		} else {
			boolean bl2 = false;

			try {
				if (!socket.isClosed()) {
					socket.close();
					bl2 = true;
				}
			} catch (IOException var5) {
				this.warn("IO: " + var5.getMessage());
			}

			if (bl) {
				this.serverSockets.remove(socket);
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
