/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class TimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("time").requires(source -> source.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("day").executes(context -> TimeCommand.executeSet((ServerCommandSource)context.getSource(), 1000)))).then(CommandManager.literal("noon").executes(context -> TimeCommand.executeSet((ServerCommandSource)context.getSource(), 6000)))).then(CommandManager.literal("night").executes(context -> TimeCommand.executeSet((ServerCommandSource)context.getSource(), 13000)))).then(CommandManager.literal("midnight").executes(context -> TimeCommand.executeSet((ServerCommandSource)context.getSource(), 18000)))).then(CommandManager.argument("time", TimeArgumentType.time()).executes(context -> TimeCommand.executeSet((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "time")))))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("time", TimeArgumentType.time()).executes(context -> TimeCommand.executeAdd((ServerCommandSource)context.getSource(), IntegerArgumentType.getInteger(context, "time")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("query").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("daytime").executes(context -> TimeCommand.executeQuery((ServerCommandSource)context.getSource(), TimeCommand.getDayTime(((ServerCommandSource)context.getSource()).getWorld()))))).then(CommandManager.literal("gametime").executes(context -> TimeCommand.executeQuery((ServerCommandSource)context.getSource(), (int)(((ServerCommandSource)context.getSource()).getWorld().getTime() % Integer.MAX_VALUE))))).then(CommandManager.literal("day").executes(context -> TimeCommand.executeQuery((ServerCommandSource)context.getSource(), (int)(((ServerCommandSource)context.getSource()).getWorld().getTimeOfDay() / 24000L % Integer.MAX_VALUE))))));
    }

    private static int getDayTime(ServerWorld world) {
        return (int)(world.getTimeOfDay() % 24000L);
    }

    private static int executeQuery(ServerCommandSource source, int time) {
        source.sendFeedback(Text.method_43469("commands.time.query", time), false);
        return time;
    }

    public static int executeSet(ServerCommandSource source, int time) {
        for (ServerWorld serverWorld : source.getServer().getWorlds()) {
            serverWorld.setTimeOfDay(time);
        }
        source.sendFeedback(Text.method_43469("commands.time.set", time), true);
        return TimeCommand.getDayTime(source.getWorld());
    }

    public static int executeAdd(ServerCommandSource source, int time) {
        for (ServerWorld serverWorld : source.getServer().getWorlds()) {
            serverWorld.setTimeOfDay(serverWorld.getTimeOfDay() + (long)time);
        }
        int i = TimeCommand.getDayTime(source.getWorld());
        source.sendFeedback(Text.method_43469("commands.time.set", i), true);
        return i;
    }
}

