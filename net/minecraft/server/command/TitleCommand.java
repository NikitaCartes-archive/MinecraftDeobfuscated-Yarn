/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.network.packet.TitleS2CPacket;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("title").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("clear").executes(commandContext -> TitleCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("reset").executes(commandContext -> TitleCommand.executeReset((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("title").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), TitleS2CPacket.Action.TITLE))))).then(CommandManager.literal("subtitle").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), TitleS2CPacket.Action.SUBTITLE))))).then(CommandManager.literal("actionbar").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), TitleS2CPacket.Action.ACTIONBAR))))).then(CommandManager.literal("times").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeIn", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("stay", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeOut", IntegerArgumentType.integer(0)).executes(commandContext -> TitleCommand.executeTimes((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut")))))))));
    }

    private static int executeClear(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.CLEAR, null);
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.cleared.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.cleared.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int executeReset(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(TitleS2CPacket.Action.RESET, null);
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.reset.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.reset.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int executeTitle(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, Text text, TitleS2CPacket.Action action) throws CommandSyntaxException {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(new TitleS2CPacket(action, Texts.parse(serverCommandSource, text, serverPlayerEntity, 0)));
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.show." + action.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int executeTimes(ServerCommandSource serverCommandSource, Collection<ServerPlayerEntity> collection, int i, int j, int k) {
        TitleS2CPacket titleS2CPacket = new TitleS2CPacket(i, j, k);
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            serverPlayerEntity.networkHandler.sendPacket(titleS2CPacket);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.times.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.title.times.multiple", collection.size()), true);
        }
        return collection.size();
    }
}

