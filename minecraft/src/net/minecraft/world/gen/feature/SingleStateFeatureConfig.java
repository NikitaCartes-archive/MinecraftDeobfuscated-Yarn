package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;

public class SingleStateFeatureConfig implements FeatureConfig {
	public static final Codec<SingleStateFeatureConfig> field_24874 = BlockState.field_24734
		.fieldOf("state")
		.<SingleStateFeatureConfig>xmap(SingleStateFeatureConfig::new, singleStateFeatureConfig -> singleStateFeatureConfig.state)
		.codec();
	public final BlockState state;

	public SingleStateFeatureConfig(BlockState state) {
		this.state = state;
	}
}
