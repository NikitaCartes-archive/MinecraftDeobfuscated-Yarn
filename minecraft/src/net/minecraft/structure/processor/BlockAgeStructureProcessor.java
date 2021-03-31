package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class BlockAgeStructureProcessor extends StructureProcessor {
	public static final Codec<BlockAgeStructureProcessor> CODEC = Codec.FLOAT
		.fieldOf("mossiness")
		.<BlockAgeStructureProcessor>xmap(BlockAgeStructureProcessor::new, blockAgeStructureProcessor -> blockAgeStructureProcessor.mossiness)
		.codec();
	private static final float field_31681 = 0.5F;
	private static final float field_31682 = 0.5F;
	private static final float field_31683 = 0.15F;
	private static final BlockState[] AGEABLE_SLABS = new BlockState[]{Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_BRICK_SLAB.getDefaultState()};
	private final float mossiness;

	public BlockAgeStructureProcessor(float mossiness) {
		this.mossiness = mossiness;
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
		BlockState blockState = structureBlockInfo2.state;
		BlockPos blockPos2 = structureBlockInfo2.pos;
		BlockState blockState2 = null;
		if (blockState.isOf(Blocks.STONE_BRICKS) || blockState.isOf(Blocks.STONE) || blockState.isOf(Blocks.CHISELED_STONE_BRICKS)) {
			blockState2 = this.processBlocks(random);
		} else if (blockState.isIn(BlockTags.STAIRS)) {
			blockState2 = this.processStairs(random, structureBlockInfo2.state);
		} else if (blockState.isIn(BlockTags.SLABS)) {
			blockState2 = this.processSlabs(random);
		} else if (blockState.isIn(BlockTags.WALLS)) {
			blockState2 = this.processWalls(random);
		} else if (blockState.isOf(Blocks.OBSIDIAN)) {
			blockState2 = this.processObsidian(random);
		}

		return blockState2 != null ? new Structure.StructureBlockInfo(blockPos2, blockState2, structureBlockInfo2.nbt) : structureBlockInfo2;
	}

	@Nullable
	private BlockState processBlocks(Random random) {
		if (random.nextFloat() >= 0.5F) {
			return null;
		} else {
			BlockState[] blockStates = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.getDefaultState(), randomStairProperties(random, Blocks.STONE_BRICK_STAIRS)};
			BlockState[] blockStates2 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.getDefaultState(), randomStairProperties(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};
			return this.process(random, blockStates, blockStates2);
		}
	}

	@Nullable
	private BlockState processStairs(Random random, BlockState state) {
		Direction direction = state.get(StairsBlock.FACING);
		BlockHalf blockHalf = state.get(StairsBlock.HALF);
		if (random.nextFloat() >= 0.5F) {
			return null;
		} else {
			BlockState[] blockStates = new BlockState[]{
				Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, direction).with(StairsBlock.HALF, blockHalf),
				Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()
			};
			return this.process(random, AGEABLE_SLABS, blockStates);
		}
	}

	@Nullable
	private BlockState processSlabs(Random random) {
		return random.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState() : null;
	}

	@Nullable
	private BlockState processWalls(Random random) {
		return random.nextFloat() < this.mossiness ? Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState() : null;
	}

	@Nullable
	private BlockState processObsidian(Random random) {
		return random.nextFloat() < 0.15F ? Blocks.CRYING_OBSIDIAN.getDefaultState() : null;
	}

	private static BlockState randomStairProperties(Random random, Block stairs) {
		return stairs.getDefaultState()
			.with(StairsBlock.FACING, Direction.Type.HORIZONTAL.random(random))
			.with(StairsBlock.HALF, BlockHalf.values()[random.nextInt(BlockHalf.values().length)]);
	}

	private BlockState process(Random random, BlockState[] regularStates, BlockState[] mossyStates) {
		return random.nextFloat() < this.mossiness ? randomState(random, mossyStates) : randomState(random, regularStates);
	}

	private static BlockState randomState(Random random, BlockState[] states) {
		return states[random.nextInt(states.length)];
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_AGE;
	}
}
