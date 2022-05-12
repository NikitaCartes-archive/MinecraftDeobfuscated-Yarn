/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
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

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            SignedChatMessage signedChatMessage = MessageArgumentType.getSignedMessage(context, "message");
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
            if (serverCommandSource.isExecutedByPlayer()) {
                ServerPlayerEntity serverPlayerEntity = serverCommandSource.getPlayerOrThrow();
                serverPlayerEntity.getTextStream().filterText(signedChatMessage.signedContent().getString()).thenAcceptAsync(message -> {
                    SignedChatMessage signedChatMessage2 = serverCommandSource.getServer().getChatDecorator().decorate(serverPlayerEntity, signedChatMessage);
                    playerManager.broadcast(signedChatMessage2, (TextStream.Message)message, serverPlayerEntity, MessageType.SAY_COMMAND);
                }, (Executor)serverCommandSource.getServer());
            } else {
                playerManager.broadcast(signedChatMessage, serverCommandSource.getChatMessageSender(), MessageType.SAY_COMMAND);
            }
            return 1;
        })));
    }
}

