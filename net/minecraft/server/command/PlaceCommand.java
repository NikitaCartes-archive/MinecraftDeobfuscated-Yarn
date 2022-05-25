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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockMirrorArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockRotationArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
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
    private static final DynamicCommandExceptionType TEMPLATE_INVALID_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("commands.place.template.invalid", id));
    private static final SimpleCommandExceptionType TEMPLATE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.place.template.failed"));
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        StructureManager structureManager = ((ServerCommandSource)context.getSource()).getWorld().getStructureManager();
        return CommandSource.suggestIdentifiers(structureManager.streamStructures(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("place").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("feature").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("feature", RegistryKeyArgumentType.registryKey(Registry.CONFIGURED_FEATURE_KEY)).executes(context -> PlaceCommand.executePlaceFeature((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceCommand.executePlaceFeature((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getConfiguredFeatureEntry(context, "feature"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"))))))).then(CommandManager.literal("jigsaw").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("pool", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_POOL_KEY)).then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("target", IdentifierArgumentType.identifier()).then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("max_depth", IntegerArgumentType.integer(1, 7)).executes(context -> PlaceCommand.executePlaceJigsaw((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getStructurePoolEntry(context, "pool"), IdentifierArgumentType.getIdentifier(context, "target"), IntegerArgumentType.getInteger(context, "max_depth"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("position", BlockPosArgumentType.blockPos()).executes(context -> PlaceCommand.executePlaceJigsaw((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getStructurePoolEntry(context, "pool"), IdentifierArgumentType.getIdentifier(context, "target"), IntegerArgumentType.getInteger(context, "max_depth"), BlockPosArgumentType.getLoadedBlockPos(context, "position"))))))))).then(CommandManager.literal("structure").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("structure", RegistryKeyArgumentType.registryKey(Registry.STRUCTURE_KEY)).executes(context -> PlaceCommand.executePlaceStructure((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getStructureTypeEntry(context, "structure"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition())))).then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceCommand.executePlaceStructure((ServerCommandSource)context.getSource(), RegistryKeyArgumentType.getStructureTypeEntry(context, "structure"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"))))))).then(CommandManager.literal("template").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)CommandManager.argument("template", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), new BlockPos(((ServerCommandSource)context.getSource()).getPosition()), BlockRotation.NONE, BlockMirror.NONE, 1.0f, 0))).then(((RequiredArgumentBuilder)CommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockRotation.NONE, BlockMirror.NONE, 1.0f, 0))).then(((RequiredArgumentBuilder)CommandManager.argument("rotation", BlockRotationArgumentType.blockRotation()).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockRotationArgumentType.getBlockRotation(context, "rotation"), BlockMirror.NONE, 1.0f, 0))).then(((RequiredArgumentBuilder)CommandManager.argument("mirror", BlockMirrorArgumentType.blockMirror()).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockRotationArgumentType.getBlockRotation(context, "rotation"), BlockMirrorArgumentType.getBlockMirror(context, "mirror"), 1.0f, 0))).then(((RequiredArgumentBuilder)CommandManager.argument("integrity", FloatArgumentType.floatArg(0.0f, 1.0f)).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockRotationArgumentType.getBlockRotation(context, "rotation"), BlockMirrorArgumentType.getBlockMirror(context, "mirror"), FloatArgumentType.getFloat(context, "integrity"), 0))).then(CommandManager.argument("seed", IntegerArgumentType.integer()).executes(context -> PlaceCommand.executePlaceTemplate((ServerCommandSource)context.getSource(), IdentifierArgumentType.getIdentifier(context, "template"), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), BlockRotationArgumentType.getBlockRotation(context, "rotation"), BlockMirrorArgumentType.getBlockMirror(context, "mirror"), FloatArgumentType.getFloat(context, "integrity"), IntegerArgumentType.getInteger(context, "seed")))))))))));
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

    public static int executePlaceJigsaw(ServerCommandSource source, RegistryEntry<StructurePool> structurePool, Identifier id, int maxDepth, BlockPos pos) throws CommandSyntaxException {
        ServerWorld serverWorld = source.getWorld();
        if (!StructurePoolBasedGenerator.generate(serverWorld, structurePool, id, maxDepth, pos, false)) {
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

    public static int executePlaceTemplate(ServerCommandSource source, Identifier id, BlockPos pos, BlockRotation rotation, BlockMirror mirror, float integrity, int seed) throws CommandSyntaxException {
        boolean bl;
        Optional<Structure> optional;
        ServerWorld serverWorld = source.getWorld();
        StructureManager structureManager = serverWorld.getStructureManager();
        try {
            optional = structureManager.getStructure(id);
        } catch (InvalidIdentifierException invalidIdentifierException) {
            throw TEMPLATE_INVALID_EXCEPTION.create(id);
        }
        if (optional.isEmpty()) {
            throw TEMPLATE_INVALID_EXCEPTION.create(id);
        }
        Structure structure = optional.get();
        PlaceCommand.throwOnUnloadedPos(serverWorld, new ChunkPos(pos), new ChunkPos(pos.add(structure.getSize())));
        StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(mirror).setRotation(rotation);
        if (integrity < 1.0f) {
            structurePlacementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(integrity)).setRandom(StructureBlockBlockEntity.createRandom(seed));
        }
        if (!(bl = structure.place(serverWorld, pos, pos, structurePlacementData, StructureBlockBlockEntity.createRandom(seed), 2))) {
            throw TEMPLATE_FAILED_EXCEPTION.create();
        }
        source.sendFeedback(Text.translatable("commands.place.template.success", id, pos.getX(), pos.getY(), pos.getZ()), true);
        return 1;
    }

    private static void throwOnUnloadedPos(ServerWorld world, ChunkPos pos1, ChunkPos pos2) throws CommandSyntaxException {
        if (ChunkPos.stream(pos1, pos2).filter(pos -> !world.canSetBlock(pos.getStartPos())).findAny().isPresent()) {
            throw BlockPosArgumentType.UNLOADED_EXCEPTION.create();
        }
    }
}

