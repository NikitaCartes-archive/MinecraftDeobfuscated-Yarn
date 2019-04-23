/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.create()).executes(commandContext -> {
            Component component = MessageArgumentType.getMessage(commandContext, "message");
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().sendToAll(new TranslatableComponent("chat.type.announcement", ((ServerCommandSource)commandContext.getSource()).getDisplayName(), component));
            return 1;
        })));
    }
}

