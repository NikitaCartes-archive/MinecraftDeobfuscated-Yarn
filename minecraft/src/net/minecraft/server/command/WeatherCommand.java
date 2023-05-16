package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.intprovider.IntProvider;

public class WeatherCommand {
	private static final int DEFAULT_DURATION = -1;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("weather")
				.requires(source -> source.hasPermissionLevel(2))
				.then(
					CommandManager.literal("clear")
						.executes(context -> executeClear(context.getSource(), -1))
						.then(
							CommandManager.argument("duration", TimeArgumentType.time(1))
								.executes(context -> executeClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
						)
				)
				.then(
					CommandManager.literal("rain")
						.executes(context -> executeRain(context.getSource(), -1))
						.then(
							CommandManager.argument("duration", TimeArgumentType.time(1))
								.executes(context -> executeRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
						)
				)
				.then(
					CommandManager.literal("thunder")
						.executes(context -> executeThunder(context.getSource(), -1))
						.then(
							CommandManager.argument("duration", TimeArgumentType.time(1))
								.executes(context -> executeThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration")))
						)
				)
		);
	}

	private static int processDuration(ServerCommandSource source, int duration, IntProvider provider) {
		return duration == -1 ? provider.get(source.getWorld().getRandom()) : duration;
	}

	private static int executeClear(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(processDuration(source, duration, ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER), 0, false, false);
		source.sendFeedback(() -> Text.translatable("commands.weather.set.clear"), true);
		return duration;
	}

	private static int executeRain(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, processDuration(source, duration, ServerWorld.RAIN_WEATHER_DURATION_PROVIDER), true, false);
		source.sendFeedback(() -> Text.translatable("commands.weather.set.rain"), true);
		return duration;
	}

	private static int executeThunder(ServerCommandSource source, int duration) {
		source.getWorld().setWeather(0, processDuration(source, duration, ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER), true, true);
		source.sendFeedback(() -> Text.translatable("commands.weather.set.thunder"), true);
		return duration;
	}
}
