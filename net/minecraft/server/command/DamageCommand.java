/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DamageCommand {
    private static final SimpleCommandExceptionType INVULNERABLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.damage.invulnerable"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("damage").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument("target", EntityArgumentType.entity()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("amount", FloatArgumentType.floatArg(0.0f)).executes(context -> DamageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), ((ServerCommandSource)context.getSource()).getWorld().getDamageSources().generic()))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("damageType", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.DAMAGE_TYPE)).executes(context -> DamageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), new DamageSource(RegistryEntryArgumentType.getRegistryEntry(context, "damageType", RegistryKeys.DAMAGE_TYPE))))).then(CommandManager.literal("at").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("location", Vec3ArgumentType.vec3()).executes(context -> DamageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), new DamageSource(RegistryEntryArgumentType.getRegistryEntry(context, "damageType", RegistryKeys.DAMAGE_TYPE), Vec3ArgumentType.getVec3(context, "location"))))))).then(CommandManager.literal("by").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("entity", EntityArgumentType.entity()).executes(context -> DamageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), new DamageSource(RegistryEntryArgumentType.getRegistryEntry(context, "damageType", RegistryKeys.DAMAGE_TYPE), EntityArgumentType.getEntity(context, "entity"))))).then(CommandManager.literal("from").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("cause", EntityArgumentType.entity()).executes(context -> DamageCommand.execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntity(context, "target"), FloatArgumentType.getFloat(context, "amount"), new DamageSource(RegistryEntryArgumentType.getRegistryEntry(context, "damageType", RegistryKeys.DAMAGE_TYPE), EntityArgumentType.getEntity(context, "entity"), EntityArgumentType.getEntity(context, "cause"))))))))))));
    }

    private static int execute(ServerCommandSource source, Entity target, float amount, DamageSource damageSource) throws CommandSyntaxException {
        if (target.damage(damageSource, amount)) {
            source.sendFeedback(Text.translatable("commands.damage.success", Float.valueOf(amount), target.getDisplayName()), true);
            return 1;
        }
        throw INVULNERABLE_EXCEPTION.create();
    }
}

