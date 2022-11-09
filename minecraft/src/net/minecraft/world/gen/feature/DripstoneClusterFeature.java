package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.util.CaveSurface;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DripstoneClusterFeature extends Feature<DripstoneClusterFeatureConfig> {
	public DripstoneClusterFeature(Codec<DripstoneClusterFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DripstoneClusterFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		DripstoneClusterFeatureConfig dripstoneClusterFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
			return false;
		} else {
			int i = dripstoneClusterFeatureConfig.height.get(random);
			float f = dripstoneClusterFeatureConfig.wetness.get(random);
			float g = dripstoneClusterFeatureConfig.density.get(random);
			int j = dripstoneClusterFeatureConfig.radius.get(random);
			int k = dripstoneClusterFeatureConfig.radius.get(random);

			for (int l = -j; l <= j; l++) {
				for (int m = -k; m <= k; m++) {
					double d = this.dripstoneChance(j, k, l, m, dripstoneClusterFeatureConfig);
					BlockPos blockPos2 = blockPos.add(l, 0, m);
					this.generate(structureWorldAccess, random, blockPos2, l, m, f, d, i, g, dripstoneClusterFeatureConfig);
				}
			}

			return true;
		}
	}

	private void generate(
		StructureWorldAccess world,
		Random random,
		BlockPos pos,
		int localX,
		int localZ,
		float wetness,
		double dripstoneChance,
		int height,
		float density,
		DripstoneClusterFeatureConfig config
	) {
		Optional<CaveSurface> optional = CaveSurface.create(
			world, pos, config.floorToCeilingSearchRange, DripstoneHelper::canGenerate, DripstoneHelper::cannotGenerate
		);
		if (optional.isPresent()) {
			OptionalInt optionalInt = ((CaveSurface)optional.get()).getCeilingHeight();
			OptionalInt optionalInt2 = ((CaveSurface)optional.get()).getFloorHeight();
			if (optionalInt.isPresent() || optionalInt2.isPresent()) {
				boolean bl = random.nextFloat() < wetness;
				CaveSurface caveSurface;
				if (bl && optionalInt2.isPresent() && this.canWaterSpawn(world, pos.withY(optionalInt2.getAsInt()))) {
					int i = optionalInt2.getAsInt();
					caveSurface = ((CaveSurface)optional.get()).withFloor(OptionalInt.of(i - 1));
					world.setBlockState(pos.withY(i), Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS);
				} else {
					caveSurface = (CaveSurface)optional.get();
				}

				OptionalInt optionalInt3 = caveSurface.getFloorHeight();
				boolean bl2 = random.nextDouble() < dripstoneChance;
				int l;
				if (optionalInt.isPresent() && bl2 && !this.isLava(world, pos.withY(optionalInt.getAsInt()))) {
					int j = config.dripstoneBlockLayerThickness.get(random);
					this.placeDripstoneBlocks(world, pos.withY(optionalInt.getAsInt()), j, Direction.UP);
					int k;
					if (optionalInt3.isPresent()) {
						k = Math.min(height, optionalInt.getAsInt() - optionalInt3.getAsInt());
					} else {
						k = height;
					}

					l = this.getHeight(random, localX, localZ, density, k, config);
				} else {
					l = 0;
				}

				boolean bl3 = random.nextDouble() < dripstoneChance;
				int j;
				if (optionalInt3.isPresent() && bl3 && !this.isLava(world, pos.withY(optionalInt3.getAsInt()))) {
					int m = config.dripstoneBlockLayerThickness.get(random);
					this.placeDripstoneBlocks(world, pos.withY(optionalInt3.getAsInt()), m, Direction.DOWN);
					if (optionalInt.isPresent()) {
						j = Math.max(0, l + MathHelper.nextBetween(random, -config.maxStalagmiteStalactiteHeightDiff, config.maxStalagmiteStalactiteHeightDiff));
					} else {
						j = this.getHeight(random, localX, localZ, density, height, config);
					}
				} else {
					j = 0;
				}

				int t;
				int m;
				if (optionalInt.isPresent() && optionalInt3.isPresent() && optionalInt.getAsInt() - l <= optionalInt3.getAsInt() + j) {
					int n = optionalInt3.getAsInt();
					int o = optionalInt.getAsInt();
					int p = Math.max(o - l, n + 1);
					int q = Math.min(n + j, o - 1);
					int r = MathHelper.nextBetween(random, p, q + 1);
					int s = r - 1;
					m = o - r;
					t = s - n;
				} else {
					m = l;
					t = j;
				}

				boolean bl4 = random.nextBoolean() && m > 0 && t > 0 && caveSurface.getOptionalHeight().isPresent() && m + t == caveSurface.getOptionalHeight().getAsInt();
				if (optionalInt.isPresent()) {
					DripstoneHelper.generatePointedDripstone(world, pos.withY(optionalInt.getAsInt() - 1), Direction.DOWN, m, bl4);
				}

				if (optionalInt3.isPresent()) {
					DripstoneHelper.generatePointedDripstone(world, pos.withY(optionalInt3.getAsInt() + 1), Direction.UP, t, bl4);
				}
			}
		}
	}

	private boolean isLava(WorldView world, BlockPos pos) {
		return world.getBlockState(pos).isOf(Blocks.LAVA);
	}

	private int getHeight(Random random, int localX, int localZ, float density, int height, DripstoneClusterFeatureConfig config) {
		if (random.nextFloat() > density) {
			return 0;
		} else {
			int i = Math.abs(localX) + Math.abs(localZ);
			float f = (float)MathHelper.clampedMap((double)i, 0.0, (double)config.maxDistanceFromCenterAffectingHeightBias, (double)height / 2.0, 0.0);
			return (int)clampedGaussian(random, 0.0F, (float)height, f, (float)config.heightDeviation);
		}
	}

	private boolean canWaterSpawn(StructureWorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.DRIPSTONE_BLOCK) && !blockState.isOf(Blocks.POINTED_DRIPSTONE)) {
			if (world.getBlockState(pos.up()).getFluidState().isIn(FluidTags.WATER)) {
				return false;
			} else {
				for (Direction direction : Direction.Type.HORIZONTAL) {
					if (!this.isStoneOrWater(world, pos.offset(direction))) {
						return false;
					}
				}

				return this.isStoneOrWater(world, pos.down());
			}
		} else {
			return false;
		}
	}

	private boolean isStoneOrWater(WorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		return blockState.isIn(BlockTags.BASE_STONE_OVERWORLD) || blockState.getFluidState().isIn(FluidTags.WATER);
	}

	private void placeDripstoneBlocks(StructureWorldAccess world, BlockPos pos, int height, Direction direction) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 0; i < height; i++) {
			if (!DripstoneHelper.generateDripstoneBlock(world, mutable)) {
				return;
			}

			mutable.move(direction);
		}
	}

	private double dripstoneChance(int radiusX, int radiusZ, int localX, int localZ, DripstoneClusterFeatureConfig config) {
		int i = radiusX - Math.abs(localX);
		int j = radiusZ - Math.abs(localZ);
		int k = Math.min(i, j);
		return (double)MathHelper.clampedMap(
			(float)k, 0.0F, (float)config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn, config.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0F
		);
	}

	private static float clampedGaussian(Random random, float min, float max, float mean, float deviation) {
		return ClampedNormalFloatProvider.get(random, mean, deviation, min, max);
	}
}
