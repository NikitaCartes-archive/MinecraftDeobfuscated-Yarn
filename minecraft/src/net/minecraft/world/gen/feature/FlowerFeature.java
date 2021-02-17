package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class FlowerFeature<U extends FeatureConfig> extends Feature<U> {
	public FlowerFeature(Codec<U> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<U> context) {
		Random random = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		U featureConfig = context.getConfig();
		BlockState blockState = this.getFlowerState(random, blockPos, featureConfig);
		int i = 0;

		for (int j = 0; j < this.getFlowerAmount(featureConfig); j++) {
			BlockPos blockPos2 = this.getPos(random, blockPos, featureConfig);
			if (structureWorldAccess.isAir(blockPos2)
				&& blockState.canPlaceAt(structureWorldAccess, blockPos2)
				&& this.isPosValid(structureWorldAccess, blockPos2, featureConfig)) {
				structureWorldAccess.setBlockState(blockPos2, blockState, 2);
				i++;
			}
		}

		return i > 0;
	}

	public abstract boolean isPosValid(WorldAccess world, BlockPos pos, U config);

	public abstract int getFlowerAmount(U config);

	public abstract BlockPos getPos(Random random, BlockPos pos, U config);

	public abstract BlockState getFlowerState(Random random, BlockPos pos, U config);
}
