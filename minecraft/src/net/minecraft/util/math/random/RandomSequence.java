package net.minecraft.util.math.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
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
		this(createSource(seed, Optional.of(id)));
	}

	public RandomSequence(long seed, Optional<Identifier> id) {
		this(createSource(seed, id));
	}

	private static Xoroshiro128PlusPlusRandom createSource(long seed, Optional<Identifier> id) {
		RandomSeed.XoroshiroSeed xoroshiroSeed = RandomSeed.createUnmixedXoroshiroSeed(seed);
		if (id.isPresent()) {
			xoroshiroSeed = xoroshiroSeed.split(createSeed((Identifier)id.get()));
		}

		return new Xoroshiro128PlusPlusRandom(xoroshiroSeed.mix());
	}

	public static RandomSeed.XoroshiroSeed createSeed(Identifier id) {
		return RandomSeed.createXoroshiroSeed(id.toString());
	}

	public Random getSource() {
		return this.source;
	}
}
