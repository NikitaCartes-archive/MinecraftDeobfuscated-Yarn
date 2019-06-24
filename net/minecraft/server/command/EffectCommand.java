/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.MobEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class EffectCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.effect.give.failed", new Object[0]));
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.effect.clear.everything.failed", new Object[0]));
    private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.effect.clear.specific.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("clear").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets")))).then(CommandManager.argument("effect", MobEffectArgumentType.mobEffect()).executes(commandContext -> EffectCommand.executeClear((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), MobEffectArgumentType.getMobEffect(commandContext, "effect"))))))).then(CommandManager.literal("give").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("effect", MobEffectArgumentType.mobEffect()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), MobEffectArgumentType.getMobEffect(commandContext, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), MobEffectArgumentType.getMobEffect(commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), MobEffectArgumentType.getMobEffect(commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), true))).then(CommandManager.argument("hideParticles", BoolArgumentType.bool()).executes(commandContext -> EffectCommand.executeGive((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntities(commandContext, "targets"), MobEffectArgumentType.getMobEffect(commandContext, "effect"), IntegerArgumentType.getInteger(commandContext, "seconds"), IntegerArgumentType.getInteger(commandContext, "amplifier"), !BoolArgumentType.getBool(commandContext, "hideParticles"))))))))));
    }

    private static int executeGive(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, StatusEffect statusEffect, @Nullable Integer integer, int i, boolean bl) throws CommandSyntaxException {
        int j = 0;
        int k = integer != null ? (statusEffect.isInstant() ? integer : integer * 20) : (statusEffect.isInstant() ? 1 : 600);
        for (Entity entity : collection) {
            StatusEffectInstance statusEffectInstance;
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).addPotionEffect(statusEffectInstance = new StatusEffectInstance(statusEffect, k, i, false, bl))) continue;
            ++j;
        }
        if (j == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.give.success.single", statusEffect.method_5560(), collection.iterator().next().getDisplayName(), k / 20), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.give.success.multiple", statusEffect.method_5560(), collection.size(), k / 20), true);
        }
        return j;
    }

    private static int executeClear(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).clearPotionEffects()) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.single", collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.multiple", collection.size()), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource serverCommandSource, Collection<? extends Entity> collection, StatusEffect statusEffect) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : collection) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeStatusEffect(statusEffect)) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
        }
        if (collection.size() == 1) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.single", statusEffect.method_5560(), collection.iterator().next().getDisplayName()), true);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.multiple", statusEffect.method_5560(), collection.size()), true);
        }
        return i;
    }
}

