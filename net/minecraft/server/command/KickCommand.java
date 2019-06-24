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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kick").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), new TranslatableText("multiplayer.disconnect.kicked", new Object[0])))).then(CommandManager.argument("reason", MessageArgumentType.message()).executes(commandContext -> KickCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "reason"))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Text text) {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.disconnect(text);
            serverCommandSource.sendFeedback(new TranslatableText("commands.kick.success", serverPlayerEntity.getDisplayName(), text), true);
        }
        return collection.size();
    }
}

