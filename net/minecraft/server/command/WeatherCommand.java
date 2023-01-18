/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.intprovider.IntProvider;

public class WeatherCommand {
    private static final int DEFAULT_DURATION = -1;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather").requires(source -> source.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(context -> WeatherCommand.executeClear((ServerCommandSource)context.getSource(), -1))).then(CommandManager.argument("duration", TimeArgumentType.time(1)).executes(context -> WeatherCommand.executeClear((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "duration")))))).then(((LiteralArgumentBuilder)CommandManager.literal("rain").executes(context -> WeatherCommand.executeRain((ServerCommandSource)context.getSource(), -1))).then(CommandManager.argument("duration", TimeArgumentType.time(1)).executes(context -> WeatherCommand.executeRain((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "duration")))))).then(((LiteralArgumentBuilder)CommandManager.literal("thunder").executes(context -> WeatherCommand.executeThunder((ServerCommandSource)context.getSource(), -1))).then(CommandManager.argument("duration", TimeArgumentType.time(1)).executes(context -> WeatherCommand.executeThunder((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "duration"))))));
    }

    private static int processDuration(ServerCommandSource source, int duration, IntProvider provider) {
        if (duration == -1) {
            return provider.get(source.getWorld().getRandom());
        }
        return duration;
    }

    private static int executeClear(ServerCommandSource source, int duration) {
        source.getWorld().setWeather(WeatherCommand.processDuration(source, duration, ServerWorld.CLEAR_WEATHER_DURATION_PROVIDER), 0, false, false);
        source.sendFeedback(Text.translatable("commands.weather.set.clear"), true);
        return duration;
    }

    private static int executeRain(ServerCommandSource source, int duration) {
        source.getWorld().setWeather(0, WeatherCommand.processDuration(source, duration, ServerWorld.RAIN_WEATHER_DURATION_PROVIDER), true, false);
        source.sendFeedback(Text.translatable("commands.weather.set.rain"), true);
        return duration;
    }

    private static int executeThunder(ServerCommandSource source, int duration) {
        source.getWorld().setWeather(0, WeatherCommand.processDuration(source, duration, ServerWorld.THUNDER_WEATHER_DURATION_PROVIDER), true, true);
        source.sendFeedback(Text.translatable("commands.weather.set.thunder"), true);
        return duration;
    }
}

