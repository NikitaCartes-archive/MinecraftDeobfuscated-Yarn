package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableTextComponent;

public class TimeCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("time")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("set")
						.then(ServerCommandManager.literal("day").executes(commandContext -> method_13784(commandContext.getSource(), 1000)))
						.then(ServerCommandManager.literal("noon").executes(commandContext -> method_13784(commandContext.getSource(), 6000)))
						.then(ServerCommandManager.literal("night").executes(commandContext -> method_13784(commandContext.getSource(), 13000)))
						.then(ServerCommandManager.literal("midnight").executes(commandContext -> method_13784(commandContext.getSource(), 18000)))
						.then(
							ServerCommandManager.argument("time", TimeArgumentType.create())
								.executes(commandContext -> method_13784(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					ServerCommandManager.literal("add")
						.then(
							ServerCommandManager.argument("time", TimeArgumentType.create())
								.executes(commandContext -> method_13788(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))
						)
				)
				.then(
					ServerCommandManager.literal("query")
						.then(
							ServerCommandManager.literal("daytime")
								.executes(commandContext -> method_13796(commandContext.getSource(), method_13787(commandContext.getSource().method_9225())))
						)
						.then(
							ServerCommandManager.literal("gametime")
								.executes(commandContext -> method_13796(commandContext.getSource(), (int)(commandContext.getSource().method_9225().getTime() % 2147483647L)))
						)
						.then(
							ServerCommandManager.literal("day")
								.executes(
									commandContext -> method_13796(commandContext.getSource(), (int)(commandContext.getSource().method_9225().getTimeOfDay() / 24000L % 2147483647L))
								)
						)
				)
		);
	}

	private static int method_13787(ServerWorld serverWorld) {
		return (int)(serverWorld.getTimeOfDay() % 24000L);
	}

	private static int method_13796(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.time.query", i), false);
		return i;
	}

	public static int method_13784(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay((long)i);
		}

		serverCommandSource.method_9226(new TranslatableTextComponent("commands.time.set", i), true);
		return method_13787(serverCommandSource.method_9225());
	}

	public static int method_13788(ServerCommandSource serverCommandSource, int i) {
		for (ServerWorld serverWorld : serverCommandSource.getMinecraftServer().getWorlds()) {
			serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)i);
		}

		int j = method_13787(serverCommandSource.method_9225());
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.time.set", j), true);
		return j;
	}
}
