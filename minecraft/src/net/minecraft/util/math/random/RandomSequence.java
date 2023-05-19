package net.minecraft.util.math.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public class RandomSequence {
	public static final Codec<RandomSequence> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Xoroshiro128PlusPlusRandom.CODEC.fieldOf("source").forGetter(sequence -> sequence.source)).apply(instance, RandomSequence::new)
	);
	private final Xoroshiro128PlusPlusRandom source;

	public RandomSequence(Xoroshiro128PlusPlusRandom source) {
		this.source = source;
	}

	public RandomSequence(long seed, Identifier id) {
		this(method_52189(seed, id));
	}

	private static Xoroshiro128PlusPlusRandom method_52189(long l, Identifier identifier) {
		Xoroshiro128PlusPlusRandom xoroshiro128PlusPlusRandom = new Xoroshiro128PlusPlusRandom(RandomSeed.createXoroshiroSeed(l).split(createSeed(identifier)));
		xoroshiro128PlusPlusRandom.nextLong();
		return xoroshiro128PlusPlusRandom;
	}

	public static RandomSeed.XoroshiroSeed createSeed(Identifier id) {
		return RandomSeed.createXoroshiroSeed(id.toString());
	}

	public Random getSource() {
		return this.source;
	}
}
