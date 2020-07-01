package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
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

	private static int executeClear(ServerCommandSource source, int duration) {
		source.getWorld().method_27910(duration, 0, false, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
		return duration;
	}

	private static int executeRain(ServerCommandSource source, int duration) {
		source.getWorld().method_27910(0, duration, true, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
		return duration;
	}

	private static int executeThunder(ServerCommandSource source, int duration) {
		source.getWorld().method_27910(0, duration, true, true);
		source.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
		return duration;
	}
}
