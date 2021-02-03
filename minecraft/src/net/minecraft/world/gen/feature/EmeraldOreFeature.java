package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EmeraldOreFeature extends Feature<EmeraldOreFeatureConfig> {
	public EmeraldOreFeature(Codec<EmeraldOreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<EmeraldOreFeatureConfig> featureContext) {
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos blockPos = featureContext.getPos();
		EmeraldOreFeatureConfig emeraldOreFeatureConfig = featureContext.getConfig();
		if (structureWorldAccess.getBlockState(blockPos).isOf(emeraldOreFeatureConfig.target.getBlock())) {
			structureWorldAccess.setBlockState(blockPos, emeraldOreFeatureConfig.state, 2);
		}

		return true;
	}
}
