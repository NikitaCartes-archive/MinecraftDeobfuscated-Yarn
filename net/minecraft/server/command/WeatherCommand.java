/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class WeatherCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("weather").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(commandContext -> WeatherCommand.executeClear((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> WeatherCommand.executeClear((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("rain").executes(commandContext -> WeatherCommand.executeRain((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> WeatherCommand.executeRain((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20))))).then(((LiteralArgumentBuilder)CommandManager.literal("thunder").executes(commandContext -> WeatherCommand.executeThunder((ServerCommandSource)commandContext.getSource(), 6000))).then(CommandManager.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(commandContext -> WeatherCommand.executeThunder((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20)))));
    }

    private static int executeClear(ServerCommandSource source, int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(duration);
        source.getWorld().getLevelProperties().setRainTime(0);
        source.getWorld().getLevelProperties().setThunderTime(0);
        source.getWorld().getLevelProperties().setRaining(false);
        source.getWorld().getLevelProperties().setThundering(false);
        source.sendFeedback(new TranslatableText("commands.weather.set.clear"), true);
        return duration;
    }

    private static int executeRain(ServerCommandSource source, int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(0);
        source.getWorld().getLevelProperties().setRainTime(duration);
        source.getWorld().getLevelProperties().setThunderTime(duration);
        source.getWorld().getLevelProperties().setRaining(true);
        source.getWorld().getLevelProperties().setThundering(false);
        source.sendFeedback(new TranslatableText("commands.weather.set.rain"), true);
        return duration;
    }

    private static int executeThunder(ServerCommandSource source, int duration) {
        source.getWorld().getLevelProperties().setClearWeatherTime(0);
        source.getWorld().getLevelProperties().setRainTime(duration);
        source.getWorld().getLevelProperties().setThunderTime(duration);
        source.getWorld().getLevelProperties().setRaining(true);
        source.getWorld().getLevelProperties().setThundering(true);
        source.sendFeedback(new TranslatableText("commands.weather.set.thunder"), true);
        return duration;
    }
}

