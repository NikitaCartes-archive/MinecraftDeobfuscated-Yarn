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
import net.minecraft.class_5888;
import net.minecraft.class_5894;
import net.minecraft.class_5903;
import net.minecraft.class_5904;
import net.minecraft.class_5905;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.network.Packet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class TitleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("title").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("clear").executes(commandContext -> TitleCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("reset").executes(commandContext -> TitleCommand.executeReset((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"))))).then(CommandManager.literal("title").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "title", class_5904::new))))).then(CommandManager.literal("subtitle").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "subtitle", class_5903::new))))).then(CommandManager.literal("actionbar").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("title", TextArgumentType.text()).executes(commandContext -> TitleCommand.executeTitle((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), TextArgumentType.getTextArgument(commandContext, "title"), "actionbar", class_5894::new))))).then(CommandManager.literal("times").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeIn", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("stay", IntegerArgumentType.integer(0)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("fadeOut", IntegerArgumentType.integer(0)).executes(commandContext -> TitleCommand.executeTimes((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut")))))))));
    }

    private static int executeClear(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        class_5888 lv = new class_5888(false);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(lv);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.cleared.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.cleared.multiple", targets.size()), true);
        }
        return targets.size();
    }

    private static int executeReset(ServerCommandSource source, Collection<ServerPlayerEntity> targets) {
        class_5888 lv = new class_5888(true);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(lv);
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
        class_5905 lv = new class_5905(fadeIn, stay, fadeOut);
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            serverPlayerEntity.networkHandler.sendPacket(lv);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.title.times.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.title.times.multiple", targets.size()), true);
        }
        return targets.size();
    }
}

