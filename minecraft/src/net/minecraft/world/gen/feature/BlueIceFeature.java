package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlueIceFeature extends Feature<DefaultFeatureConfig> {
	public BlueIceFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		if (blockPos.getY() > structureWorldAccess.getSeaLevel() - 1) {
			return false;
		} else if (!structureWorldAccess.getBlockState(blockPos).isOf(Blocks.WATER) && !structureWorldAccess.getBlockState(blockPos.down()).isOf(Blocks.WATER)) {
			return false;
		} else {
			boolean bl = false;

			for (Direction direction : Direction.values()) {
				if (direction != Direction.DOWN && structureWorldAccess.getBlockState(blockPos.offset(direction)).isOf(Blocks.PACKED_ICE)) {
					bl = true;
					break;
				}
			}

			if (!bl) {
				return false;
			} else {
				structureWorldAccess.setBlockState(blockPos, Blocks.BLUE_ICE.getDefaultState(), Block.NOTIFY_LISTENERS);

				for (int i = 0; i < 200; i++) {
					int j = random.nextInt(5) - random.nextInt(6);
					int k = 3;
					if (j < 2) {
						k += j / 2;
					}

					if (k >= 1) {
						BlockPos blockPos2 = blockPos.add(random.nextInt(k) - random.nextInt(k), j, random.nextInt(k) - random.nextInt(k));
						BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
						if (blockState.isAir() || blockState.isOf(Blocks.WATER) || blockState.isOf(Blocks.PACKED_ICE) || blockState.isOf(Blocks.ICE)) {
							for (Direction direction2 : Direction.values()) {
								BlockState blockState2 = structureWorldAccess.getBlockState(blockPos2.offset(direction2));
								if (blockState2.isOf(Blocks.BLUE_ICE)) {
									structureWorldAccess.setBlockState(blockPos2, Blocks.BLUE_ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
									break;
								}
							}
						}
					}
				}

				return true;
			}
		}
	}
}
