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
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.experience.set.points.invalid"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("experience").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer()).executes(context -> ExperienceCommand.executeAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.POINTS))).then(CommandManager.literal("points").executes(context -> ExperienceCommand.executeAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(context -> ExperienceCommand.executeAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.LEVELS))))))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer(0)).executes(context -> ExperienceCommand.executeSet((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.POINTS))).then(CommandManager.literal("points").executes(context -> ExperienceCommand.executeSet((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(context -> ExperienceCommand.executeSet((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "amount"), Component.LEVELS))))))).then(CommandManager.literal("query").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.player()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("points").executes(context -> ExperienceCommand.executeQuery((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayer(context, "targets"), Component.POINTS)))).then(CommandManager.literal("levels").executes(context -> ExperienceCommand.executeQuery((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayer(context, "targets"), Component.LEVELS))))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("xp").requires(source -> source.hasPermissionLevel(2))).redirect(literalCommandNode));
    }

    private static int executeQuery(ServerCommandSource source, ServerPlayerEntity player, Component component) {
        int i = component.getter.applyAsInt(player);
        source.sendFeedback(new TranslatableText("commands.experience.query." + component.name, player.getDisplayName(), i), false);
        return i;
    }

    private static int executeAdd(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Component component) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            component.adder.accept(serverPlayerEntity, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.experience.add." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.experience.add." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    private static int executeSet(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Component component) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!component.setter.test(serverPlayerEntity, amount)) continue;
            ++i;
        }
        if (i == 0) {
            throw SET_POINT_INVALID_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslatableText("commands.experience.set." + component.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslatableText("commands.experience.set." + component.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    static enum Component {
        POINTS("points", PlayerEntity::addExperience, (player, xp) -> {
            if (xp >= player.getNextLevelExperience()) {
                return false;
            }
            player.setExperiencePoints((int)xp);
            return true;
        }, player -> MathHelper.floor(player.experienceProgress * (float)player.getNextLevelExperience())),
        LEVELS("levels", ServerPlayerEntity::addExperienceLevels, (player, level) -> {
            player.setExperienceLevel((int)level);
            return true;
        }, player -> player.experienceLevel);

        public final BiConsumer<ServerPlayerEntity, Integer> adder;
        public final BiPredicate<ServerPlayerEntity, Integer> setter;
        public final String name;
        final ToIntFunction<ServerPlayerEntity> getter;

        private Component(String name, BiConsumer<ServerPlayerEntity, Integer> adder, BiPredicate<ServerPlayerEntity, Integer> setter, ToIntFunction<ServerPlayerEntity> getter) {
            this.adder = adder;
            this.name = name;
            this.setter = setter;
            this.getter = getter;
        }
    }
}

