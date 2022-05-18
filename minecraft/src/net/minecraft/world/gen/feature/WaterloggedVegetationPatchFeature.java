package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class WaterloggedVegetationPatchFeature extends VegetationPatchFeature {
	public WaterloggedVegetationPatchFeature(Codec<VegetationPatchFeatureConfig> codec) {
		super(codec);
	}

	@Override
	protected Set<BlockPos> placeGroundAndGetPositions(
		StructureWorldAccess world, VegetationPatchFeatureConfig config, Random random, BlockPos pos, Predicate<BlockState> replaceable, int radiusX, int radiusZ
	) {
		Set<BlockPos> set = super.placeGroundAndGetPositions(world, config, random, pos, replaceable, radiusX, radiusZ);
		Set<BlockPos> set2 = new HashSet();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (BlockPos blockPos : set) {
			if (!isSolidBlockAroundPos(world, set, blockPos, mutable)) {
				set2.add(blockPos);
			}
		}

		for (BlockPos blockPosx : set2) {
			world.setBlockState(blockPosx, Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS);
		}

		return set2;
	}

	private static boolean isSolidBlockAroundPos(StructureWorldAccess world, Set<BlockPos> positions, BlockPos pos, BlockPos.Mutable mutablePos) {
		return isSolidBlockSide(world, pos, mutablePos, Direction.NORTH)
			|| isSolidBlockSide(world, pos, mutablePos, Direction.EAST)
			|| isSolidBlockSide(world, pos, mutablePos, Direction.SOUTH)
			|| isSolidBlockSide(world, pos, mutablePos, Direction.WEST)
			|| isSolidBlockSide(world, pos, mutablePos, Direction.DOWN);
	}

	private static boolean isSolidBlockSide(StructureWorldAccess world, BlockPos pos, BlockPos.Mutable mutablePos, Direction direction) {
		mutablePos.set(pos, direction);
		return !world.getBlockState(mutablePos).isSideSolidFullSquare(world, mutablePos, direction.getOpposite());
	}

	@Override
	protected boolean generateVegetationFeature(
		StructureWorldAccess world, VegetationPatchFeatureConfig config, ChunkGenerator generator, Random random, BlockPos pos
	) {
		if (super.generateVegetationFeature(world, config, generator, random, pos.down())) {
			BlockState blockState = world.getBlockState(pos);
			if (blockState.contains(Properties.WATERLOGGED) && !(Boolean)blockState.get(Properties.WATERLOGGED)) {
				world.setBlockState(pos, blockState.with(Properties.WATERLOGGED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			}

			return true;
		} else {
			return false;
		}
	}
}
