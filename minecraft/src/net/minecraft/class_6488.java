package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class class_6488 {
	private static class_6487 field_34366;
	private static class_6486 field_34367;

	public static void method_37885(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("chase")
				.executes(commandContext -> method_37889(commandContext.getSource(), "localhost", 10000))
				.then(
					CommandManager.literal("me")
						.executes(commandContext -> method_37888(commandContext.getSource(), 10000))
						.then(
							CommandManager.argument("port", IntegerArgumentType.integer())
								.executes(commandContext -> method_37888(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port")))
						)
				)
				.then(
					CommandManager.argument("host", StringArgumentType.string())
						.executes(commandContext -> method_37889(commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), 10000))
						.then(
							CommandManager.argument("port", IntegerArgumentType.integer())
								.executes(
									commandContext -> method_37889(
											commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), IntegerArgumentType.getInteger(commandContext, "port")
										)
								)
						)
				)
				.then(CommandManager.literal("stop").executes(commandContext -> method_37887(commandContext.getSource())))
		);
	}

	private static int method_37887(ServerCommandSource serverCommandSource) {
		if (field_34367 != null) {
			field_34367.method_37877();
			serverCommandSource.sendFeedback(new LiteralText("You will now stop chasing"), false);
			field_34367 = null;
		}

		if (field_34366 != null) {
			field_34366.method_37882();
			serverCommandSource.sendFeedback(new LiteralText("You will now stop being chased"), false);
			field_34366 = null;
		}

		return 0;
	}

	private static int method_37888(ServerCommandSource serverCommandSource, int i) {
		if (field_34366 != null) {
			serverCommandSource.sendError(new LiteralText("Chase server is already running. Stop it using /chase stop"));
			return 0;
		} else {
			field_34366 = new class_6487(i, serverCommandSource.getServer().getPlayerManager(), 100);

			try {
				field_34366.method_37879();
				serverCommandSource.sendFeedback(new LiteralText("Chase server is now running on port " + i + ". Clients can follow you using /chase <ip> <port>"), false);
			} catch (IOException var3) {
				var3.printStackTrace();
				serverCommandSource.sendError(new LiteralText("Failed to start chase server on port " + i));
				field_34366 = null;
			}

			return 0;
		}
	}

	private static int method_37889(ServerCommandSource serverCommandSource, String string, int i) {
		if (field_34367 != null) {
			serverCommandSource.sendError(new LiteralText("You are already chasing someone. Stop it using /chase stop"));
			return 0;
		} else {
			field_34367 = new class_6486(string, i, serverCommandSource.getServer());
			field_34367.method_37875();
			serverCommandSource.sendFeedback(
				new LiteralText(
					"You are now chasing "
						+ string
						+ ":"
						+ i
						+ ". If that server does '/chase me' then you will automatically go to the same position. Use '/chase stop' to stop chasing."
				),
				false
			);
			return 0;
		}
	}
}
