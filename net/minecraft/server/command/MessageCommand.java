/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class MessageCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("message", MessageArgumentType.message()).executes(commandContext -> MessageCommand.execute((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), MessageArgumentType.getMessage(commandContext, "message"))))));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
        dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text message) {
        Consumer<Text> consumer;
        UUID uUID = source.getEntity() == null ? Util.NIL_UUID : source.getEntity().getUuid();
        Entity entity = source.getEntity();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            consumer = text2 -> serverPlayerEntity.sendSystemMessage(new TranslatableText("commands.message.display.outgoing", text2, message).formatted(Formatting.GRAY, Formatting.ITALIC), serverPlayerEntity.getUuid());
        } else {
            consumer = text2 -> source.sendFeedback(new TranslatableText("commands.message.display.outgoing", text2, message).formatted(Formatting.GRAY, Formatting.ITALIC), false);
        }
        for (ServerPlayerEntity serverPlayerEntity2 : targets) {
            consumer.accept(serverPlayerEntity2.getDisplayName());
            serverPlayerEntity2.sendSystemMessage(new TranslatableText("commands.message.display.incoming", source.getDisplayName(), message).formatted(Formatting.GRAY, Formatting.ITALIC), uUID);
        }
        return targets.size();
    }
}

