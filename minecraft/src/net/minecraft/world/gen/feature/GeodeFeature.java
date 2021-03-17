package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
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
		int m = 0;

		for (int n = 0; n < k; n++) {
			int o = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
			int p = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
			int q = geodeFeatureConfig.minOuterWallDistance + random.nextInt(geodeFeatureConfig.maxOuterWallDistance - geodeFeatureConfig.minOuterWallDistance);
			BlockPos blockPos2 = blockPos.add(o, p, q);
			BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
			if (blockState.isAir() || blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.LAVA)) {
				if (++m > geodeFeatureConfig.invalidBlocksThreshold) {
					return false;
				}
			}

			list.add(Pair.of(blockPos2, geodeFeatureConfig.minPointOffset + random.nextInt(geodeFeatureConfig.maxPointOffset - geodeFeatureConfig.minPointOffset)));
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

		for (BlockPos blockPos3 : BlockPos.iterate(blockPos.add(i, i, i), blockPos.add(j, j, j))) {
			double r = doublePerlinNoiseSampler.sample((double)blockPos3.getX(), (double)blockPos3.getY(), (double)blockPos3.getZ())
				* geodeFeatureConfig.noiseMultiplier;
			double s = 0.0;
			double t = 0.0;

			for (Pair<BlockPos, Integer> pair : list) {
				s += MathHelper.fastInverseSqrt(blockPos3.getSquaredDistance(pair.getFirst()) + (double)pair.getSecond().intValue()) + r;
			}

			for (BlockPos blockPos4 : list2) {
				t += MathHelper.fastInverseSqrt(blockPos3.getSquaredDistance(blockPos4) + (double)geodeCrackConfig.crackPointOffset) + r;
			}

			if (!(s < h)) {
				if (bl && t >= l && s < e) {
					if (structureWorldAccess.getFluidState(blockPos3).isEmpty()) {
						structureWorldAccess.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), SetBlockStateFlags.NOTIFY_LISTENERS);
					}
				} else if (s >= e) {
					structureWorldAccess.setBlockState(blockPos3, geodeLayerConfig.fillingProvider.getBlockState(random, blockPos3), SetBlockStateFlags.NOTIFY_LISTENERS);
				} else if (s >= f) {
					boolean bl2 = (double)random.nextFloat() < geodeFeatureConfig.useAlternateLayer0Chance;
					if (bl2) {
						structureWorldAccess.setBlockState(
							blockPos3, geodeLayerConfig.alternateInnerLayerProvider.getBlockState(random, blockPos3), SetBlockStateFlags.NOTIFY_LISTENERS
						);
					} else {
						structureWorldAccess.setBlockState(blockPos3, geodeLayerConfig.innerLayerProvider.getBlockState(random, blockPos3), SetBlockStateFlags.NOTIFY_LISTENERS);
					}

					if ((!geodeFeatureConfig.placementsRequireLayer0Alternate || bl2) && (double)random.nextFloat() < geodeFeatureConfig.usePotentialPlacementsChance) {
						list3.add(blockPos3.toImmutable());
					}
				} else if (s >= g) {
					structureWorldAccess.setBlockState(blockPos3, geodeLayerConfig.middleLayerProvider.getBlockState(random, blockPos3), SetBlockStateFlags.NOTIFY_LISTENERS);
				} else if (s >= h) {
					structureWorldAccess.setBlockState(blockPos3, geodeLayerConfig.outerLayerProvider.getBlockState(random, blockPos3), SetBlockStateFlags.NOTIFY_LISTENERS);
				}
			}
		}

		List<BlockState> list4 = geodeLayerConfig.innerBlocks;

		for (BlockPos blockPos5 : list3) {
			BlockState blockState2 = (BlockState)list4.get(random.nextInt(list4.size()));

			for (Direction direction : DIRECTIONS) {
				if (blockState2.contains(Properties.FACING)) {
					blockState2 = blockState2.with(Properties.FACING, direction);
				}

				BlockPos blockPos6 = blockPos5.offset(direction);
				BlockState blockState3 = structureWorldAccess.getBlockState(blockPos6);
				if (blockState2.contains(Properties.WATERLOGGED)) {
					blockState2 = blockState2.with(Properties.WATERLOGGED, Boolean.valueOf(blockState3.getFluidState().isStill()));
				}

				if (BuddingAmethystBlock.canGrowIn(blockState3)) {
					structureWorldAccess.setBlockState(blockPos6, blockState2, SetBlockStateFlags.NOTIFY_LISTENERS);
					break;
				}
			}
		}

		return true;
	}
}
