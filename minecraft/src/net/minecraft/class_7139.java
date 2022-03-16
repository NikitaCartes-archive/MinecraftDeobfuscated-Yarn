package net.minecraft;

import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.RandomDeriver;

public record class_7139(RandomDeriver random, boolean useLegacyInit, long legacyLevelSeed) {
	public AbstractRandom method_41562(long l) {
		return new AtomicSimpleRandom(this.legacyLevelSeed + l);
	}
}
