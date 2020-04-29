package net.minecraft.server.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RconListener extends RconBase {
	private static final Logger SERVER_LOGGER = LogManager.getLogger();
	private final int port;
	private String hostname;
	private ServerSocket listener;
	private final String password;
	private final List<RconClient> clients = Lists.<RconClient>newArrayList();
	private final DedicatedServer server;

	public RconListener(DedicatedServer server) {
		super("RCON Listener");
		this.server = server;
		ServerPropertiesHandler serverPropertiesHandler = server.getProperties();
		this.port = serverPropertiesHandler.rconPort;
		this.password = serverPropertiesHandler.rconPassword;
		this.hostname = server.getHostname();
		if (this.hostname.isEmpty()) {
			this.hostname = "0.0.0.0";
		}
	}

	private void removeStoppedClients() {
		this.clients.removeIf(rconClient -> !rconClient.isRunning());
	}

	public void run() {
		SERVER_LOGGER.info("RCON running on {}:{}", this.hostname, this.port);

		try {
			while (this.running) {
				try {
					Socket socket = this.listener.accept();
					RconClient rconClient = new RconClient(this.server, this.password, socket);
					rconClient.start();
					this.clients.add(rconClient);
					this.removeStoppedClients();
				} catch (SocketTimeoutException var7) {
					this.removeStoppedClients();
				} catch (IOException var8) {
					if (this.running) {
						SERVER_LOGGER.info("IO exception: ", (Throwable)var8);
					}
				}
			}
		} finally {
			this.closeSocket(this.listener);
		}
	}

	@Override
	public void start() {
		if (this.password.isEmpty()) {
			SERVER_LOGGER.warn("No rcon password set in server.properties, rcon disabled!");
		} else if (0 >= this.port || 65535 < this.port) {
			SERVER_LOGGER.warn("Invalid rcon port {} found in server.properties, rcon disabled!", this.port);
		} else if (!this.running) {
			try {
				this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
				this.listener.setSoTimeout(500);
				super.start();
			} catch (IOException var2) {
				SERVER_LOGGER.warn("Unable to initialise rcon on {}:{}", this.hostname, this.port, var2);
			}
		}
	}

	@Override
	public void stop() {
		this.running = false;
		this.closeSocket(this.listener);
		super.stop();

		for (RconClient rconClient : this.clients) {
			if (rconClient.isRunning()) {
				rconClient.stop();
			}
		}

		this.clients.clear();
	}

	private void closeSocket(ServerSocket socket) {
		SERVER_LOGGER.debug("closeSocket: {}", socket);

		try {
			socket.close();
		} catch (IOException var3) {
			SERVER_LOGGER.warn("Failed to close socket", (Throwable)var3);
		}
	}
}
