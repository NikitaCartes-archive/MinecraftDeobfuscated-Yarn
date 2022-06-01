/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.concurrent.Executor;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;

public class MeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("action", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.SignedMessage signedMessage = MessageArgumentType.getSignedMessage(context, "action");
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
            signedMessage.decorate(serverCommandSource).thenAcceptAsync(decoratedMessage -> playerManager.broadcast((FilteredMessage<SignedMessage>)decoratedMessage, serverCommandSource, MessageType.EMOTE_COMMAND), (Executor)serverCommandSource.getServer());
            return 1;
        })));
    }
}

