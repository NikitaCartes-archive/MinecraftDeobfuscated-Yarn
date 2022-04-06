package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class HugeFungusFeatureConfig implements FeatureConfig {
	public static final Codec<HugeFungusFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("valid_base_block").forGetter(config -> config.validBaseBlock),
					BlockState.CODEC.fieldOf("stem_state").forGetter(config -> config.stemState),
					BlockState.CODEC.fieldOf("hat_state").forGetter(config -> config.hatState),
					BlockState.CODEC.fieldOf("decor_state").forGetter(config -> config.decorationState),
					Codec.BOOL.fieldOf("planted").orElse(false).forGetter(config -> config.planted)
				)
				.apply(instance, HugeFungusFeatureConfig::new)
	);
	public final BlockState validBaseBlock;
	public final BlockState stemState;
	public final BlockState hatState;
	public final BlockState decorationState;
	public final boolean planted;

	public HugeFungusFeatureConfig(BlockState validBaseBlock, BlockState stemState, BlockState hatState, BlockState decorationState, boolean planted) {
		this.validBaseBlock = validBaseBlock;
		this.stemState = stemState;
		this.hatState = hatState;
		this.decorationState = decorationState;
		this.planted = planted;
	}
}
