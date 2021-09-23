package net.minecraft.server.command;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ChaseCommand {
	private static final String field_35000 = "localhost";
	private static final String field_35001 = "localhost";
	private static final int field_35002 = 10000;
	private static final int field_35003 = 100;
	public static BiMap<String, RegistryKey<World>> DIMENSIONS = ImmutableBiMap.of("o", World.OVERWORLD, "n", World.NETHER, "e", World.END);
	@Nullable
	private static ChaseServer server;
	@Nullable
	private static ChaseClient client;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("chase")
				.then(
					CommandManager.literal("follow")
						.then(
							CommandManager.argument("host", StringArgumentType.string())
								.executes(commandContext -> startClient(commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), 10000))
								.then(
									CommandManager.argument("port", IntegerArgumentType.integer(1, 65535))
										.executes(
											commandContext -> startClient(
													commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), IntegerArgumentType.getInteger(commandContext, "port")
												)
										)
								)
						)
						.executes(commandContext -> startClient(commandContext.getSource(), "localhost", 10000))
				)
				.then(
					CommandManager.literal("lead")
						.then(
							CommandManager.argument("bind_address", StringArgumentType.string())
								.executes(commandContext -> startServer(commandContext.getSource(), StringArgumentType.getString(commandContext, "bind_address"), 10000))
								.then(
									CommandManager.argument("port", IntegerArgumentType.integer(1024, 65535))
										.executes(
											commandContext -> startServer(
													commandContext.getSource(), StringArgumentType.getString(commandContext, "bind_address"), IntegerArgumentType.getInteger(commandContext, "port")
												)
										)
								)
						)
						.executes(commandContext -> startServer(commandContext.getSource(), "localhost", 10000))
				)
				.then(CommandManager.literal("stop").executes(commandContext -> stop(commandContext.getSource())))
		);
	}

	private static int stop(ServerCommandSource source) {
		if (client != null) {
			client.stop();
			source.sendFeedback(new LiteralText("You have now stopped chasing"), false);
			client = null;
		}

		if (server != null) {
			server.stop();
			source.sendFeedback(new LiteralText("You are no longer being chased"), false);
			server = null;
		}

		return 0;
	}

	private static boolean isRunning(ServerCommandSource source) {
		if (server != null) {
			source.sendError(new LiteralText("Chase server is already running. Stop it using /chase stop"));
			return true;
		} else if (client != null) {
			source.sendError(new LiteralText("You are already chasing someone. Stop it using /chase stop"));
			return true;
		} else {
			return false;
		}
	}

	private static int startServer(ServerCommandSource source, String ip, int port) {
		if (isRunning(source)) {
			return 0;
		} else {
			server = new ChaseServer(ip, port, source.getServer().getPlayerManager(), 100);

			try {
				server.start();
				source.sendFeedback(new LiteralText("Chase server is now running on port " + port + ". Clients can follow you using /chase follow <ip> <port>"), false);
			} catch (IOException var4) {
				var4.printStackTrace();
				source.sendError(new LiteralText("Failed to start chase server on port " + port));
				server = null;
			}

			return 0;
		}
	}

	private static int startClient(ServerCommandSource source, String ip, int port) {
		if (isRunning(source)) {
			return 0;
		} else {
			client = new ChaseClient(ip, port, source.getServer());
			client.start();
			source.sendFeedback(
				new LiteralText(
					"You are now chasing "
						+ ip
						+ ":"
						+ port
						+ ". If that server does '/chase lead' then you will automatically go to the same position. Use '/chase stop' to stop chasing."
				),
				false
			);
			return 0;
		}
	}
}
