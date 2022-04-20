/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class EffectCommand {
    private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.method_43471("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(source -> source.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal("clear").executes(context -> EffectCommand.executeClear((ServerCommandSource)context.getSource(), ImmutableList.of(((ServerCommandSource)context.getSource()).getEntityOrThrow())))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).executes(context -> EffectCommand.executeClear((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets")))).then(CommandManager.argument("effect", StatusEffectArgumentType.statusEffect()).executes(context -> EffectCommand.executeClear((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StatusEffectArgumentType.getStatusEffect(context, "effect"))))))).then(CommandManager.literal("give").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("targets", EntityArgumentType.entities()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("effect", StatusEffectArgumentType.statusEffect()).executes(context -> EffectCommand.executeGive((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StatusEffectArgumentType.getStatusEffect(context, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes(context -> EffectCommand.executeGive((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StatusEffectArgumentType.getStatusEffect(context, "effect"), IntegerArgumentType.getInteger(context, "seconds"), 0, true))).then(((RequiredArgumentBuilder)CommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes(context -> EffectCommand.executeGive((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StatusEffectArgumentType.getStatusEffect(context, "effect"), IntegerArgumentType.getInteger(context, "seconds"), IntegerArgumentType.getInteger(context, "amplifier"), true))).then(CommandManager.argument("hideParticles", BoolArgumentType.bool()).executes(context -> EffectCommand.executeGive((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StatusEffectArgumentType.getStatusEffect(context, "effect"), IntegerArgumentType.getInteger(context, "seconds"), IntegerArgumentType.getInteger(context, "amplifier"), !BoolArgumentType.getBool(context, "hideParticles"))))))))));
    }

    private static int executeGive(ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect, @Nullable Integer seconds, int amplifier, boolean showParticles) throws CommandSyntaxException {
        int i = 0;
        int j = seconds != null ? (effect.isInstant() ? seconds : seconds * 20) : (effect.isInstant() ? 1 : 600);
        for (Entity entity : targets) {
            StatusEffectInstance statusEffectInstance;
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).addStatusEffect(statusEffectInstance = new StatusEffectInstance(effect, j, amplifier, false, showParticles), source.getEntity())) continue;
            ++i;
        }
        if (i == 0) {
            throw GIVE_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.method_43469("commands.effect.give.success.single", effect.getName(), targets.iterator().next().getDisplayName(), j / 20), true);
        } else {
            source.sendFeedback(Text.method_43469("commands.effect.give.success.multiple", effect.getName(), targets.size(), j / 20), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).clearStatusEffects()) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.method_43469("commands.effect.clear.everything.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.method_43469("commands.effect.clear.everything.success.multiple", targets.size()), true);
        }
        return i;
    }

    private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect) throws CommandSyntaxException {
        int i = 0;
        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).removeStatusEffect(effect)) continue;
            ++i;
        }
        if (i == 0) {
            throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(Text.method_43469("commands.effect.clear.specific.success.single", effect.getName(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(Text.method_43469("commands.effect.clear.specific.success.multiple", effect.getName(), targets.size()), true);
        }
        return i;
    }
}

