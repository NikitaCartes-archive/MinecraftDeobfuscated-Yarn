/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.io.IOException;
import net.minecraft.server.chase.ChaseClient;
import net.minecraft.server.chase.ChaseServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChaseCommand {
    private static final String field_35000 = "localhost";
    private static final String field_35001 = "0.0.0.0";
    private static final int field_35002 = 10000;
    private static final int field_35003 = 100;
    public static BiMap<String, RegistryKey<World>> DIMENSIONS = ImmutableBiMap.of("o", World.OVERWORLD, "n", World.NETHER, "e", World.END);
    @Nullable
    private static ChaseServer server;
    @Nullable
    private static ChaseClient client;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("chase").then((ArgumentBuilder<ServerCommandSource, ?>)((LiteralArgumentBuilder)CommandManager.literal("follow").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("host", StringArgumentType.string()).executes(commandContext -> ChaseCommand.startClient((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), 10000))).then(CommandManager.argument("port", IntegerArgumentType.integer(1, 65535)).executes(commandContext -> ChaseCommand.startClient((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "host"), IntegerArgumentType.getInteger(commandContext, "port")))))).executes(commandContext -> ChaseCommand.startClient((ServerCommandSource)commandContext.getSource(), field_35000, 10000)))).then(((LiteralArgumentBuilder)CommandManager.literal("lead").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("bind_address", StringArgumentType.string()).executes(commandContext -> ChaseCommand.startServer((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "bind_address"), 10000))).then(CommandManager.argument("port", IntegerArgumentType.integer(1024, 65535)).executes(commandContext -> ChaseCommand.startServer((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "bind_address"), IntegerArgumentType.getInteger(commandContext, "port")))))).executes(commandContext -> ChaseCommand.startServer((ServerCommandSource)commandContext.getSource(), field_35001, 10000)))).then(CommandManager.literal("stop").executes(commandContext -> ChaseCommand.stop((ServerCommandSource)commandContext.getSource()))));
    }

    private static int stop(ServerCommandSource source) {
        if (client != null) {
            client.stop();
            source.sendFeedback(new LiteralText("You have now stopped chasing"), false);
            client = null;
        }
        if (server != null) {
            server.stop();
            source.sendFeedback(new LiteralText("You are no longer being chased"), false);
            server = null;
        }
        return 0;
    }

    private static boolean isRunning(ServerCommandSource source) {
        if (server != null) {
            source.sendError(new LiteralText("Chase server is already running. Stop it using /chase stop"));
            return true;
        }
        if (client != null) {
            source.sendError(new LiteralText("You are already chasing someone. Stop it using /chase stop"));
            return true;
        }
        return false;
    }

    private static int startServer(ServerCommandSource source, String ip, int port) {
        if (ChaseCommand.isRunning(source)) {
            return 0;
        }
        server = new ChaseServer(ip, port, source.getServer().getPlayerManager(), 100);
        try {
            server.start();
            source.sendFeedback(new LiteralText("Chase server is now running on port " + port + ". Clients can follow you using /chase follow <ip> <port>"), false);
        } catch (IOException iOException) {
            iOException.printStackTrace();
            source.sendError(new LiteralText("Failed to start chase server on port " + port));
            server = null;
        }
        return 0;
    }

    private static int startClient(ServerCommandSource source, String ip, int port) {
        if (ChaseCommand.isRunning(source)) {
            return 0;
        }
        client = new ChaseClient(ip, port, source.getServer());
        client.start();
        source.sendFeedback(new LiteralText("You are now chasing " + ip + ":" + port + ". If that server does '/chase lead' then you will automatically go to the same position. Use '/chase stop' to stop chasing."), false);
        return 0;
    }
}

