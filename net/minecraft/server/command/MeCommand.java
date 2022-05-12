/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.concurrent.Executor;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayerEntity;

public class MeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("action", MessageArgumentType.message()).executes(context -> {
            SignedChatMessage signedChatMessage = MessageArgumentType.getSignedMessage(context, "action");
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            if (serverCommandSource.isExecutedByPlayer()) {
                ServerPlayerEntity serverPlayerEntity = serverCommandSource.getPlayerOrThrow();
                serverPlayerEntity.getTextStream().filterText(signedChatMessage.signedContent().getString()).thenAcceptAsync(message -> {
                    PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
                    SignedChatMessage signedChatMessage2 = serverCommandSource.getServer().getChatDecorator().decorate(serverPlayerEntity, signedChatMessage);
                    playerManager.broadcast(signedChatMessage2, (TextStream.Message)message, serverPlayerEntity, MessageType.EMOTE_COMMAND);
                }, (Executor)serverCommandSource.getServer());
            } else {
                serverCommandSource.getServer().getPlayerManager().broadcast(signedChatMessage, serverCommandSource.getChatMessageSender(), MessageType.EMOTE_COMMAND);
            }
            return 1;
        })));
    }
}

