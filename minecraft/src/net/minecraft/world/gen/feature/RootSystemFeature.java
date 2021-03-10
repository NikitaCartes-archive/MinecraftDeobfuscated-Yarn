package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
			Random random = context.getRandom();
			BlockPos blockPos2 = context.getOrigin();
			RootSystemFeatureConfig rootSystemFeatureConfig = context.getConfig();
			BlockPos.Mutable mutable = blockPos2.mutableCopy();
			if (this.generateTreeAndRoots(structureWorldAccess, context.getGenerator(), rootSystemFeatureConfig, random, mutable, blockPos2)) {
				this.generateHangingRoots(structureWorldAccess, rootSystemFeatureConfig, random, blockPos2, mutable);
			}

			return true;
		}
	}

	private boolean hasEnoughSpaceForTree(StructureWorldAccess world, RootSystemFeatureConfig config, BlockPos pos) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 1; i <= config.requiredVerticalSpaceForTree; i++) {
			mutable.move(Direction.UP);
			if (!world.isAir(mutable) && !world.isWater(mutable)) {
				return false;
			}
		}

		return true;
	}

	private boolean generateTreeAndRoots(
		StructureWorldAccess world, ChunkGenerator generator, RootSystemFeatureConfig config, Random random, BlockPos.Mutable mutablePos, BlockPos pos
	) {
		int i = pos.getX();
		int j = pos.getZ();

		for (int k = 0; k < config.maxRootColumnHeight; k++) {
			mutablePos.move(Direction.UP);
			if (TreeFeature.canReplace(world, mutablePos)) {
				if (this.hasEnoughSpaceForTree(world, config, mutablePos)) {
					BlockPos blockPos = mutablePos.down();
					if (world.getFluidState(blockPos).isIn(FluidTags.LAVA) || !world.getBlockState(blockPos).getMaterial().isSolid()) {
						return false;
					}

					if (this.generateFeature(world, generator, config, random, mutablePos)) {
						return true;
					}
				}
			} else {
				this.placeRoots(world, config, random, i, j, mutablePos);
			}
		}

		return false;
	}

	private boolean generateFeature(StructureWorldAccess world, ChunkGenerator generator, RootSystemFeatureConfig config, Random random, BlockPos pos) {
		return ((ConfiguredFeature)config.feature.get()).generate(world, generator, random, pos);
	}

	private void placeRoots(StructureWorldAccess world, RootSystemFeatureConfig config, Random random, int x, int z, BlockPos.Mutable mutablePos) {
		int i = config.rootRadius;
		Tag<Block> tag = BlockTags.getTagGroup().getTag(config.rootReplaceable);
		Predicate<BlockState> predicate = tag == null ? blockState -> true : blockState -> blockState.isIn(tag);

		for (int j = 0; j < config.rootPlacementAttempts; j++) {
			mutablePos.set(mutablePos, random.nextInt(i) - random.nextInt(i), 0, random.nextInt(i) - random.nextInt(i));
			if (predicate.test(world.getBlockState(mutablePos))) {
				world.setBlockState(mutablePos, config.rootStateProvider.getBlockState(random, mutablePos), 2);
			}

			mutablePos.setX(x);
			mutablePos.setZ(z);
		}
	}

	private void generateHangingRoots(StructureWorldAccess world, RootSystemFeatureConfig config, Random random, BlockPos pos, BlockPos.Mutable mutablePos) {
		int i = config.hangingRootRadius;
		int j = config.hangingRootVerticalSpan;

		for (int k = 0; k < config.hangingRootPlacementAttempts; k++) {
			mutablePos.set(pos, random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
			if (world.isAir(mutablePos) && world.getBlockState(mutablePos.up()).getMaterial().isSolid()) {
				world.setBlockState(mutablePos, config.hangingRootStateProvider.getBlockState(random, mutablePos), 2);
			}
		}
	}
}
