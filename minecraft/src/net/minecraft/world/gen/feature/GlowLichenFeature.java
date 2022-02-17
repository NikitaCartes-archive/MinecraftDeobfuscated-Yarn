package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractLichenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GlowLichenFeature extends Feature<LichenFeatureConfig> {
	public GlowLichenFeature(Codec<LichenFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<LichenFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		LichenFeatureConfig lichenFeatureConfig = context.getConfig();
		if (!isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
			return false;
		} else {
			List<Direction> list = shuffleDirections(lichenFeatureConfig, random);
			if (generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), lichenFeatureConfig, random, list)) {
				return true;
			} else {
				BlockPos.Mutable mutable = blockPos.mutableCopy();

				for (Direction direction : list) {
					mutable.set(blockPos);
					List<Direction> list2 = shuffleDirections(lichenFeatureConfig, random, direction.getOpposite());

					for (int i = 0; i < lichenFeatureConfig.searchRange; i++) {
						mutable.set(blockPos, direction);
						BlockState blockState = structureWorldAccess.getBlockState(mutable);
						if (!isAirOrWater(blockState) && !blockState.isOf(lichenFeatureConfig.blockToPlace.getBlock())) {
							break;
						}

						if (generate(structureWorldAccess, mutable, blockState, lichenFeatureConfig, random, list2)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public static boolean generate(
		StructureWorldAccess world, BlockPos pos, BlockState state, LichenFeatureConfig config, Random random, List<Direction> directions
	) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (Direction direction : directions) {
			BlockState blockState = world.getBlockState(mutable.set(pos, direction));
			if (config.canPlaceOn.contains(blockState.getBlock())) {
				AbstractLichenBlock abstractLichenBlock = (AbstractLichenBlock)config.blockToPlace.getBlock();
				BlockState blockState2 = abstractLichenBlock.withDirection(state, world, pos, direction);
				if (blockState2 == null) {
					return false;
				}

				world.setBlockState(pos, blockState2, Block.NOTIFY_ALL);
				world.getChunk(pos).markBlockForPostProcessing(pos);
				if (random.nextFloat() < config.spreadChance) {
					abstractLichenBlock.method_40751().method_40761(blockState2, world, pos, direction, random, true);
				}

				return true;
			}
		}

		return false;
	}

	public static List<Direction> shuffleDirections(LichenFeatureConfig config, Random random) {
		List<Direction> list = Lists.<Direction>newArrayList(config.directions);
		Collections.shuffle(list, random);
		return list;
	}

	public static List<Direction> shuffleDirections(LichenFeatureConfig config, Random random, Direction excluded) {
		List<Direction> list = (List<Direction>)config.directions.stream().filter(direction -> direction != excluded).collect(Collectors.toList());
		Collections.shuffle(list, random);
		return list;
	}

	private static boolean isAirOrWater(BlockState state) {
		return state.isAir() || state.isOf(Blocks.WATER);
	}
}
