package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
	private static final int field_33398 = 6000;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("weather")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("clear")
						.executes(context -> executeClear(context.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(context -> executeClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))
						)
				)
				.then(
					CommandManager.literal("rain")
						.executes(context -> executeRain(context.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(context -> executeRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))
						)
				)
				.then(
					CommandManager.literal("thunder")
						.executes(context -> executeThunder(context.getSource(), 6000))
						.then(
							CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000))
								.executes(context -> executeThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))
						)
				)
		);
	}

	private static int executeClear(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(duration, 0, false, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
		return duration;
	}

	private static int executeRain(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, duration, true, false);
		source.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
		return duration;
	}

	private static int executeThunder(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, duration, true, true);
		source.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
		return duration;
	}
}
