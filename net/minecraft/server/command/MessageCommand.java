/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.concurrent.Executor;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(context -> MessageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), MessageArgumentType.getSignedMessage(context, "message"))))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, MessageArgumentType.SignedMessage signedMessage) {
        if (targets.isEmpty()) {
            return 0;
        }
        MessageSender messageSender = source.getChatMessageSender();
        signedMessage.decorate(source).thenAcceptAsync(decoratedMessage -> {
            for (ServerPlayerEntity serverPlayerEntity : targets) {
                MessageSender messageSender2 = messageSender.withTargetName(serverPlayerEntity.getDisplayName());
                source.sendChatMessage(messageSender2, (SignedMessage)decoratedMessage.raw(), MessageType.MSG_COMMAND_OUTGOING);
                SignedMessage signedMessage = (SignedMessage)decoratedMessage.getFilterableFor(source, serverPlayerEntity);
                if (signedMessage == null) continue;
                serverPlayerEntity.sendChatMessage(signedMessage, messageSender, MessageType.MSG_COMMAND_INCOMING);
            }
        }, (Executor)source.getServer());
        return targets.size();
    }
}

