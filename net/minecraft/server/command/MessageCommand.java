/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            Collection<ServerPlayerEntity> collection = EntityArgumentType.getPlayers(context, "targets");
            if (!collection.isEmpty()) {
                MessageArgumentType.getSignedMessage(context, "message", message -> MessageCommand.execute((ServerCommandSource)context.getSource(), collection, message));
            }
            return collection.size();
        }))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
    }

    private static void execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message) {
        MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, source);
        SentMessage sentMessage = SentMessage.of(message);
        boolean bl = false;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            MessageType.Parameters parameters2 = MessageType.params(MessageType.MSG_COMMAND_OUTGOING, source).withTargetName(serverPlayerEntity.getDisplayName());
            source.sendChatMessage(sentMessage, false, parameters2);
            boolean bl2 = source.shouldFilterText(serverPlayerEntity);
            serverPlayerEntity.sendChatMessage(sentMessage, bl2, parameters);
            bl |= bl2 && message.isFullyFiltered();
        }
        if (bl) {
            source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
        }
    }
}

