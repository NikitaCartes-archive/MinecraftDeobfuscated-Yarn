package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5428;
import net.minecraft.block.BlockState;

public class DeltaFeatureConfig implements FeatureConfig {
	public static final Codec<DeltaFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockState.CODEC.fieldOf("contents").forGetter(deltaFeatureConfig -> deltaFeatureConfig.contents),
					BlockState.CODEC.fieldOf("rim").forGetter(deltaFeatureConfig -> deltaFeatureConfig.rim),
					class_5428.method_30316(0, 8, 8).fieldOf("size").forGetter(deltaFeatureConfig -> deltaFeatureConfig.field_25843),
					class_5428.method_30316(0, 8, 8).fieldOf("rim_size").forGetter(deltaFeatureConfig -> deltaFeatureConfig.field_25844)
				)
				.apply(instance, DeltaFeatureConfig::new)
	);
	private final BlockState contents;
	private final BlockState rim;
	private final class_5428 field_25843;
	private final class_5428 field_25844;

	public DeltaFeatureConfig(BlockState contents, BlockState rim, class_5428 arg, class_5428 arg2) {
		this.contents = contents;
		this.rim = rim;
		this.field_25843 = arg;
		this.field_25844 = arg2;
	}

	public BlockState method_30397() {
		return this.contents;
	}

	public BlockState method_30400() {
		return this.rim;
	}

	public class_5428 method_30402() {
		return this.field_25843;
	}

	public class_5428 method_30403() {
		return this.field_25844;
	}
}
