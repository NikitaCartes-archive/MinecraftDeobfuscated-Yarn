/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.ChatFormat;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.create()).executes(commandContext -> MessageCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "message"))))));
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Component component) {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.sendMessage(new TranslatableComponent("commands.message.display.incoming", serverCommandSource.getDisplayName(), component.copy()).applyFormat(ChatFormat.GRAY, ChatFormat.ITALIC));
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.message.display.outgoing", serverPlayerEntity.getDisplayName(), component.copy()).applyFormat(ChatFormat.GRAY, ChatFormat.ITALIC), false);
        }
        return collection.size();
    }
}

