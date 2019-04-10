package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableTextComponent;

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
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(0);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRaining(false);
		serverCommandSource.getWorld().getLevelProperties().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.weather.set.clear"), true);
		return i;
	}

	private static int executeRain(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(i);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRaining(true);
		serverCommandSource.getWorld().getLevelProperties().setThundering(false);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.weather.set.rain"), true);
		return i;
	}

	private static int executeThunder(ServerCommandSource serverCommandSource, int i) {
		serverCommandSource.getWorld().getLevelProperties().setClearWeatherTime(0);
		serverCommandSource.getWorld().getLevelProperties().setRainTime(i);
		serverCommandSource.getWorld().getLevelProperties().setThunderTime(i);
		serverCommandSource.getWorld().getLevelProperties().setRaining(true);
		serverCommandSource.getWorld().getLevelProperties().setThundering(true);
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.weather.set.thunder"), true);
		return i;
	}
}
