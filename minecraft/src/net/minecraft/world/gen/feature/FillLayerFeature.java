package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class FillLayerFeature extends Feature<FillLayerFeatureConfig> {
	public FillLayerFeature(Codec<FillLayerFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<FillLayerFeatureConfig> featureContext) {
		BlockPos blockPos = featureContext.getPos();
		FillLayerFeatureConfig fillLayerFeatureConfig = featureContext.getConfig();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = fillLayerFeatureConfig.height;
				mutable.set(k, m, l);
				if (structureWorldAccess.getBlockState(mutable).isAir()) {
					structureWorldAccess.setBlockState(mutable, fillLayerFeatureConfig.state, 2);
				}
			}
		}

		return true;
	}
}
