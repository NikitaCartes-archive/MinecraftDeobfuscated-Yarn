package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Function;

public class BiasedToBottomIntProvider extends IntProvider {
	public static final Codec<BiasedToBottomIntProvider> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("min_inclusive").forGetter(biasedToBottomIntProvider -> biasedToBottomIntProvider.min),
						Codec.INT.fieldOf("max_inclusive").forGetter(biasedToBottomIntProvider -> biasedToBottomIntProvider.max)
					)
					.apply(instance, BiasedToBottomIntProvider::new)
		)
		.comapFlatMap(
			biasedToBottomIntProvider -> biasedToBottomIntProvider.max < biasedToBottomIntProvider.min
					? DataResult.error("Max must be at least min, min_inclusive: " + biasedToBottomIntProvider.min + ", max_inclusive: " + biasedToBottomIntProvider.max)
					: DataResult.success(biasedToBottomIntProvider),
			Function.identity()
		);
	private final int min;
	private final int max;

	private BiasedToBottomIntProvider(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public static BiasedToBottomIntProvider create(int min, int max) {
		return new BiasedToBottomIntProvider(min, max);
	}

	@Override
	public int get(Random random) {
		return this.min + random.nextInt(random.nextInt(this.max - this.min + 1) + 1);
	}

	@Override
	public int getMin() {
		return this.min;
	}

	@Override
	public int getMax() {
		return this.max;
	}

	@Override
	public IntProviderType<?> getType() {
		return IntProviderType.BIASED_TO_BOTTOM;
	}

	public String toString() {
		return "[" + this.min + "-" + this.max + "]";
	}
}
