package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BlockColumnFeature extends Feature<BlockColumnFeatureConfig> {
	public BlockColumnFeature(Codec<BlockColumnFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<BlockColumnFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockColumnFeatureConfig blockColumnFeatureConfig = context.getConfig();
		Random random = context.getRandom();
		int i = blockColumnFeatureConfig.layers().size();
		int[] is = new int[i];
		int j = 0;

		for (int k = 0; k < i; k++) {
			is[k] = ((BlockColumnFeatureConfig.Layer)blockColumnFeatureConfig.layers().get(k)).height().get(random);
			j += is[k];
		}

		if (j == 0) {
			return false;
		} else {
			BlockPos.Mutable mutable = context.getOrigin().mutableCopy();
			BlockPos.Mutable mutable2 = mutable.mutableCopy().move(blockColumnFeatureConfig.direction());

			for (int l = 0; l < j; l++) {
				if (!blockColumnFeatureConfig.allowedPlacement().test(structureWorldAccess, mutable2)) {
					adjustLayerHeights(is, j, l, blockColumnFeatureConfig.prioritizeTip());
					break;
				}

				mutable2.move(blockColumnFeatureConfig.direction());
			}

			for (int l = 0; l < i; l++) {
				int m = is[l];
				if (m != 0) {
					BlockColumnFeatureConfig.Layer layer = (BlockColumnFeatureConfig.Layer)blockColumnFeatureConfig.layers().get(l);

					for (int n = 0; n < m; n++) {
						structureWorldAccess.setBlockState(mutable, layer.state().get(random, mutable), Block.NOTIFY_LISTENERS);
						mutable.move(blockColumnFeatureConfig.direction());
					}
				}
			}

			return true;
		}
	}

	private static void adjustLayerHeights(int[] layerHeights, int expectedHeight, int actualHeight, boolean prioritizeTip) {
		int i = expectedHeight - actualHeight;
		int j = prioritizeTip ? 1 : -1;
		int k = prioritizeTip ? 0 : layerHeights.length - 1;
		int l = prioritizeTip ? layerHeights.length : -1;

		for (int m = k; m != l && i > 0; m += j) {
			int n = layerHeights[m];
			int o = Math.min(n, i);
			i -= o;
			layerHeights[m] -= o;
		}
	}
}
