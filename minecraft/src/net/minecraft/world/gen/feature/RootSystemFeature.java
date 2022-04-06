package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RootSystemFeature extends Feature<RootSystemFeatureConfig> {
	public RootSystemFeature(Codec<RootSystemFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<RootSystemFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		if (!structureWorldAccess.getBlockState(blockPos).isAir()) {
			return false;
		} else {
			AbstractRandom abstractRandom = context.getRandom();
			BlockPos blockPos2 = context.getOrigin();
			RootSystemFeatureConfig rootSystemFeatureConfig = context.getConfig();
			BlockPos.Mutable mutable = blockPos2.mutableCopy();
			if (generateTreeAndRoots(structureWorldAccess, context.getGenerator(), rootSystemFeatureConfig, abstractRandom, mutable, blockPos2)) {
				generateHangingRoots(structureWorldAccess, rootSystemFeatureConfig, abstractRandom, blockPos2, mutable);
			}

			return true;
		}
	}

	private static boolean hasSpaceForTree(StructureWorldAccess world, RootSystemFeatureConfig config, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 1; i <= config.requiredVerticalSpaceForTree; i++) {
			mutable.move(Direction.UP);
			BlockState blockState = world.getBlockState(mutable);
			if (!isAirOrWater(blockState, i, config.allowedVerticalWaterForTree)) {
				return false;
			}
		}

		return true;
	}

	private static boolean isAirOrWater(BlockState state, int height, int allowedVerticalWaterForTree) {
		if (state.isAir()) {
			return true;
		} else {
			int i = height + 1;
			return i <= allowedVerticalWaterForTree && state.getFluidState().isIn(FluidTags.WATER);
		}
	}

	private static boolean generateTreeAndRoots(
		StructureWorldAccess world,
		ChunkGenerator generator,
		RootSystemFeatureConfig config,
		AbstractRandom abstractRandom,
		BlockPos.Mutable mutablePos,
		BlockPos pos
	) {
		for (int i = 0; i < config.maxRootColumnHeight; i++) {
			mutablePos.move(Direction.UP);
			if (config.predicate.test(world, mutablePos) && hasSpaceForTree(world, config, mutablePos)) {
				BlockPos blockPos = mutablePos.down();
				if (world.getFluidState(blockPos).isIn(FluidTags.LAVA) || !world.getBlockState(blockPos).getMaterial().isSolid()) {
					return false;
				}

				if (config.feature.value().generateUnregistered(world, generator, abstractRandom, mutablePos)) {
					generateRootsColumn(pos, pos.getY() + i, world, config, abstractRandom);
					return true;
				}
			}
		}

		return false;
	}

	private static void generateRootsColumn(BlockPos pos, int maxY, StructureWorldAccess world, RootSystemFeatureConfig config, AbstractRandom abstractRandom) {
		int i = pos.getX();
		int j = pos.getZ();
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int k = pos.getY(); k < maxY; k++) {
			generateRoots(world, config, abstractRandom, i, j, mutable.set(i, k, j));
		}
	}

	private static void generateRoots(
		StructureWorldAccess world, RootSystemFeatureConfig config, AbstractRandom abstractRandom, int x, int z, BlockPos.Mutable mutablePos
	) {
		int i = config.rootRadius;
		Predicate<BlockState> predicate = state -> state.isIn(config.rootReplaceable);

		for (int j = 0; j < config.rootPlacementAttempts; j++) {
			mutablePos.set(mutablePos, abstractRandom.nextInt(i) - abstractRandom.nextInt(i), 0, abstractRandom.nextInt(i) - abstractRandom.nextInt(i));
			if (predicate.test(world.getBlockState(mutablePos))) {
				world.setBlockState(mutablePos, config.rootStateProvider.getBlockState(abstractRandom, mutablePos), Block.NOTIFY_LISTENERS);
			}

			mutablePos.setX(x);
			mutablePos.setZ(z);
		}
	}

	private static void generateHangingRoots(
		StructureWorldAccess world, RootSystemFeatureConfig config, AbstractRandom abstractRandom, BlockPos pos, BlockPos.Mutable mutablePos
	) {
		int i = config.hangingRootRadius;
		int j = config.hangingRootVerticalSpan;

		for (int k = 0; k < config.hangingRootPlacementAttempts; k++) {
			mutablePos.set(
				pos,
				abstractRandom.nextInt(i) - abstractRandom.nextInt(i),
				abstractRandom.nextInt(j) - abstractRandom.nextInt(j),
				abstractRandom.nextInt(i) - abstractRandom.nextInt(i)
			);
			if (world.isAir(mutablePos)) {
				BlockState blockState = config.hangingRootStateProvider.getBlockState(abstractRandom, mutablePos);
				if (blockState.canPlaceAt(world, mutablePos) && world.getBlockState(mutablePos.up()).isSideSolidFullSquare(world, mutablePos, Direction.DOWN)) {
					world.setBlockState(mutablePos, blockState, Block.NOTIFY_LISTENERS);
				}
			}
		}
	}
}
