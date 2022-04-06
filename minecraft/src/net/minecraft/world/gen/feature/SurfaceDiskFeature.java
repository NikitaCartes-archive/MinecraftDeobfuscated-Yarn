package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SurfaceDiskFeature extends DiskFeature {
	public SurfaceDiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		return !context.getWorld().getBlockState(context.getOrigin().down()).isIn(context.getConfig().canOriginReplace()) ? false : super.generate(context);
	}

	@Override
	protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, int topY, int bottomY, BlockPos.Mutable pos) {
		if (!world.isAir(pos.setY(topY + 1))) {
			return false;
		} else {
			for (int i = topY; i > bottomY; i--) {
				BlockState blockState = world.getBlockState(pos.setY(i));
				if (this.anyTargetsMatch(config, blockState)) {
					world.setBlockState(pos, config.state(), Block.NOTIFY_LISTENERS);
					this.markBlocksAboveForPostProcessing(world, pos);
					return true;
				}

				if (!blockState.isAir()) {
					return false;
				}
			}

			return false;
		}
	}
}
