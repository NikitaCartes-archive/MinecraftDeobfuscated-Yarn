/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class KickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kick").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), new TranslatableComponent("multiplayer.disconnect.kicked", new Object[0])))).then(CommandManager.argument("reason", MessageArgumentType.create()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "reason"))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Component component) {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.disconnect(component);
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.kick.success", serverPlayerEntity.getDisplayName(), component), true);
        }
        return collection.size();
    }
}

