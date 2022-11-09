/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockAgeStructureProcessor
extends StructureProcessor {
    public static final Codec<BlockAgeStructureProcessor> CODEC = ((MapCodec)Codec.FLOAT.fieldOf("mossiness")).xmap(BlockAgeStructureProcessor::new, processor -> Float.valueOf(processor.mossiness)).codec();
    private static final float field_31681 = 0.5f;
    private static final float field_31682 = 0.5f;
    private static final float field_31683 = 0.15f;
    private static final BlockState[] AGEABLE_SLABS = new BlockState[]{Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_BRICK_SLAB.getDefaultState()};
    private final float mossiness;

    public BlockAgeStructureProcessor(float mossiness) {
        this.mossiness = mossiness;
    }

    @Override
    @Nullable
    public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
        Random random = data.getRandom(currentBlockInfo.pos);
        BlockState blockState = currentBlockInfo.state;
        BlockPos blockPos = currentBlockInfo.pos;
        BlockState blockState2 = null;
        if (blockState.isOf(Blocks.STONE_BRICKS) || blockState.isOf(Blocks.STONE) || blockState.isOf(Blocks.CHISELED_STONE_BRICKS)) {
            blockState2 = this.processBlocks(random);
        } else if (blockState.isIn(BlockTags.STAIRS)) {
            blockState2 = this.processStairs(random, currentBlockInfo.state);
        } else if (blockState.isIn(BlockTags.SLABS)) {
            blockState2 = this.processSlabs(random);
        } else if (blockState.isIn(BlockTags.WALLS)) {
            blockState2 = this.processWalls(random);
        } else if (blockState.isOf(Blocks.OBSIDIAN)) {
            blockState2 = this.processObsidian(random);
        }
        if (blockState2 != null) {
            return new StructureTemplate.StructureBlockInfo(blockPos, blockState2, currentBlockInfo.nbt);
        }
        return currentBlockInfo;
    }

    @Nullable
    private BlockState processBlocks(Random random) {
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] blockStates = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.getDefaultState(), BlockAgeStructureProcessor.randomStairProperties(random, Blocks.STONE_BRICK_STAIRS)};
        BlockState[] blockStates2 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.getDefaultState(), BlockAgeStructureProcessor.randomStairProperties(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};
        return this.process(random, blockStates, blockStates2);
    }

    @Nullable
    private BlockState processStairs(Random random, BlockState state) {
        Direction direction = state.get(StairsBlock.FACING);
        BlockHalf blockHalf = state.get(StairsBlock.HALF);
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] blockStates = new BlockState[]{(BlockState)((BlockState)Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, direction)).with(StairsBlock.HALF, blockHalf), Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()};
        return this.process(random, AGEABLE_SLABS, blockStates);
    }

    @Nullable
    private BlockState processSlabs(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState();
        }
        return null;
    }

    @Nullable
    private BlockState processWalls(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState();
        }
        return null;
    }

    @Nullable
    private BlockState processObsidian(Random random) {
        if (random.nextFloat() < 0.15f) {
            return Blocks.CRYING_OBSIDIAN.getDefaultState();
        }
        return null;
    }

    private static BlockState randomStairProperties(Random random, Block stairs) {
        return (BlockState)((BlockState)stairs.getDefaultState().with(StairsBlock.FACING, Direction.Type.HORIZONTAL.random(random))).with(StairsBlock.HALF, BlockHalf.values()[random.nextInt(BlockHalf.values().length)]);
    }

    private BlockState process(Random random, BlockState[] regularStates, BlockState[] mossyStates) {
        if (random.nextFloat() < this.mossiness) {
            return BlockAgeStructureProcessor.randomState(random, mossyStates);
        }
        return BlockAgeStructureProcessor.randomState(random, regularStates);
    }

    private static BlockState randomState(Random random, BlockState[] states) {
        return states[random.nextInt(states.length)];
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_AGE;
    }
}

