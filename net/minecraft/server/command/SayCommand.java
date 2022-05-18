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
import net.minecraft.server.filter.FilteredMessage;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "message");
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
            signedMessage.decorate(serverCommandSource).thenAcceptAsync(decoratedMessage -> playerManager.broadcast((FilteredMessage<SignedChatMessage>)decoratedMessage, serverCommandSource, MessageType.SAY_COMMAND), (Executor)serverCommandSource.getServer());
            return 1;
        })));
    }
}

