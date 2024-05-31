package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EndPlatformFeature extends Feature<DefaultFeatureConfig> {
	public EndPlatformFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		generate(context.getWorld(), context.getOrigin(), false);
		return true;
	}

	public static void generate(ServerWorldAccess world, BlockPos pos, boolean breakBlocks) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -1; k < 3; k++) {
					BlockPos blockPos = mutable.set(pos).move(j, k, i);
					Block block = k == -1 ? Blocks.OBSIDIAN : Blocks.AIR;
					if (!world.getBlockState(blockPos).isOf(block)) {
						if (breakBlocks) {
							world.breakBlock(blockPos, true, null);
						}

						world.setBlockState(blockPos, block.getDefaultState(), Block.NOTIFY_ALL);
					}
				}
			}
		}
	}
}
