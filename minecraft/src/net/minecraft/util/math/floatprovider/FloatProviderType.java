package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface FloatProviderType<P extends FloatProvider> {
	FloatProviderType<ConstantFloatProvider> CONSTANT = register("constant", ConstantFloatProvider.CODEC);
	FloatProviderType<UniformFloatProvider> UNIFORM = register("uniform", UniformFloatProvider.CODEC);
	FloatProviderType<ClampedNormalFloatProvider> CLAMPED_NORMAL = register("clamped_normal", ClampedNormalFloatProvider.CODEC);
	FloatProviderType<TrapezoidFloatProvider> TRAPEZOID = register("trapezoid", TrapezoidFloatProvider.CODEC);

	Codec<P> codec();

	static <P extends FloatProvider> FloatProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registries.FLOAT_PROVIDER_TYPE, id, () -> codec);
	}
}
