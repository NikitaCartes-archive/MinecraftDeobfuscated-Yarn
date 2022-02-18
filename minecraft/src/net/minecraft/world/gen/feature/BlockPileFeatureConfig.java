package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class BlockPileFeatureConfig implements FeatureConfig {
	public static final Codec<BlockPileFeatureConfig> CODEC = BlockStateProvider.TYPE_CODEC
		.fieldOf("state_provider")
		.<BlockPileFeatureConfig>xmap(BlockPileFeatureConfig::new, config -> config.stateProvider)
		.codec();
	public final BlockStateProvider stateProvider;

	public BlockPileFeatureConfig(BlockStateProvider stateProvider) {
		this.stateProvider = stateProvider;
	}
}
