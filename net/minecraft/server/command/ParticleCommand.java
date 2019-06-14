/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.ParticleArgumentType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleCommand {
    private static final SimpleCommandExceptionType FAILED_EXCPETION = new SimpleCommandExceptionType(new TranslatableText("commands.particle.failed", new Object[0]));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("particle").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("name", ParticleArgumentType.create()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), ((ServerCommandSource)commandContext.getSource()).getPosition(), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.create()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3d.ZERO, 0.0f, 0, false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("delta", Vec3ArgumentType.create(false)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("speed", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("count", IntegerArgumentType.integer(0)).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3ArgumentType.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(((LiteralArgumentBuilder)CommandManager.literal("force").executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3ArgumentType.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", EntityArgumentType.players()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3ArgumentType.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, EntityArgumentType.getPlayers(commandContext, "viewers")))))).then(((LiteralArgumentBuilder)CommandManager.literal("normal").executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3ArgumentType.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPlayerManager().getPlayerList()))).then(CommandManager.argument("viewers", EntityArgumentType.players()).executes(commandContext -> ParticleCommand.execute((ServerCommandSource)commandContext.getSource(), ParticleArgumentType.getParticle(commandContext, "name"), Vec3ArgumentType.getVec3(commandContext, "pos"), Vec3ArgumentType.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, EntityArgumentType.getPlayers(commandContext, "viewers")))))))))));
    }

    private static int execute(ServerCommandSource serverCommandSource, ParticleEffect particleEffect, Vec3d vec3d, Vec3d vec3d2, float f, int i, boolean bl, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
        int j = 0;
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            if (!serverCommandSource.getWorld().spawnParticles(serverPlayerEntity, particleEffect, bl, vec3d.x, vec3d.y, vec3d.z, i, vec3d2.x, vec3d2.y, vec3d2.z, f)) continue;
            ++j;
        }
        if (j == 0) {
            throw FAILED_EXCPETION.create();
        }
        serverCommandSource.sendFeedback(new TranslatableText("commands.particle.success", Registry.PARTICLE_TYPE.getId(particleEffect.getType()).toString()), true);
        return j;
    }
}

