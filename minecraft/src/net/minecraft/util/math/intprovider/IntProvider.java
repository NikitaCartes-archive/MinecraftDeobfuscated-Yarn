package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

public abstract class IntProvider {
	private static final Codec<Either<Integer, IntProvider>> INT_CODEC = Codec.either(
		Codec.INT, Registries.INT_PROVIDER_TYPE.getCodec().dispatch(IntProvider::getType, IntProviderType::codec)
	);
	public static final Codec<IntProvider> VALUE_CODEC = INT_CODEC.xmap(
		either -> either.map(ConstantIntProvider::create, provider -> provider),
		provider -> provider.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantIntProvider)provider).getValue()) : Either.right(provider)
	);
	public static final Codec<IntProvider> NON_NEGATIVE_CODEC = createValidatingCodec(0, Integer.MAX_VALUE);
	public static final Codec<IntProvider> POSITIVE_CODEC = createValidatingCodec(1, Integer.MAX_VALUE);

	public static Codec<IntProvider> createValidatingCodec(int min, int max) {
		return createValidatingCodec(min, max, VALUE_CODEC);
	}

	public static <T extends IntProvider> Codec<T> createValidatingCodec(int min, int max, Codec<T> providerCodec) {
		return Codecs.validate(
			providerCodec,
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

	public abstract int get(Random random);

	public abstract int getMin();

	public abstract int getMax();

	public abstract IntProviderType<?> getType();
}
