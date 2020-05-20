/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;

public class TimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("day").executes(commandContext -> TimeCommand.executeSet((ServerCommandSource)commandContext.getSource(), 1000)))).then(CommandManager.literal("noon").executes(commandContext -> TimeCommand.executeSet((ServerCommandSource)commandContext.getSource(), 6000)))).then(CommandManager.literal("night").executes(commandContext -> TimeCommand.executeSet((ServerCommandSource)commandContext.getSource(), 13000)))).then(CommandManager.literal("midnight").executes(commandContext -> TimeCommand.executeSet((ServerCommandSource)commandContext.getSource(), 18000)))).then(CommandManager.argument("time", TimeArgumentType.time()).executes(commandContext -> TimeCommand.executeSet((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("time", TimeArgumentType.time()).executes(commandContext -> TimeCommand.executeAdd((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("query").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("daytime").executes(commandContext -> TimeCommand.executeQuery((ServerCommandSource)commandContext.getSource(), TimeCommand.getDayTime(((ServerCommandSource)commandContext.getSource()).getWorld()))))).then(CommandManager.literal("gametime").executes(commandContext -> TimeCommand.executeQuery((ServerCommandSource)commandContext.getSource(), (int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTime() % Integer.MAX_VALUE))))).then(CommandManager.literal("day").executes(commandContext -> TimeCommand.executeQuery((ServerCommandSource)commandContext.getSource(), (int)(((ServerCommandSource)commandContext.getSource()).getWorld().getTimeOfDay() / 24000L % Integer.MAX_VALUE))))));
    }

    private static int getDayTime(ServerWorld world) {
        return (int)(world.getTimeOfDay() % 24000L);
    }

    private static int executeQuery(ServerCommandSource source, int time) {
        source.sendFeedback(new TranslatableText("commands.time.query", time), false);
        return time;
    }

    public static int executeSet(ServerCommandSource source, int time) {
        for (ServerWorld serverWorld : source.getMinecraftServer().getWorlds()) {
            serverWorld.method_29199(time);
        }
        source.sendFeedback(new TranslatableText("commands.time.set", time), true);
        return TimeCommand.getDayTime(source.getWorld());
    }

    public static int executeAdd(ServerCommandSource source, int time) {
        for (ServerWorld serverWorld : source.getMinecraftServer().getWorlds()) {
            serverWorld.method_29199(serverWorld.getTimeOfDay() + (long)time);
        }
        int i = TimeCommand.getDayTime(source.getWorld());
        source.sendFeedback(new TranslatableText("commands.time.set", i), true);
        return i;
    }
}

