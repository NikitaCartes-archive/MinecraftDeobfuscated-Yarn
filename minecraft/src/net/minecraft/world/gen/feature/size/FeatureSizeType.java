package net.minecraft.world.gen.feature.size;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class FeatureSizeType<P extends FeatureSize> {
	public static final FeatureSizeType<TwoLayersFeatureSize> TWO_LAYERS_FEATURE_SIZE = register("two_layers_feature_size", TwoLayersFeatureSize::new);
	public static final FeatureSizeType<ThreeLayersFeatureSize> THREE_LAYERS_FEATURE_SIZE = register("three_layers_feature_size", ThreeLayersFeatureSize::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends FeatureSize> FeatureSizeType<P> register(String id, Function<Dynamic<?>, P> deserializer) {
		return Registry.register(Registry.FEATURE_SIZE_TYPE, id, new FeatureSizeType<>(deserializer));
	}

	private FeatureSizeType(Function<Dynamic<?>, P> deserializer) {
		this.deserializer = deserializer;
	}

	public P method_27381(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}
}
