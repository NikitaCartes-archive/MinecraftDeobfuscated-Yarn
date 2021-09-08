package net.minecraft.server.chase;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Locale;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChaseServer {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int port;
	private final PlayerManager playerManager;
	private final int interval;
	private boolean running = false;
	private ServerSocket socket;

	public ChaseServer(int port, PlayerManager playerManager, int interval) {
		this.port = port;
		this.playerManager = playerManager;
		this.interval = interval;
	}

	public void start() throws IOException {
		if (this.socket != null && !this.socket.isClosed()) {
			LOGGER.warn("Remote control server was asked to start, but it is already running. Will ignore.");
		} else {
			this.running = true;
			this.socket = new ServerSocket(this.port);
			new Thread(this::run).start();
		}
	}

	public void stop() {
		this.running = false;
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (IOException var2) {
				LOGGER.error("Failed to close remote control server socket", (Throwable)var2);
			}

			this.socket = null;
		}
	}

	public void run() {
		try {
			while (this.running) {
				LOGGER.info("Remote control server is listening for connections on port " + this.port);
				Socket socket = this.socket.accept();
				LOGGER.info("Remote control server received client connection on port " + socket.getPort());
				new Thread(() -> this.streamCommand(socket)).start();
			}
		} catch (ClosedByInterruptException var12) {
			if (this.running) {
				LOGGER.info("Remote control server closed by interrupt");
			}
		} catch (IOException var13) {
			if (this.running) {
				LOGGER.error("Remote control server closed because of an IO exception", (Throwable)var13);
			}
		} finally {
			if (this.socket != null && !this.socket.isClosed()) {
				try {
					this.socket.close();
				} catch (IOException var11) {
					LOGGER.warn("Failed to close remote control server socket", (Throwable)var11);
				}
			}
		}

		LOGGER.info("Remote control server is now stopped");
		this.running = false;
	}

	private void streamCommand(Socket socket) {
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

			while (this.running) {
				Thread.sleep((long)this.interval);
				this.writeCommandToStream(dataOutputStream);
			}
		} catch (InterruptedException var13) {
			LOGGER.info("Remote control client broadcast socket was interrupted and will be closed");
		} catch (IOException var14) {
			LOGGER.info("Remote control client broadcast socket got an IO exception and will be closed", (Throwable)var14);
		} finally {
			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (IOException var12) {
				LOGGER.warn("Failed to close remote control client socket", (Throwable)var12);
			}
		}

		LOGGER.info("Closed connection to remote control client");
	}

	private void writeCommandToStream(DataOutputStream stream) throws IOException {
		List<ServerPlayerEntity> list = this.playerManager.getPlayerList();
		if (!list.isEmpty()) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
			String string = String.format(
				Locale.ROOT,
				"/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
				serverPlayerEntity.world.getRegistryKey().getValue(),
				serverPlayerEntity.getX(),
				serverPlayerEntity.getY(),
				serverPlayerEntity.getZ(),
				serverPlayerEntity.getYaw(),
				serverPlayerEntity.getPitch()
			);
			stream.writeUTF(string);
		}
	}
}
