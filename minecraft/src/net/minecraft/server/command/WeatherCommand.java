package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("weather")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("clear")
						.executes(commandContext -> executeClear(commandContext.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> executeClear(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					CommandManager.literal("rain")
						.executes(commandContext -> executeRain(commandContext.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> executeRain(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
				.then(
					CommandManager.literal("thunder")
						.executes(commandContext -> executeThunder(commandContext.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(commandContext -> executeThunder(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().method_8401().setClearWeatherTime(i);
		serverCommandSource.getWorld().method_8401().setRainTime(0);
		serverCommandSource.getWorld().method_8401().setThunderTime(0);
		serverCommandSource.getWorld().method_8401().setRaining(false);
		serverCommandSource.getWorld().method_8401().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
		return i;
	}

	private static int executeRain(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().method_8401().setClearWeatherTime(0);
		serverCommandSource.getWorld().method_8401().setRainTime(i);
		serverCommandSource.getWorld().method_8401().setThunderTime(i);
		serverCommandSource.getWorld().method_8401().setRaining(true);
		serverCommandSource.getWorld().method_8401().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
		return i;
	}

	private static int executeThunder(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().method_8401().setClearWeatherTime(0);
		serverCommandSource.getWorld().method_8401().setRainTime(i);
		serverCommandSource.getWorld().method_8401().setThunderTime(i);
		serverCommandSource.getWorld().method_8401().setRaining(true);
		serverCommandSource.getWorld().method_8401().setThundering(true);
		serverCommandSource.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
		return i;
	}
}
