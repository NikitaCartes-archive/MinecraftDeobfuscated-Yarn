/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(context -> {
            MessageArgumentType.getSignedMessage(context, "message", message -> {
                ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
                PlayerManager playerManager = serverCommandSource.getServer().getPlayerManager();
                playerManager.broadcast((SignedMessage)message, serverCommandSource, MessageType.params(MessageType.SAY_COMMAND, serverCommandSource));
            });
            return 1;
        })));
    }
}

