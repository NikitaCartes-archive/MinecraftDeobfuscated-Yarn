package net.minecraft;

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

public class class_6486 {
	private static final Logger field_34352 = LogManager.getLogger();
	private static final int field_34353 = 5;
	private final String field_34354;
	private final int field_34355;
	private final MinecraftServer field_34356;
	private boolean field_34357;
	private Socket field_34358;
	private Thread field_34359;

	public class_6486(String string, int i, MinecraftServer minecraftServer) {
		this.field_34354 = string;
		this.field_34355 = i;
		this.field_34356 = minecraftServer;
	}

	public void method_37875() {
		if (this.field_34359 != null && this.field_34359.isAlive()) {
			field_34352.warn("Remote control client was asked to start, but it is already running. Will ignore.");
		}

		this.field_34357 = true;
		this.field_34359 = new Thread(this::method_37878);
		this.field_34359.start();
	}

	public void method_37877() {
		this.field_34357 = false;
		if (this.field_34358 != null && !this.field_34358.isClosed()) {
			try {
				this.field_34358.close();
			} catch (IOException var2) {
				field_34352.warn("Failed to close socket to remote control server", (Throwable)var2);
			}
		}

		this.field_34358 = null;
		this.field_34359 = null;
	}

	public void method_37878() {
		String string = this.field_34354 + ":" + this.field_34355;

		while (this.field_34357) {
			try {
				field_34352.info("Connecting to remote control server " + string);
				this.field_34358 = new Socket(this.field_34354, this.field_34355);
				field_34352.info("Connected to remote control server! Will continuously execute the command broadcasted by that server.");

				try {
					DataInputStream dataInputStream = new DataInputStream(this.field_34358.getInputStream());

					while (this.field_34357) {
						String string2 = dataInputStream.readUTF();
						this.method_37876(string2);
					}
				} catch (IOException var5) {
					field_34352.warn("Lost connection to remote control server " + string + ". Will retry in 5s.");
				}
			} catch (IOException var6) {
				field_34352.warn("Failed to connect to remote control server " + string + ". Will retry in 5s.");
			}

			if (this.field_34357) {
				try {
					Thread.sleep(5000L);
				} catch (InterruptedException var4) {
				}
			}
		}
	}

	private void method_37876(String string) {
		List<ServerPlayerEntity> list = this.field_34356.getPlayerManager().getPlayerList();
		if (!list.isEmpty()) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(0);
			ServerWorld serverWorld = this.field_34356.getOverworld();
			ServerCommandSource serverCommandSource = new ServerCommandSource(
				serverPlayerEntity, Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "", LiteralText.EMPTY, this.field_34356, serverPlayerEntity
			);
			CommandManager commandManager = this.field_34356.getCommandManager();
			commandManager.execute(serverCommandSource, string);
		}
	}
}
