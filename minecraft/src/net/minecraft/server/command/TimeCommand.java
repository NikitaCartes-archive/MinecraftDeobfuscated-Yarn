package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;

public class TimeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("time")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("set")
						.then(CommandManager.literal("day").executes(commandContext -> executeSet(commandContext.getSource(), 1000)))
						.then(CommandManager.literal("noon").executes(commandContext -> executeSet(commandContext.getSource(), 6000)))
						.then(CommandManager.literal("night").executes(commandContext -> executeSet(commandContext.getSource(), 13000)))
						.then(CommandManager.literal("midnight").executes(commandContext -> executeSet(commandContext.getSource(), 18000)))
						.then(
							CommandManager.argument("time", TimeArgumentType.create())
								.executes(commandContext -> executeSet(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					CommandManager.literal("add")
						.then(
							CommandManager.argument("time", TimeArgumentType.create())
								.executes(commandContext -> executeAdd(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					CommandManager.literal("query")
						.then(
							CommandManager.literal("daytime")
								.executes(commandContext -> executeQuery(commandContext.getSource(), getDayTime(commandContext.getSource().getWorld())))
						)
						.then(
							CommandManager.literal("gametime")
								.executes(commandContext -> executeQuery(commandContext.getSource(), (int)(commandContext.getSource().getWorld().getTime() % 2147483647L)))
						)
						.then(
							CommandManager.literal("day")
								.executes(
									commandContext -> executeQuery(commandContext.getSource(), (int)(commandContext.getSource().getWorld().getTimeOfDay() / 24000L % 2147483647L))
								)
						)
				)
		);
	}

	private static int getDayTime(ServerWorld serverWorld) {
		return (int)(serverWorld.getTimeOfDay() % 24000L);
	}

	private static int executeQuery(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.time.query", i), false);
		return i;
	}

	public static int executeSet(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay((long)i);
		}

		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.time.set", i), true);
		return getDayTime(serverCommandSource.getWorld());
	}

	public static int executeAdd(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}

		int j = getDayTime(serverCommandSource.getWorld());
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.time.set", j), true);
		return j;
	}
}
