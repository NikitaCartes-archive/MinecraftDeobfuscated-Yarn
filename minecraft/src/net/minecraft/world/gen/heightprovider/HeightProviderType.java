package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface HeightProviderType<P extends HeightProvider> {
	HeightProviderType<ConstantHeightProvider> CONSTANT = register("constant", ConstantHeightProvider.CONSTANT_CODEC);
	HeightProviderType<UniformHeightProvider> UNIFORM = register("uniform", UniformHeightProvider.UNIFORM_CODEC);
	HeightProviderType<BiasedToBottomHeightProvider> BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomHeightProvider.BIASED_TO_BOTTOM_CODEC);
	HeightProviderType<VeryBiasedToBottomHeightProvider> VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", VeryBiasedToBottomHeightProvider.CODEC);
	HeightProviderType<TrapezoidHeightProvider> TRAPEZOID = register("trapezoid", TrapezoidHeightProvider.CODEC);
	HeightProviderType<WeightedListHeightProvider> WEIGHTED_LIST = register("weighted_list", WeightedListHeightProvider.WEIGHTED_LIST_CODEC);

	Codec<P> codec();

	private static <P extends HeightProvider> HeightProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.HEIGHT_PROVIDER_TYPE, id, () -> codec);
	}
}
