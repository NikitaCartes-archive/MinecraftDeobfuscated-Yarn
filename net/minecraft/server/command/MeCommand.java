/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class MeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)CommandManager.literal("me").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("action", StringArgumentType.greedyString()).executes(commandContext -> {
            ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().sendToAll(new TranslatableText("chat.type.emote", ((ServerCommandSource)commandContext.getSource()).getDisplayName(), StringArgumentType.getString(commandContext, "action")));
            return 1;
        })));
    }
}

