package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class BuriedTreasureFeatureConfig implements FeatureConfig {
	public static final Codec<BuriedTreasureFeatureConfig> CODEC = Codec.FLOAT
		.xmap(BuriedTreasureFeatureConfig::new, buriedTreasureFeatureConfig -> buriedTreasureFeatureConfig.probability);
	public final float probability;

	public BuriedTreasureFeatureConfig(float probability) {
		this.probability = probability;
	}
}
