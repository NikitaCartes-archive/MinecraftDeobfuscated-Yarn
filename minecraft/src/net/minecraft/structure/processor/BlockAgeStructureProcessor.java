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
	private final float mossiness;

	public BlockAgeStructureProcessor(float mossiness) {
		this.mossiness = mossiness;
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
		BlockState blockState = structureBlockInfo2.state;
		BlockPos blockPos2 = structureBlockInfo2.pos;
		BlockState blockState2 = null;
		if (blockState.isOf(Blocks.field_10056) || blockState.isOf(Blocks.field_10340) || blockState.isOf(Blocks.field_10552)) {
			blockState2 = this.processBlocks(random);
		} else if (blockState.isIn(BlockTags.field_15459)) {
			blockState2 = this.processStairs(random, structureBlockInfo2.state);
		} else if (blockState.isIn(BlockTags.field_15469)) {
			blockState2 = this.processSlabs(random);
		} else if (blockState.isIn(BlockTags.field_15504)) {
			blockState2 = this.processWalls(random);
		} else if (blockState.isOf(Blocks.field_10540)) {
			blockState2 = this.processObsidian(random);
		}

		return blockState2 != null ? new Structure.StructureBlockInfo(blockPos2, blockState2, structureBlockInfo2.tag) : structureBlockInfo2;
	}

	@Nullable
	private BlockState processBlocks(Random random) {
		if (random.nextFloat() >= 0.5F) {
			return null;
		} else {
			BlockState[] blockStates = new BlockState[]{Blocks.field_10416.getDefaultState(), randomStairProperties(random, Blocks.field_10392)};
			BlockState[] blockStates2 = new BlockState[]{Blocks.field_10065.getDefaultState(), randomStairProperties(random, Blocks.field_10173)};
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
			BlockState[] blockStates = new BlockState[]{Blocks.field_10454.getDefaultState(), Blocks.field_10131.getDefaultState()};
			BlockState[] blockStates2 = new BlockState[]{
				Blocks.field_10173.getDefaultState().with(StairsBlock.FACING, direction).with(StairsBlock.HALF, blockHalf), Blocks.field_10024.getDefaultState()
			};
			return this.process(random, blockStates, blockStates2);
		}
	}

	@Nullable
	private BlockState processSlabs(Random random) {
		return random.nextFloat() < this.mossiness ? Blocks.field_10024.getDefaultState() : null;
	}

	@Nullable
	private BlockState processWalls(Random random) {
		return random.nextFloat() < this.mossiness ? Blocks.field_10059.getDefaultState() : null;
	}

	@Nullable
	private BlockState processObsidian(Random random) {
		return random.nextFloat() < 0.15F ? Blocks.field_22423.getDefaultState() : null;
	}

	private static BlockState randomStairProperties(Random random, Block stairs) {
		return stairs.getDefaultState()
			.with(StairsBlock.FACING, Direction.Type.field_11062.random(random))
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
		return StructureProcessorType.field_24044;
	}
}
