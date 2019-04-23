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
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType SET_POINT_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslatableComponent("commands.experience.set.points.invalid", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("experience").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("add").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer()).executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.LEVELS))))))).then(CommandManager.literal("set").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.players()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("amount", IntegerArgumentType.integer(0)).executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.POINTS))).then(CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), Component.LEVELS))))))).then(CommandManager.literal("query").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.player()).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.literal("points").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer(commandContext, "targets"), Component.POINTS)))).then(CommandManager.literal("levels").executes(commandContext -> ExperienceCommand.executeQuery((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getPlayer(commandContext, "targets"), Component.LEVELS))))));
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("xp").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).redirect(literalCommandNode));
    }

    private static int executeQuery(ServerCommandSource serverCommandSource, ServerPlayerEntity serverPlayerEntity, Component component) {
        int i = component.getter.applyAsInt(serverPlayerEntity);
        serverCommandSource.sendFeedback(new TranslatableComponent("commands.experience.query." + component.name, serverPlayerEntity.getDisplayName(), i), false);
        return i;
    }

    private static int executeAdd(ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, Component component) {
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            component.adder.accept(serverPlayerEntity, i);
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.experience.add." + component.name + ".success.single", i, collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.experience.add." + component.name + ".success.multiple", i, collection.size()), true);
        }
        return collection.size();
    }

    private static int executeSet(ServerCommandSource serverCommandSource, Collection<? extends ServerPlayerEntity> collection, int i, Component component) throws CommandSyntaxException {
        int j = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            if (!component.setter.test(serverPlayerEntity, i)) continue;
            ++j;
        }
        if (j == 0) {
            throw SET_POINT_INVALID_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.experience.set." + component.name + ".success.single", i, collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableComponent("commands.experience.set." + component.name + ".success.multiple", i, collection.size()), true);
        }
        return collection.size();
    }

    static enum Component {
        POINTS("points", PlayerEntity::addExperience, (serverPlayerEntity, integer) -> {
            if (integer >= serverPlayerEntity.getNextLevelExperience()) {
                return false;
            }
            serverPlayerEntity.setExperiencePoints((int)integer);
            return true;
        }, serverPlayerEntity -> MathHelper.floor(serverPlayerEntity.experienceProgress * (float)serverPlayerEntity.getNextLevelExperience())),
        LEVELS("levels", ServerPlayerEntity::addExperienceLevels, (serverPlayerEntity, integer) -> {
            serverPlayerEntity.setExperienceLevel((int)integer);
            return true;
        }, serverPlayerEntity -> serverPlayerEntity.experienceLevel);

        public final BiConsumer<ServerPlayerEntity, Integer> adder;
        public final BiPredicate<ServerPlayerEntity, Integer> setter;
        public final String name;
        private final ToIntFunction<ServerPlayerEntity> getter;

        private Component(String string2, BiConsumer<ServerPlayerEntity, Integer> biConsumer, BiPredicate<ServerPlayerEntity, Integer> biPredicate, ToIntFunction<ServerPlayerEntity> toIntFunction) {
            this.adder = biConsumer;
            this.name = string2;
            this.setter = biPredicate;
            this.getter = toIntFunction;
        }
    }
}

