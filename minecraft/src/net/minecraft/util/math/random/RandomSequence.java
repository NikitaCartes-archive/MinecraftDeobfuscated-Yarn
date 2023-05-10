package net.minecraft.util.math.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public class RandomSequence {
	public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Xoroshiro128PlusPlusRandom.CODEC.fieldOf("source").forGetter(randomSequence -> randomSequence.source))
				.apply(instance, RandomSequence::new)
	);
	private final Xoroshiro128PlusPlusRandom source;

	public RandomSequence(Xoroshiro128PlusPlusRandom source) {
		this.source = source;
	}

	public RandomSequence(long seed, Identifier id) {
		this(new Xoroshiro128PlusPlusRandom(seed, (long)id.hashCode()));
	}

	public Random getSource() {
		return this.source;
	}
}
