package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface IntProviderType<P extends IntProvider> {
	IntProviderType<ConstantIntProvider> CONSTANT = register("constant", ConstantIntProvider.CODEC);
	IntProviderType<UniformIntProvider> UNIFORM = register("uniform", UniformIntProvider.CODEC);
	IntProviderType<BiasedToBottomIntProvider> BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomIntProvider.CODEC);
	IntProviderType<ClampedIntProvider> CLAMPED = register("clamped", ClampedIntProvider.CODEC);
	IntProviderType<WeightedListIntProvider> WEIGHTED_LIST = register("weighted_list", WeightedListIntProvider.CODEC);
	IntProviderType<ClampedNormalIntProvider> CLAMPED_NORMAL = register("clamped_normal", ClampedNormalIntProvider.CODEC);

	Codec<P> codec();

	static <P extends IntProvider> IntProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.INT_PROVIDER_TYPE, id, () -> codec);
	}
}
