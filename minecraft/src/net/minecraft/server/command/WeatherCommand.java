package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableTextComponent;

public class WeatherCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("weather")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("clear")
						.executes(commandContext -> method_13824(commandContext.getSource(), 6000))
						.then(
							ServerCommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13824(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					ServerCommandManager.literal("rain")
						.executes(commandContext -> method_13828(commandContext.getSource(), 6000))
						.then(
							ServerCommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13828(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					ServerCommandManager.literal("thunder")
						.executes(commandContext -> method_13833(commandContext.getSource(), 6000))
						.then(
							ServerCommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> method_13833(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
		);
	}

	private static int method_13824(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.method_9225().method_8401().setClearWeatherTime(i);
		serverCommandSource.method_9225().method_8401().setRainTime(0);
		serverCommandSource.method_9225().method_8401().setThunderTime(0);
		serverCommandSource.method_9225().method_8401().setRaining(false);
		serverCommandSource.method_9225().method_8401().setThundering(false);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.weather.set.clear"), true);
		return i;
	}

	private static int method_13828(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.method_9225().method_8401().setClearWeatherTime(0);
		serverCommandSource.method_9225().method_8401().setRainTime(i);
		serverCommandSource.method_9225().method_8401().setThunderTime(i);
		serverCommandSource.method_9225().method_8401().setRaining(true);
		serverCommandSource.method_9225().method_8401().setThundering(false);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.weather.set.rain"), true);
		return i;
	}

	private static int method_13833(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.method_9225().method_8401().setClearWeatherTime(0);
		serverCommandSource.method_9225().method_8401().setRainTime(i);
		serverCommandSource.method_9225().method_8401().setThunderTime(i);
		serverCommandSource.method_9225().method_8401().setRaining(true);
		serverCommandSource.method_9225().method_8401().setThundering(true);
		serverCommandSource.method_9226(new TranslatableTextComponent("commands.weather.set.thunder"), true);
		return i;
	}
}
