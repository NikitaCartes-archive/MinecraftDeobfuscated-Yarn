/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SummonCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed"));
    private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.invalidPosition"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("summon").requires(source -> source.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("entity", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> SummonCommand.execute((ServerCommandSource)context.getSource(), RegistryEntryArgumentType.getSummonableEntityType(context, "entity"), ((ServerCommandSource)context.getSource()).getPosition(), new NbtCompound(), true))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes(context -> SummonCommand.execute((ServerCommandSource)context.getSource(), RegistryEntryArgumentType.getSummonableEntityType(context, "entity"), Vec3ArgumentType.getVec3(context, "pos"), new NbtCompound(), true))).then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(context -> SummonCommand.execute((ServerCommandSource)context.getSource(), RegistryEntryArgumentType.getSummonableEntityType(context, "entity"), Vec3ArgumentType.getVec3(context, "pos"), NbtCompoundArgumentType.getNbtCompound(context, "nbt"), false))))));
    }

    private static int execute(ServerCommandSource source, RegistryEntry.Reference<EntityType<?>> entityType, Vec3d pos, NbtCompound nbt, boolean initialize) throws CommandSyntaxException {
        BlockPos blockPos = new BlockPos(pos);
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create();
        }
        NbtCompound nbtCompound = nbt.copy();
        nbtCompound.putString("id", entityType.registryKey().getValue().toString());
        ServerWorld serverWorld = source.getWorld();
        Entity entity2 = EntityType.loadEntityWithPassengers(nbtCompound, serverWorld, entity -> {
            entity.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, entity.getYaw(), entity.getPitch());
            return entity;
        });
        if (entity2 == null) {
            throw FAILED_EXCEPTION.create();
        }
        if (initialize && entity2 instanceof MobEntity) {
            ((MobEntity)entity2).initialize(source.getWorld(), source.getWorld().getLocalDifficulty(entity2.getBlockPos()), SpawnReason.COMMAND, null, null);
        }
        if (!serverWorld.spawnNewEntityAndPassengers(entity2)) {
            throw FAILED_UUID_EXCEPTION.create();
        }
        source.sendFeedback(Text.translatable("commands.summon.success", entity2.getDisplayName()), true);
        return 1;
    }
}

