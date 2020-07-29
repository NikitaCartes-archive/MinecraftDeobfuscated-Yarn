/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class StructureTestUtil {
    public static String testStructuresDirectoryName = "gameteststructures";

    public static BlockRotation method_29408(int i) {
        switch (i) {
            case 0: {
                return BlockRotation.NONE;
            }
            case 1: {
                return BlockRotation.CLOCKWISE_90;
            }
            case 2: {
                return BlockRotation.CLOCKWISE_180;
            }
            case 3: {
                return BlockRotation.COUNTERCLOCKWISE_90;
            }
        }
        throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + i);
    }

    public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockEntity) {
        BlockPos blockPos = structureBlockEntity.getPos();
        BlockPos blockPos2 = blockPos.add(structureBlockEntity.getSize().add(-1, -1, -1));
        BlockPos blockPos3 = Structure.transformAround(blockPos2, BlockMirror.NONE, structureBlockEntity.getRotation(), blockPos);
        return new Box(blockPos, blockPos3);
    }

    public static BlockBox method_29410(StructureBlockBlockEntity structureBlockBlockEntity) {
        BlockPos blockPos = structureBlockBlockEntity.getPos();
        BlockPos blockPos2 = blockPos.add(structureBlockBlockEntity.getSize().add(-1, -1, -1));
        BlockPos blockPos3 = Structure.transformAround(blockPos2, BlockMirror.NONE, structureBlockBlockEntity.getRotation(), blockPos);
        return new BlockBox(blockPos, blockPos3);
    }

    public static void placeStartButton(BlockPos blockPos, BlockPos blockPos2, BlockRotation blockRotation, ServerWorld serverWorld) {
        BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, blockRotation, blockPos);
        serverWorld.setBlockState(blockPos3, Blocks.COMMAND_BLOCK.getDefaultState());
        CommandBlockBlockEntity commandBlockBlockEntity = (CommandBlockBlockEntity)serverWorld.getBlockEntity(blockPos3);
        commandBlockBlockEntity.getCommandExecutor().setCommand("test runthis");
        BlockPos blockPos4 = Structure.transformAround(blockPos3.add(0, 0, -1), BlockMirror.NONE, blockRotation, blockPos3);
        serverWorld.setBlockState(blockPos4, Blocks.STONE_BUTTON.getDefaultState().rotate(blockRotation));
    }

    public static void createTestArea(String structure, BlockPos pos, BlockPos size, BlockRotation blockRotation, ServerWorld world) {
        BlockBox blockBox = StructureTestUtil.method_29409(pos, size, blockRotation);
        StructureTestUtil.clearArea(blockBox, pos.getY(), world);
        world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
        structureBlockBlockEntity.setIgnoreEntities(false);
        structureBlockBlockEntity.setStructureName(new Identifier(structure));
        structureBlockBlockEntity.setSize(size);
        structureBlockBlockEntity.setMode(StructureBlockMode.SAVE);
        structureBlockBlockEntity.setShowBoundingBox(true);
    }

    public static StructureBlockBlockEntity method_22250(String string, BlockPos blockPos, BlockRotation blockRotation, int i, ServerWorld serverWorld, boolean bl) {
        BlockPos blockPos3;
        BlockPos blockPos2 = StructureTestUtil.createStructure(string, serverWorld).getSize();
        BlockBox blockBox = StructureTestUtil.method_29409(blockPos, blockPos2, blockRotation);
        if (blockRotation == BlockRotation.NONE) {
            blockPos3 = blockPos;
        } else if (blockRotation == BlockRotation.CLOCKWISE_90) {
            blockPos3 = blockPos.add(blockPos2.getZ() - 1, 0, 0);
        } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
            blockPos3 = blockPos.add(blockPos2.getX() - 1, 0, blockPos2.getZ() - 1);
        } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
            blockPos3 = blockPos.add(0, 0, blockPos2.getX() - 1);
        } else {
            throw new IllegalArgumentException("Invalid rotation: " + (Object)((Object)blockRotation));
        }
        StructureTestUtil.forceLoadNearbyChunks(blockPos, serverWorld);
        StructureTestUtil.clearArea(blockBox, blockPos.getY(), serverWorld);
        StructureBlockBlockEntity structureBlockBlockEntity = StructureTestUtil.placeStructure(string, blockPos3, blockRotation, serverWorld, bl);
        ((ServerTickScheduler)serverWorld.getBlockTickScheduler()).getScheduledTicks(blockBox, true, false);
        serverWorld.clearUpdatesInArea(blockBox);
        return structureBlockBlockEntity;
    }

    private static void forceLoadNearbyChunks(BlockPos pos, ServerWorld world) {
        ChunkPos chunkPos = new ChunkPos(pos);
        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int k = chunkPos.x + i;
                int l = chunkPos.z + j;
                world.setChunkForced(k, l, true);
            }
        }
    }

    public static void clearArea(BlockBox area, int i, ServerWorld world) {
        BlockBox blockBox = new BlockBox(area.minX - 2, area.minY - 3, area.minZ - 3, area.maxX + 3, area.maxY + 20, area.maxZ + 3);
        BlockPos.stream(blockBox).forEach(blockPos -> StructureTestUtil.method_22368(i, blockPos, world));
        ((ServerTickScheduler)world.getBlockTickScheduler()).getScheduledTicks(blockBox, true, false);
        world.clearUpdatesInArea(blockBox);
        Box box = new Box(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ);
        List<Entity> list = world.getEntitiesByClass(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
        list.forEach(Entity::remove);
    }

    public static BlockBox method_29409(BlockPos blockPos, BlockPos blockPos2, BlockRotation blockRotation) {
        BlockPos blockPos3 = blockPos.add(blockPos2).add(-1, -1, -1);
        BlockPos blockPos4 = Structure.transformAround(blockPos3, BlockMirror.NONE, blockRotation, blockPos);
        BlockBox blockBox = BlockBox.create(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos4.getX(), blockPos4.getY(), blockPos4.getZ());
        int i = Math.min(blockBox.minX, blockBox.maxX);
        int j = Math.min(blockBox.minZ, blockBox.maxZ);
        BlockPos blockPos5 = new BlockPos(blockPos.getX() - i, 0, blockPos.getZ() - j);
        blockBox.method_29299(blockPos5);
        return blockBox;
    }

    public static Optional<BlockPos> findContainingStructureBlock(BlockPos pos, int radius, ServerWorld world) {
        return StructureTestUtil.findStructureBlocks(pos, radius, world).stream().filter(blockPos2 -> StructureTestUtil.isInStructureBounds(blockPos2, pos, world)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos pos, int radius, ServerWorld world) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(blockPos2 -> blockPos2.getManhattanDistance(pos));
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(pos, radius, world);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world) {
        ArrayList<BlockPos> collection = Lists.newArrayList();
        Box box = new Box(pos);
        box = box.expand(radius);
        for (int i = (int)box.minX; i <= (int)box.maxX; ++i) {
            for (int j = (int)box.minY; j <= (int)box.maxY; ++j) {
                for (int k = (int)box.minZ; k <= (int)box.maxZ; ++k) {
                    BlockPos blockPos = new BlockPos(i, j, k);
                    BlockState blockState = world.getBlockState(blockPos);
                    if (!blockState.isOf(Blocks.STRUCTURE_BLOCK)) continue;
                    collection.add(blockPos);
                }
            }
        }
        return collection;
    }

    private static Structure createStructure(String structureId, ServerWorld world) {
        StructureManager structureManager = world.getStructureManager();
        Structure structure = structureManager.getStructure(new Identifier(structureId));
        if (structure != null) {
            return structure;
        }
        String string = structureId + ".snbt";
        Path path = Paths.get(testStructuresDirectoryName, string);
        CompoundTag compoundTag = StructureTestUtil.loadSnbt(path);
        if (compoundTag == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
        }
        return structureManager.createStructure(compoundTag);
    }

    private static StructureBlockBlockEntity placeStructure(String name, BlockPos pos, BlockRotation blockRotation, ServerWorld serverWorld, boolean bl) {
        serverWorld.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(pos);
        structureBlockBlockEntity.setMode(StructureBlockMode.LOAD);
        structureBlockBlockEntity.setRotation(blockRotation);
        structureBlockBlockEntity.setIgnoreEntities(false);
        structureBlockBlockEntity.setStructureName(new Identifier(name));
        structureBlockBlockEntity.loadStructure(serverWorld, bl);
        if (structureBlockBlockEntity.getSize() != BlockPos.ORIGIN) {
            return structureBlockBlockEntity;
        }
        Structure structure = StructureTestUtil.createStructure(name, serverWorld);
        structureBlockBlockEntity.place(serverWorld, bl, structure);
        if (structureBlockBlockEntity.getSize() == BlockPos.ORIGIN) {
            throw new RuntimeException("Failed to load structure " + name);
        }
        return structureBlockBlockEntity;
    }

    @Nullable
    private static CompoundTag loadSnbt(Path path) {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string = IOUtils.toString(bufferedReader);
            return StringNbtReader.parse(string);
        } catch (IOException iOException) {
            return null;
        } catch (CommandSyntaxException commandSyntaxException) {
            throw new RuntimeException("Error while trying to load structure " + path, commandSyntaxException);
        }
    }

    private static void method_22368(int altitude, BlockPos pos, ServerWorld world) {
        BlockState blockState = null;
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.getDefaultConfig();
        if (flatChunkGeneratorConfig instanceof FlatChunkGeneratorConfig) {
            BlockState[] blockStates = flatChunkGeneratorConfig.getLayerBlocks();
            if (pos.getY() < altitude && pos.getY() <= blockStates.length) {
                blockState = blockStates[pos.getY() - 1];
            }
        } else if (pos.getY() == altitude - 1) {
            blockState = world.getBiome(pos).getGenerationSettings().getSurfaceConfig().getTopMaterial();
        } else if (pos.getY() < altitude - 1) {
            blockState = world.getBiome(pos).getGenerationSettings().getSurfaceConfig().getUnderMaterial();
        }
        if (blockState == null) {
            blockState = Blocks.AIR.getDefaultState();
        }
        BlockStateArgument blockStateArgument = new BlockStateArgument(blockState, Collections.emptySet(), null);
        blockStateArgument.setBlockState(world, pos, 2);
        world.updateNeighbors(pos, blockState.getBlock());
    }

    private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
        Box box = StructureTestUtil.getStructureBoundingBox(structureBlockBlockEntity).expand(1.0);
        return box.contains(Vec3d.ofCenter(pos));
    }
}

