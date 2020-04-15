package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
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
	private final float mossiness;

	public BlockAgeStructureProcessor(float mossiness) {
		this.mossiness = mossiness;
	}

	public BlockAgeStructureProcessor(Dynamic<?> dynamic) {
		this(dynamic.get("mossiness").asFloat(1.0F));
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
		Block block = structureBlockInfo2.state.getBlock();
		BlockPos blockPos2 = structureBlockInfo2.pos;
		BlockState blockState = null;
		if (block == Blocks.STONE_BRICKS || block == Blocks.STONE || block == Blocks.CHISELED_STONE_BRICKS) {
			blockState = this.processBlocks(random);
		} else if (block.isIn(BlockTags.STAIRS)) {
			blockState = this.processStairs(random, structureBlockInfo2.state);
		} else if (block.isIn(BlockTags.SLABS)) {
			blockState = this.processSlabs(random);
		} else if (block.isIn(BlockTags.WALLS)) {
			blockState = this.processWalls(random);
		} else if (block == Blocks.OBSIDIAN) {
			blockState = this.processObsidian(random);
		}

		return blockState != null ? new Structure.StructureBlockInfo(blockPos2, blockState, structureBlockInfo2.tag) : structureBlockInfo2;
	}

	@Nullable
	private BlockState processBlocks(Random random) {
		if (random.nextFloat() < 0.5F) {
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
		if (random.nextFloat() < 0.5F) {
			return null;
		} else {
			BlockState[] blockStates = new BlockState[]{Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_BRICK_SLAB.getDefaultState()};
			BlockState[] blockStates2 = new BlockState[]{
				Blocks.MOSSY_STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, direction).with(StairsBlock.HALF, blockHalf),
				Blocks.MOSSY_STONE_BRICK_SLAB.getDefaultState()
			};
			return this.process(random, blockStates, blockStates2);
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
		return (double)random.nextFloat() < 0.2 ? Blocks.CRYING_OBSIDIAN.getDefaultState() : null;
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
	protected StructureProcessorType getType() {
		return StructureProcessorType.BLOCK_AGE;
	}

	@Override
	protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("mossiness"), dynamicOps.createFloat(this.mossiness))));
	}
}
