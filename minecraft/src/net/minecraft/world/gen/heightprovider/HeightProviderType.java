package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface HeightProviderType<P extends HeightProvider> {
	HeightProviderType<ConstantHeightProvider> CONSTANT = register("constant", ConstantHeightProvider.CONSTANT_CODEC);
	HeightProviderType<UniformHeightProvider> UNIFORM = register("uniform", UniformHeightProvider.UNIFORM_CODEC);
	HeightProviderType<BiasedToBottomHeightProvider> BIASED_TO_BOTTOM = register("biased_to_bottom", BiasedToBottomHeightProvider.BIASED_TO_BOTTOM_CODEC);
	HeightProviderType<VeryBiasedToBottomHeightProvider> VERY_BIASED_TO_BOTTOM = register("very_biased_to_bottom", VeryBiasedToBottomHeightProvider.CODEC);
	HeightProviderType<TrapezoidHeightProvider> TRAPEZOID = register("trapezoid", TrapezoidHeightProvider.CODEC);

	Codec<P> codec();

	static <P extends HeightProvider> HeightProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.HEIGHT_PROVIDER_TYPE, id, () -> codec);
	}
}
