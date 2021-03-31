package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DiskFeature extends Feature<DiskFeatureConfig> {
	public DiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		DiskFeatureConfig diskFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		boolean bl = false;
		int i = blockPos.getY();
		int j = i + diskFeatureConfig.halfHeight;
		int k = i - diskFeatureConfig.halfHeight - 1;
		boolean bl2 = diskFeatureConfig.state.getBlock() instanceof FallingBlock;
		int l = diskFeatureConfig.radius.get(context.getRandom());

		for (int m = blockPos.getX() - l; m <= blockPos.getX() + l; m++) {
			for (int n = blockPos.getZ() - l; n <= blockPos.getZ() + l; n++) {
				int o = m - blockPos.getX();
				int p = n - blockPos.getZ();
				if (o * o + p * p <= l * l) {
					boolean bl3 = false;

					for (int q = j; q >= k; q--) {
						BlockPos blockPos2 = new BlockPos(m, q, n);
						BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
						Block block = blockState.getBlock();
						boolean bl4 = false;
						if (q > k) {
							for (BlockState blockState2 : diskFeatureConfig.targets) {
								if (blockState2.isOf(block)) {
									structureWorldAccess.setBlockState(blockPos2, diskFeatureConfig.state, Block.NOTIFY_LISTENERS);
									bl = true;
									bl4 = true;
									break;
								}
							}
						}

						if (bl2 && bl3 && blockState.isAir()) {
							BlockState blockState3 = diskFeatureConfig.state.isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
							structureWorldAccess.setBlockState(new BlockPos(m, q + 1, n), blockState3, Block.NOTIFY_LISTENERS);
						}

						bl3 = bl4;
					}
				}
			}
		}

		return bl;
	}
}
