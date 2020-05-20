package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class FeatureSizeType<P extends FeatureSize> {
	public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize.CODEC);
	public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize.CODEC);
	private final Codec<P> field_24923;

	private static <P extends FeatureSize> FeatureSizeType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.FEATURE_SIZE_TYPE, id, new FeatureSizeType<>(codec));
	}

	private FeatureSizeType(Codec<P> codec) {
		this.field_24923 = codec;
	}

	public Codec<P> method_28825() {
		return this.field_24923;
	}
}
