/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public class SayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("say").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> {
            Text text = MessageArgumentType.getMessage(commandContext, "message");
            TranslatableText translatableText = new TranslatableText("chat.type.announcement", ((ServerCommandSource)commandContext.getSource()).getDisplayName(), text);
            Entity entity = ((ServerCommandSource)commandContext.getSource()).getEntity();
            if (entity != null) {
                ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().broadcastChatMessage(translatableText, MessageType.CHAT, entity.getUuid());
            } else {
                ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().broadcastChatMessage(translatableText, MessageType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }
}

