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

public class RconServer extends RconBase {
	private static final Logger field_23966 = LogManager.getLogger();
	private final int port;
	private String hostname;
	private ServerSocket listener;
	private final String password;
	private final List<RconClient> clients = Lists.<RconClient>newArrayList();
	private final DedicatedServer field_23967;

	public RconServer(DedicatedServer dedicatedServer) {
		super("RCON Listener");
		this.field_23967 = dedicatedServer;
		ServerPropertiesHandler serverPropertiesHandler = dedicatedServer.getProperties();
		this.port = serverPropertiesHandler.rconPort;
		this.password = serverPropertiesHandler.rconPassword;
		this.hostname = dedicatedServer.getHostname();
		if (this.hostname.isEmpty()) {
			this.hostname = "0.0.0.0";
		}
	}

	private void removeStoppedClients() {
		this.clients.removeIf(rconClient -> !rconClient.isRunning());
	}

	public void run() {
		field_23966.info("RCON running on {}:{}", this.hostname, this.port);

		try {
			while (this.running) {
				try {
					Socket socket = this.listener.accept();
					RconClient rconClient = new RconClient(this.field_23967, this.password, socket);
					rconClient.start();
					this.clients.add(rconClient);
					this.removeStoppedClients();
				} catch (SocketTimeoutException var7) {
					this.removeStoppedClients();
				} catch (IOException var8) {
					if (this.running) {
						field_23966.info("IO exception: ", (Throwable)var8);
					}
				}
			}
		} finally {
			this.method_27176(this.listener);
		}
	}

	@Override
	public void start() {
		if (this.password.isEmpty()) {
			field_23966.warn("No rcon password set in server.properties, rcon disabled!");
		} else if (0 >= this.port || 65535 < this.port) {
			field_23966.warn("Invalid rcon port {} found in server.properties, rcon disabled!", this.port);
		} else if (!this.running) {
			try {
				this.listener = new ServerSocket(this.port, 0, InetAddress.getByName(this.hostname));
				this.listener.setSoTimeout(500);
				super.start();
			} catch (IOException var2) {
				field_23966.warn("Unable to initialise rcon on {}:{}", this.hostname, this.port, var2);
			}
		}
	}

	@Override
	public void stop() {
		this.running = false;
		this.method_27176(this.listener);
		super.stop();

		for (RconClient rconClient : this.clients) {
			if (rconClient.isRunning()) {
				rconClient.stop();
			}
		}

		this.clients.clear();
	}

	private void method_27176(ServerSocket serverSocket) {
		field_23966.debug("closeSocket: {}", serverSocket);

		try {
			serverSocket.close();
		} catch (IOException var3) {
			field_23966.warn("Failed to close socket", (Throwable)var3);
		}
	}
}
