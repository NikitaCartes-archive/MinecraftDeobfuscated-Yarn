package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;

public abstract class FloatProvider implements FloatSupplier {
	private static final Codec<Either<Float, FloatProvider>> FLOAT_CODEC = Codec.either(
		Codec.FLOAT, Registries.FLOAT_PROVIDER_TYPE.getCodec().dispatch(FloatProvider::getType, FloatProviderType::codec)
	);
	public static final Codec<FloatProvider> VALUE_CODEC = FLOAT_CODEC.xmap(
		either -> either.map(ConstantFloatProvider::create, provider -> provider),
		provider -> provider.getType() == FloatProviderType.CONSTANT ? Either.left(((ConstantFloatProvider)provider).getValue()) : Either.right(provider)
	);

	public static Codec<FloatProvider> createValidatedCodec(float min, float max) {
		return Codecs.validate(
			VALUE_CODEC,
			provider -> {
				if (provider.getMin() < min) {
					return DataResult.error(() -> "Value provider too low: " + min + " [" + provider.getMin() + "-" + provider.getMax() + "]");
				} else {
					return provider.getMax() > max
						? DataResult.error(() -> "Value provider too high: " + max + " [" + provider.getMin() + "-" + provider.getMax() + "]")
						: DataResult.success(provider);
				}
			}
		);
	}

	public abstract float getMin();

	public abstract float getMax();

	public abstract FloatProviderType<?> getType();
}
