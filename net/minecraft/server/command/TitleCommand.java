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
import java.util.function.Function;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("title").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("clear").executes(commandContext -> TitleCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("reset").executes(commandContext -> TitleCommand.executeReset((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("title").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "title", TitleS2CPacket::new))))).then(CommandManager.literal("subtitle").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "subtitle", SubtitleS2CPacket::new))))).then(CommandManager.literal("actionbar").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "actionbar", OverlayMessageS2CPacket::new))))).then(CommandManager.literal("times").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeIn", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("stay", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeOut", IntegerArgumentType.integer(0)).executes(commandContext -> TitleCommand.executeTimes((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut")))))))));
    }

    private static int executeClear(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        ClearTitleS2CPacket clearTitleS2CPacket = new ClearTitleS2CPacket(false);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(clearTitleS2CPacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.cleared.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.cleared.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        ClearTitleS2CPacket clearTitleS2CPacket = new ClearTitleS2CPacket(true);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(clearTitleS2CPacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.reset.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.reset.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeTitle(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Text title, String string, Function<Text, Packet<?>> function) throws CommandSyntaxException {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(function.apply(Texts.parse(source, title, serverPlayerEntity, 0)));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.show." + string + ".single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.show." + string + ".multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeTimes(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int fadeIn, int stay, int fadeOut) {
        TitleFadeS2CPacket titleFadeS2CPacket = new TitleFadeS2CPacket(fadeIn, stay, fadeOut);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(titleFadeS2CPacket);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.times.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.times.multiple", targets.size()), true);
        }
        return targets.size();
    }
}

