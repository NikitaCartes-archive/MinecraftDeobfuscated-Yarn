package net.minecraft.world.gen.chunk.placement;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

public enum SpreadType implements StringIdentifiable {
	LINEAR("linear"),
	TRIANGULAR("triangular");

	public static final com.mojang.serialization.Codec<SpreadType> CODEC = StringIdentifiable.createCodec(SpreadType::values);
	private final String name;

	private SpreadType(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public int get(Random random, int bound) {
		return switch (this) {
			case LINEAR -> random.nextInt(bound);
			case TRIANGULAR -> (random.nextInt(bound) + random.nextInt(bound)) / 2;
		};
	}
}
