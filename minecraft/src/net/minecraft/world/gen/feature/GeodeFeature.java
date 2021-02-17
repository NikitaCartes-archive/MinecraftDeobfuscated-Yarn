package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BuddingAmethystBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.ChunkRandom;
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
		if (structureWorldAccess.getFluidState(blockPos.add(0, j / 3, 0)).isStill()) {
			return false;
		} else {
			List<Pair<BlockPos, Integer>> list = Lists.<Pair<BlockPos, Integer>>newLinkedList();
			int k = geodeFeatureConfig.minDistributionPoints + random.nextInt(geodeFeatureConfig.maxDistributionPoints - geodeFeatureConfig.minDistributionPoints);
			ChunkRandom chunkRandom = new ChunkRandom(structureWorldAccess.getSeed());
			DoublePerlinNoiseSampler doublePerlinNoiseSampler = DoublePerlinNoiseSampler.create(chunkRandom, -4, 1.0);
			List<BlockPos> list2 = Lists.<BlockPos>newLinkedList();
			double d = (double)k / (double)geodeFeatureConfig.maxOuterWallDistance;
			GeodeLayerThicknessConfig geodeLayerThicknessConfig = geodeFeatureConfig.layerThicknessConfig;
			GeodeLayerConfig geodeLayerConfig = geodeFeatureConfig.layerConfig;
			GeodeCrackConfig geodeCrackConfig = geodeFeatureConfig.crackConfig;
			double e = 1.0 / Math.sqrt(geodeLayerThicknessConfig.filling);
			double f = 1.0 / Math.sqrt(geodeLayerThicknessConfig.innerLayer + d);
			double g = 1.0 / Math.sqrt(geodeLayerThicknessConfig.middleLayer + d);
			double h = 1.0 / Math.sqrt(geodeLayerThicknessConfig.outerLayer + d);
			double l = 1.0 / Math.sqrt(geodeCrackConfig.baseCrackSize + random.nextDouble() / 2.0 + (k > 3 ? d : 0.0));
			boolean bl = (double)random.nextFloat() < geodeCrackConfig.generateCrackChance;

			for (int m = 0; m < k; m++) {
				int n = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
				int o = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
				int p = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
				list.add(
					Pair.of(blockPos.add(n, o, p), geodeFeatureConfig.minPointOffset + random.nextInt(geodeFeatureConfig.maxPointOffset - geodeFeatureConfig.minPointOffset))
				);
			}

			if (bl) {
				int m = random.nextInt(4);
				int n = k * 2 + 1;
				if (m == 0) {
					list2.add(blockPos.add(n, 7, 0));
					list2.add(blockPos.add(n, 5, 0));
					list2.add(blockPos.add(n, 1, 0));
				} else if (m == 1) {
					list2.add(blockPos.add(0, 7, n));
					list2.add(blockPos.add(0, 5, n));
					list2.add(blockPos.add(0, 1, n));
				} else if (m == 2) {
					list2.add(blockPos.add(n, 7, n));
					list2.add(blockPos.add(n, 5, n));
					list2.add(blockPos.add(n, 1, n));
				} else {
					list2.add(blockPos.add(0, 7, 0));
					list2.add(blockPos.add(0, 5, 0));
					list2.add(blockPos.add(0, 1, 0));
				}
			}

			List<BlockPos> list3 = Lists.<BlockPos>newArrayList();

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(i, i, i), blockPos.add(j, j, j))) {
				double q = doublePerlinNoiseSampler.sample((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ())
					* geodeFeatureConfig.noiseMultiplier;
				double r = 0.0;
				double s = 0.0;

				for (Pair<BlockPos, Integer> pair : list) {
					r += MathHelper.fastInverseSqrt(blockPos2.getSquaredDistance(pair.getFirst()) + (double)pair.getSecond().intValue()) + q;
				}

				for (BlockPos blockPos3 : list2) {
					s += MathHelper.fastInverseSqrt(blockPos2.getSquaredDistance(blockPos3) + (double)geodeCrackConfig.crackPointOffset) + q;
				}

				if (!(r < h)) {
					if (bl && s >= l && r < e) {
						if (structureWorldAccess.getFluidState(blockPos2).isEmpty()) {
							structureWorldAccess.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 2);
						}
					} else if (r >= e) {
						structureWorldAccess.setBlockState(blockPos2, geodeLayerConfig.fillingProvider.getBlockState(random, blockPos2), 2);
					} else if (r >= f) {
						boolean bl2 = (double)random.nextFloat() < geodeFeatureConfig.useAlternateLayer0Chance;
						if (bl2) {
							structureWorldAccess.setBlockState(blockPos2, geodeLayerConfig.alternateInnerLayerProvider.getBlockState(random, blockPos2), 2);
						} else {
							structureWorldAccess.setBlockState(blockPos2, geodeLayerConfig.innerLayerProvider.getBlockState(random, blockPos2), 2);
						}

						if ((!geodeFeatureConfig.placementsRequireLayer0Alternate || bl2) && (double)random.nextFloat() < geodeFeatureConfig.usePotentialPlacementsChance) {
							list3.add(blockPos2.toImmutable());
						}
					} else if (r >= g) {
						structureWorldAccess.setBlockState(blockPos2, geodeLayerConfig.middleLayerProvider.getBlockState(random, blockPos2), 2);
					} else if (r >= h) {
						structureWorldAccess.setBlockState(blockPos2, geodeLayerConfig.outerLayerProvider.getBlockState(random, blockPos2), 2);
					}
				}
			}

			List<BlockState> list4 = geodeLayerConfig.innerBlocks;

			for (BlockPos blockPos4 : list3) {
				BlockState blockState = (BlockState)list4.get(random.nextInt(list4.size()));

				for (Direction direction : DIRECTIONS) {
					if (blockState.contains(Properties.FACING)) {
						blockState = blockState.with(Properties.FACING, direction);
					}

					BlockPos blockPos5 = blockPos4.offset(direction);
					BlockState blockState2 = structureWorldAccess.getBlockState(blockPos5);
					if (blockState.contains(Properties.WATERLOGGED)) {
						blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(blockState2.getFluidState().isStill()));
					}

					if (BuddingAmethystBlock.canGrowIn(blockState2)) {
						structureWorldAccess.setBlockState(blockPos5, blockState, 2);
						break;
					}
				}
			}

			return true;
		}
	}
}
