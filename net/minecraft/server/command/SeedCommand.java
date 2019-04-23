/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class SeedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("seed").requires(serverCommandSource -> serverCommandSource.getMinecraftServer().isSinglePlayer() || serverCommandSource.hasPermissionLevel(2))).executes(commandContext -> {
            long l = ((ServerCommandSource)commandContext.getSource()).getWorld().getSeed();
            Component component = Components.bracketed(new TextComponent(String.valueOf(l)).modifyStyle(style -> style.setColor(ChatFormat.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(l))).setInsertion(String.valueOf(l))));
            ((ServerCommandSource)commandContext.getSource()).sendFeedback(new TranslatableComponent("commands.seed.success", component), false);
            return (int)l;
        }));
    }
}

