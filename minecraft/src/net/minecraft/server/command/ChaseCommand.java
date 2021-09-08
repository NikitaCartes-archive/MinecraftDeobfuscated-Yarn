package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.text.LiteralText;

public class ChaseCommand {
	private static ChaseServer server;
	private static ChaseClient client;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("chase")
				.executes(commandContext -> startClient(commandContext.getSource(), "localhost", 10000))
				.then(
					CommandManager.literal("me")
						.executes(commandContext -> startServer(commandContext.getSource(), 10000))
						.then(
							CommandManager.argument("port", IntegerArgumentType.integer())
								.executes(commandContext -> startServer(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
						)
				)
				.then(
					CommandManager.argument("host", StringArgumentType.string())
						.executes(commandContext -> startClient(commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), 10000))
						.then(
							CommandManager.argument("port", IntegerArgumentType.integer())
								.executes(
									commandContext -> startClient(
											commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), IntegerArgumentType.getInteger(commandContext, "port")
										)
								)
						)
				)
				.then(CommandManager.literal("stop").executes(commandContext -> stop(commandContext.getSource())))
		);
	}

	private static int stop(ServerCommandSource source) {
		if (client != null) {
			client.stop();
			source.sendFeedback(new LiteralText("You will now stop chasing"), false);
			client = null;
		}

		if (server != null) {
			server.stop();
			source.sendFeedback(new LiteralText("You will now stop being chased"), false);
			server = null;
		}

		return 0;
	}

	private static int startServer(ServerCommandSource source, int port) {
		if (server != null) {
			source.sendError(new LiteralText("Chase server is already running. Stop it using /chase stop"));
			return 0;
		} else {
			server = new ChaseServer(port, source.getServer().getPlayerManager(), 100);

			try {
				server.start();
				source.sendFeedback(new LiteralText("Chase server is now running on port " + port + ". Clients can follow you using /chase <ip> <port>"), false);
			} catch (IOException var3) {
				var3.printStackTrace();
				source.sendError(new LiteralText("Failed to start chase server on port " + port));
				server = null;
			}

			return 0;
		}
	}

	private static int startClient(ServerCommandSource source, String ip, int port) {
		if (client != null) {
			source.sendError(new LiteralText("You are already chasing someone. Stop it using /chase stop"));
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
						+ ". If that server does '/chase me' then you will automatically go to the same position. Use '/chase stop' to stop chasing."
				),
				false
			);
			return 0;
		}
	}
}
