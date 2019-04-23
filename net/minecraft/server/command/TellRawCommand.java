/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.ComponentArgumentType;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.network.chat.Components;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class TellRawCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tellraw").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", ComponentArgumentType.create()).executes(commandContext -> {
            int i = 0;
            for (ServerPlayerEntity serverPlayerEntity : EntityArgumentType.getPlayers(commandContext, "targets")) {
                serverPlayerEntity.sendMessage(Components.resolveAndStyle((ServerCommandSource)commandContext.getSource(), ComponentArgumentType.getComponent(commandContext, "message"), serverPlayerEntity));
                ++i;
            }
            return i;
        }))));
    }
}

