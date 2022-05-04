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
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.structure.StructureType;

public class PlaceCommand {
    private static final SimpleCommandExceptionType FEATURE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.feature.failed"));
    private static final SimpleCommandExceptionType JIGSAW_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.jigsaw.failed"));
    private static final SimpleCommandExceptionType STRUCTURE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.structure.failed"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("place").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("feature").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("feature", RegistryKeyArgumentType.registryKey(Registry.CONFIGURED_FEATURE_KEY)).executes(context -> PlaceCommand.executePlaceFeature((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceCommand.executePlaceFeature((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"))))))).then(CommandManager.literal("jigsaw").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pool", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_POOL_KEY)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", IdentifierArgumentType.identifier()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("max_depth", IntegerArgumentType.integer(1, 7)).executes(commandContext -> PlaceCommand.executePlaceJigsaw((ServerCommandSource)commandContext.getSource(), RegistryKeyArgumentType.getStructurePoolEntry(commandContext, "pool"), IdentifierArgumentType.getIdentifier(commandContext, "target"), IntegerArgumentType.getInteger(commandContext, "max_depth"), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("position", BlockPosArgumentType.blockPos()).executes(commandContext -> PlaceCommand.executePlaceJigsaw((ServerCommandSource)commandContext.getSource(), RegistryKeyArgumentType.getStructurePoolEntry(commandContext, "pool"), IdentifierArgumentType.getIdentifier(commandContext, "target"), IntegerArgumentType.getInteger(commandContext, "max_depth"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "position"))))))))).then(CommandManager.literal("structure").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("structure", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_KEY)).executes(commandContext -> PlaceCommand.executePlaceStructure((ServerCommandSource)commandContext.getSource(), RegistryKeyArgumentType.getStructureTypeEntry(commandContext, "structure"), new BlockPos(((ServerCommandSource)commandContext.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(commandContext -> PlaceCommand.executePlaceStructure((ServerCommandSource)commandContext.getSource(), RegistryKeyArgumentType.getStructureTypeEntry(commandContext, "structure"), BlockPosArgumentType.getLoadedBlockPos(commandContext, "pos")))))));
    }

    public static int executePlaceFeature(ServerCommandSource source, RegistryEntry<ConfiguredFeature<?, ?>> feature, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        ConfiguredFeature<?, ?> configuredFeature = feature.value();
        ChunkPos chunkPos = new ChunkPos(pos);
        PlaceCommand.throwOnUnloadedPos(serverWorld, new ChunkPos(chunkPos.x - 1, chunkPos.z - 1), new ChunkPos(chunkPos.x + 1, chunkPos.z + 1));
        if (!configuredFeature.generate(serverWorld, serverWorld.getChunkManager().getChunkGenerator(), serverWorld.getRandom(), pos)) {
            throw FEATURE_FAILED_EXCEPTION.create();
        }
        String string = feature.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
        source.sendFeedback(Text.translatable("commands.place.feature.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    public static int executePlaceJigsaw(ServerCommandSource source, RegistryEntry<StructurePool> structurePool, Identifier id, int i, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        if (!StructurePoolBasedGenerator.generate(serverWorld, structurePool, id, i, pos, false)) {
            throw JIGSAW_FAILED_EXCEPTION.create();
        }
        source.sendFeedback(Text.translatable("commands.place.jigsaw.success", pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    public static int executePlaceStructure(ServerCommandSource source, RegistryEntry<StructureType> structureType, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        StructureType structureType2 = structureType.value();
        ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        StructureStart structureStart = structureType2.createStructureStart(source.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), serverWorld.getChunkManager().getNoiseConfig(), serverWorld.getStructureManager(), serverWorld.getSeed(), new ChunkPos(pos), 0, serverWorld, registryEntry -> true);
        if (!structureStart.hasChildren()) {
            throw STRUCTURE_FAILED_EXCEPTION.create();
        }
        BlockBox blockBox = structureStart.getBoundingBox();
        ChunkPos chunkPos2 = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMinX()), ChunkSectionPos.getSectionCoord(blockBox.getMinZ()));
        ChunkPos chunkPos22 = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMaxX()), ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()));
        PlaceCommand.throwOnUnloadedPos(serverWorld, chunkPos2, chunkPos22);
        ChunkPos.stream(chunkPos2, chunkPos22).forEach(chunkPos -> structureStart.place(serverWorld, serverWorld.getStructureAccessor(), chunkGenerator, serverWorld.getRandom(), new BlockBox(chunkPos.getStartX(), serverWorld.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), serverWorld.getTopY(), chunkPos.getEndZ()), (ChunkPos)chunkPos));
        String string = structureType.getKey().map(key -> key.getValue().toString()).orElse("[unregistered]");
        source.sendFeedback(Text.translatable("commands.place.structure.success", string, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    private static void throwOnUnloadedPos(ServerWorld world, ChunkPos pos1, ChunkPos pos2) throws CommandSyntaxException {
        if (ChunkPos.stream(pos1, pos2).filter(pos -> !world.canSetBlock(pos.getStartPos())).findAny().isPresent()) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
    }
}

