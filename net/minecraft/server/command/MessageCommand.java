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
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");
            try {
                return MessageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), signedMessage);
            } catch (Exception exception) {
                signedMessage.sendHeader((ServerCommandSource)context.getSource());
                throw exception;
            }
        }))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, MessageArgumentType.SignedMessage signedMessage2) {
        MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, serverCommandSource);
        signedMessage2.decorate(serverCommandSource, signedMessage -> {
            SentMessage sentMessage = SentMessage.of(signedMessage);
            boolean bl = signedMessage.method_45100();
            Entity entity = serverCommandSource.getEntity();
            boolean bl2 = false;
            for (ServerPlayerEntity serverPlayerEntity : collection) {
                MessageType.Parameters parameters2 = MessageType.params(MessageType.MSG_COMMAND_OUTGOING, serverCommandSource).withTargetName(serverPlayerEntity.getDisplayName());
                serverCommandSource.sendChatMessage(sentMessage, false, parameters2);
                boolean bl3 = serverCommandSource.method_45067(serverPlayerEntity);
                serverPlayerEntity.sendChatMessage(sentMessage, bl3, parameters);
                bl2 |= bl && bl3 && serverPlayerEntity != entity;
            }
            if (bl2) {
                serverCommandSource.method_45068(PlayerManager.field_39921);
            }
            sentMessage.afterPacketsSent(serverCommandSource.getServer().getPlayerManager());
        });
        return collection.size();
    }
}

