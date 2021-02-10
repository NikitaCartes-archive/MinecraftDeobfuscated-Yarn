package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.util.DripstoneColumn;
import net.minecraft.world.gen.feature.util.DripstoneHelper;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DripstoneClusterFeature extends Feature<DripstoneClusterFeatureConfig> {
	public DripstoneClusterFeature(Codec<DripstoneClusterFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DripstoneClusterFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getPos();
		DripstoneClusterFeatureConfig dripstoneClusterFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		if (!DripstoneHelper.canGenerate(structureWorldAccess, blockPos)) {
			return false;
		} else {
			int i = dripstoneClusterFeatureConfig.height.getValue(random);
			float f = clampedGaussian(
				random,
				dripstoneClusterFeatureConfig.wetness.minValue(),
				dripstoneClusterFeatureConfig.wetness.maxValue(),
				dripstoneClusterFeatureConfig.wetnessMean,
				dripstoneClusterFeatureConfig.wetnessDeviation
			);
			float g = dripstoneClusterFeatureConfig.density.getValue(random);
			int j = dripstoneClusterFeatureConfig.radius.getValue(random);
			int k = dripstoneClusterFeatureConfig.radius.getValue(random);

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
		Optional<DripstoneColumn> optional = DripstoneColumn.create(
			world, pos, config.floorToCeilingSearchRange, DripstoneHelper::canGenerate, DripstoneHelper::canReplaceOrLava
		);
		if (optional.isPresent()) {
			OptionalInt optionalInt = ((DripstoneColumn)optional.get()).getCeilingHeight();
			OptionalInt optionalInt2 = ((DripstoneColumn)optional.get()).getFloorHeight();
			if (optionalInt.isPresent() || optionalInt2.isPresent()) {
				boolean bl = random.nextFloat() < wetness;
				DripstoneColumn dripstoneColumn;
				if (bl && optionalInt2.isPresent() && this.canWaterSpawn(world, pos.withY(optionalInt2.getAsInt()))) {
					int i = optionalInt2.getAsInt();
					dripstoneColumn = ((DripstoneColumn)optional.get()).withFloor(OptionalInt.of(i - 1));
					world.setBlockState(pos.withY(i), Blocks.WATER.getDefaultState(), 2);
				} else {
					dripstoneColumn = (DripstoneColumn)optional.get();
				}

				OptionalInt optionalInt3 = dripstoneColumn.getFloorHeight();
				boolean bl2 = random.nextDouble() < dripstoneChance;
				int l;
				if (optionalInt.isPresent() && bl2 && !this.isLava(world, pos.withY(optionalInt.getAsInt()))) {
					int j = config.dripstoneBlockLayerThickness.getValue(random);
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
					int m = config.dripstoneBlockLayerThickness.getValue(random);
					this.placeDripstoneBlocks(world, pos.withY(optionalInt3.getAsInt()), m, Direction.DOWN);
					j = Math.max(0, l + MathHelper.nextBetween(random, -config.maxStalagmiteStalactiteHeightDiff, config.maxStalagmiteStalactiteHeightDiff));
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

				boolean bl4 = random.nextBoolean()
					&& m > 0
					&& t > 0
					&& dripstoneColumn.getOptionalHeight().isPresent()
					&& m + t == dripstoneColumn.getOptionalHeight().getAsInt();
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
			float f = (float)MathHelper.clampedLerpFromProgress((double)i, 0.0, (double)config.maxDistanceFromCenterAffectingHeightBias, (double)height / 2.0, 0.0);
			return (int)clampedGaussian(random, 0.0F, (float)height, f, (float)config.heightDeviation);
		}
	}

	private boolean canWaterSpawn(StructureWorldAccess world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.DRIPSTONE_BLOCK) && !blockState.isOf(Blocks.POINTED_DRIPSTONE)) {
			for (Direction direction : Direction.Type.HORIZONTAL) {
				if (!this.isStoneOrWater(world, pos.offset(direction))) {
					return false;
				}
			}

			return this.isStoneOrWater(world, pos.down());
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
		return MathHelper.clampedLerpFromProgress(
			(double)k, 0.0, (double)config.maxDistanceFromCenterAffectingChanceOfDripstoneColumn, (double)config.chanceOfDripstoneColumnAtMaxDistanceFromCenter, 1.0
		);
	}

	private static float clampedGaussian(Random random, float min, float max, float mean, float deviation) {
		return MathHelper.clamp(MathHelper.nextGaussian(random, mean, deviation), min, max);
	}
}
