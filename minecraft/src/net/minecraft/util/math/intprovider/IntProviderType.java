package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IntProviderType<P extends IntProvider> {
	IntProviderType<ConstantIntProvider> CONSTANT = register("constant", ConstantIntProvider.CODEC);
	IntProviderType<UniformIntProvider> UNIFORM = register("uniform", UniformIntProvider.CODEC);

	Codec<P> codec();

	static <P extends IntProvider> IntProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.INT_PROVIDER_TYPE, id, () -> codec);
	}
}
