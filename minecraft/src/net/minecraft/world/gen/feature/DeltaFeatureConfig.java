package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.UniformIntDistribution;

public class DeltaFeatureConfig implements FeatureConfig {
	public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("contents").forGetter(deltaFeatureConfig -> deltaFeatureConfig.contents),
					BlockState.CODEC.fieldOf("rim").forGetter(deltaFeatureConfig -> deltaFeatureConfig.rim),
					UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("size").forGetter(deltaFeatureConfig -> deltaFeatureConfig.field_25843),
					UniformIntDistribution.createValidatedCodec(0, 8, 8).fieldOf("rim_size").forGetter(deltaFeatureConfig -> deltaFeatureConfig.field_25844)
				)
				.apply(instance, DeltaFeatureConfig::new)
	);
	private final BlockState contents;
	private final BlockState rim;
	private final UniformIntDistribution field_25843;
	private final UniformIntDistribution field_25844;

	public DeltaFeatureConfig(BlockState contents, BlockState rim, UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2) {
		this.contents = contents;
		this.rim = rim;
		this.field_25843 = uniformIntDistribution;
		this.field_25844 = uniformIntDistribution2;
	}

	public BlockState method_30397() {
		return this.contents;
	}

	public BlockState method_30400() {
		return this.rim;
	}

	public UniformIntDistribution method_30402() {
		return this.field_25843;
	}

	public UniformIntDistribution method_30403() {
		return this.field_25844;
	}
}
