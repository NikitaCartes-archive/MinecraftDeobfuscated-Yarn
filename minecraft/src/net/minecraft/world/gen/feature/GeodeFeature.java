package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GeodeFeature extends Feature<GeodeFeatureConfig> {
	private static final Direction[] DIRECTIONS = Direction.values();

	public GeodeFeature(Codec<GeodeFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<GeodeFeatureConfig> context) {
		GeodeFeatureConfig geodeFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		int i = geodeFeatureConfig.minGenOffset;
		int j = geodeFeatureConfig.maxGenOffset;
		List<Pair<BlockPos, Integer>> list = Lists.<Pair<BlockPos, Integer>>newLinkedList();
		int k = geodeFeatureConfig.distributionPoints.get(random);
		ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(structureWorldAccess.getSeed()));
		DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -4, 1.0);
		List<BlockPos> list2 = Lists.<BlockPos>newLinkedList();
		double d = (double)k / (double)geodeFeatureConfig.outerWallDistance.getMax();
		GeodeLayerThicknessConfig geodeLayerThicknessConfig = geodeFeatureConfig.layerThicknessConfig;
		GeodeLayerConfig geodeLayerConfig = geodeFeatureConfig.layerConfig;
		GeodeCrackConfig geodeCrackConfig = geodeFeatureConfig.crackConfig;
		double e = 1.0 / Math.sqrt(geodeLayerThicknessConfig.filling);
		double f = 1.0 / Math.sqrt(geodeLayerThicknessConfig.innerLayer + d);
		double g = 1.0 / Math.sqrt(geodeLayerThicknessConfig.middleLayer + d);
		double h = 1.0 / Math.sqrt(geodeLayerThicknessConfig.outerLayer + d);
		double l = 1.0 / Math.sqrt(geodeCrackConfig.baseCrackSize + random.nextDouble() / 2.0 + (k > 3 ? d : 0.0));
		boolean bl = (double)random.nextFloat() < geodeCrackConfig.generateCrackChance;
		int m = 0;

		for (int n = 0; n < k; n++) {
			int o = geodeFeatureConfig.outerWallDistance.get(random);
			int p = geodeFeatureConfig.outerWallDistance.get(random);
			int q = geodeFeatureConfig.outerWallDistance.get(random);
			BlockPos blockPos2 = blockPos.add(o, p, q);
			BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
			if (blockState.isAir() || blockState.isIn(BlockTags.GEODE_INVALID_BLOCKS)) {
				if (++m > geodeFeatureConfig.invalidBlocksThreshold) {
					return false;
				}
			}

			list.add(Pair.of(blockPos2, geodeFeatureConfig.pointOffset.get(random)));
		}

		if (bl) {
			int n = random.nextInt(4);
			int o = k * 2 + 1;
			if (n == 0) {
				list2.add(blockPos.add(o, 7, 0));
				list2.add(blockPos.add(o, 5, 0));
				list2.add(blockPos.add(o, 1, 0));
			} else if (n == 1) {
				list2.add(blockPos.add(0, 7, o));
				list2.add(blockPos.add(0, 5, o));
				list2.add(blockPos.add(0, 1, o));
			} else if (n == 2) {
				list2.add(blockPos.add(o, 7, o));
				list2.add(blockPos.add(o, 5, o));
				list2.add(blockPos.add(o, 1, o));
			} else {
				list2.add(blockPos.add(0, 7, 0));
				list2.add(blockPos.add(0, 5, 0));
				list2.add(blockPos.add(0, 1, 0));
			}
		}

		List<BlockPos> list3 = Lists.<BlockPos>newArrayList();
		Predicate<BlockState> predicate = notInBlockTagPredicate(geodeFeatureConfig.layerConfig.cannotReplace);

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos.add(i, i, i), blockPos.add(j, j, j))) {
			double r = doublePerlinNoiseSampler.sample((double)blockPos3.getX(), (double)blockPos3.getY(), (double)blockPos3.getZ())
				* geodeFeatureConfig.noiseMultiplier;
			double s = 0.0;
			double t = 0.0;

			for (Pair<BlockPos, Integer> pair : list) {
				s += MathHelper.inverseSqrt(blockPos3.getSquaredDistance(pair.getFirst()) + (double)pair.getSecond().intValue()) + r;
			}

			for (BlockPos blockPos4 : list2) {
				t += MathHelper.inverseSqrt(blockPos3.getSquaredDistance(blockPos4) + (double)geodeCrackConfig.crackPointOffset) + r;
			}

			if (!(s < h)) {
				if (bl && t >= l && s < e) {
					this.setBlockStateIf(structureWorldAccess, blockPos3, Blocks.AIR.getDefaultState(), predicate);

					for (Direction direction : DIRECTIONS) {
						BlockPos blockPos5 = blockPos3.offset(direction);
						FluidState fluidState = structureWorldAccess.getFluidState(blockPos5);
						if (!fluidState.isEmpty()) {
							structureWorldAccess.scheduleFluidTick(blockPos5, fluidState.getFluid(), 0);
						}
					}
				} else if (s >= e) {
					this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.fillingProvider.get(random, blockPos3), predicate);
				} else if (s >= f) {
					boolean bl2 = (double)random.nextFloat() < geodeFeatureConfig.useAlternateLayer0Chance;
					if (bl2) {
						this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.alternateInnerLayerProvider.get(random, blockPos3), predicate);
					} else {
						this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.innerLayerProvider.get(random, blockPos3), predicate);
					}

					if ((!geodeFeatureConfig.placementsRequireLayer0Alternate || bl2) && (double)random.nextFloat() < geodeFeatureConfig.usePotentialPlacementsChance) {
						list3.add(blockPos3.toImmutable());
					}
				} else if (s >= g) {
					this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.middleLayerProvider.get(random, blockPos3), predicate);
				} else if (s >= h) {
					this.setBlockStateIf(structureWorldAccess, blockPos3, geodeLayerConfig.outerLayerProvider.get(random, blockPos3), predicate);
				}
			}
		}

		List<BlockState> list4 = geodeLayerConfig.innerBlocks;

		for (BlockPos blockPos2 : list3) {
			BlockState blockState = Util.getRandom(list4, random);

			for (Direction direction2 : DIRECTIONS) {
				if (blockState.contains(Properties.FACING)) {
					blockState = blockState.with(Properties.FACING, direction2);
				}

				BlockPos blockPos6 = blockPos2.offset(direction2);
				BlockState blockState2 = structureWorldAccess.getBlockState(blockPos6);
				if (blockState.contains(Properties.WATERLOGGED)) {
					blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(blockState2.getFluidState().isStill()));
				}

				if (BuddingAmethystBlock.canGrowIn(blockState2)) {
					this.setBlockStateIf(structureWorldAccess, blockPos6, blockState, predicate);
					break;
				}
			}
		}

		return true;
	}
}
