package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SimpleBlockFeature extends Feature<SimpleBlockFeatureConfig> {
	public SimpleBlockFeature(Codec<SimpleBlockFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<SimpleBlockFeatureConfig> featureContext) {
		SimpleBlockFeatureConfig simpleBlockFeatureConfig = featureContext.getConfig();
		StructureWorldAccess structureWorldAccess = featureContext.getWorld();
		BlockPos blockPos = featureContext.getPos();
		if (simpleBlockFeatureConfig.placeOn.contains(structureWorldAccess.getBlockState(blockPos.down()))
			&& simpleBlockFeatureConfig.placeIn.contains(structureWorldAccess.getBlockState(blockPos))
			&& simpleBlockFeatureConfig.placeUnder.contains(structureWorldAccess.getBlockState(blockPos.up()))) {
			structureWorldAccess.setBlockState(blockPos, simpleBlockFeatureConfig.toPlace, 2);
			return true;
		} else {
			return false;
		}
	}
}
