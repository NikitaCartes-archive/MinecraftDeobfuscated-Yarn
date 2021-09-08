package net.minecraft.server.chase;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChaseClient {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_34353 = 5;
	private final String ip;
	private final int port;
	private final MinecraftServer minecraftServer;
	private boolean running;
	private Socket socket;
	private Thread thread;

	public ChaseClient(String ip, int port, MinecraftServer minecraftServer) {
		this.ip = ip;
		this.port = port;
		this.minecraftServer = minecraftServer;
	}

	public void start() {
		if (this.thread != null && this.thread.isAlive()) {
			LOGGER.warn("Remote control client was asked to start, but it is already running. Will ignore.");
		}

		this.running = true;
		this.thread = new Thread(this::run);
		this.thread.start();
	}

	public void stop() {
		this.running = false;
		if (this.socket != null && !this.socket.isClosed()) {
			try {
				this.socket.close();
			} catch (IOException var2) {
				LOGGER.warn("Failed to close socket to remote control server", (Throwable)var2);
			}
		}

		this.socket = null;
		this.thread = null;
	}

	public void run() {
		String string = this.ip + ":" + this.port;

		while (this.running) {
			try {
				LOGGER.info("Connecting to remote control server " + string);
				this.socket = new Socket(this.ip, this.port);
				LOGGER.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");

				try {
					DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());

					while (this.running) {
						String string2 = dataInputStream.readUTF();
						this.executeCommand(string2);
					}
				} catch (IOException var5) {
					LOGGER.warn("Lost connection to remote control server " + string + ". Will retry in 5s.");
				}
			} catch (IOException var6) {
				LOGGER.warn("Failed to connect to remote control server " + string + ". Will retry in 5s.");
			}

			if (this.running) {
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException var4) {
				}
			}
		}
	}

	private void executeCommand(String command) {
		List<ServerPlayerEntity> list = this.minecraftServer.getPlayerManager().getPlayerList();
		if (!list.isEmpty()) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
			ServerWorld serverWorld = this.minecraftServer.getOverworld();
			ServerCommandSource serverCommandSource = new ServerCommandSource(
				serverPlayerEntity, Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "", LiteralText.EMPTY, this.minecraftServer, serverPlayerEntity
			);
			CommandManager commandManager = this.minecraftServer.getCommandManager();
			commandManager.execute(serverCommandSource, command);
		}
	}
}
