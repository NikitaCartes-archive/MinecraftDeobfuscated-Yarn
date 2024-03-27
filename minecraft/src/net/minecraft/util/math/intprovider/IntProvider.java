package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registries;
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
		return providerCodec.validate(provider -> method_58612(min, max, provider));
	}

	private static <T extends IntProvider> DataResult<T> method_58612(int i, int j, T intProvider) {
		if (intProvider.getMin() < i) {
			return DataResult.error(() -> "Value provider too low: " + i + " [" + intProvider.getMin() + "-" + intProvider.getMax() + "]");
		} else {
			return intProvider.getMax() > j
				? DataResult.error(() -> "Value provider too high: " + j + " [" + intProvider.getMin() + "-" + intProvider.getMax() + "]")
				: DataResult.success(intProvider);
		}
	}

	public abstract int get(Random random);

	public abstract int getMin();

	public abstract int getMax();

	public abstract IntProviderType<?> getType();
}
