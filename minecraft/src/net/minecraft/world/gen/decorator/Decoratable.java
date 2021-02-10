package net.minecraft.world.gen.decorator;

import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.UniformIntDistribution;

public interface Decoratable<R> {
	R decorate(ConfiguredDecorator<?> decorator);

	/**
	 * Applies the {@code minecraft:chance} decorator, which only
	 * allows positions with a {@code 1 / count} chance,
	 * e.g. a count of 2 would give approximately half of the input positions.
	 */
	default R applyChance(int chance) {
		return this.decorate(Decorator.CHANCE.configure(new ChanceDecoratorConfig(chance)));
	}

	/**
	 * Applies the {@code minecraft:count} decorator, which repeats
	 * the input positions by the value of the {@code count} distribution.
	 * 
	 * @param count the distribution of the repetition count
	 */
	default R repeat(UniformIntDistribution count) {
		return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
	}

	/**
	 * Applies the {@code minecraft:count} decorator, which repeats
	 * the input positions {@code count} times.
	 * 
	 * @param count the repetition count
	 */
	default R repeat(int count) {
		return this.repeat(UniformIntDistribution.of(count));
	}

	/**
	 * Applies the {@code minecraft:count} decorator, which repeats
	 * the input positions by a random number between 0 and {@code maxCount}.
	 * 
	 * @param maxCount the maximum repetition count
	 */
	default R repeatRandomly(int maxCount) {
		return this.repeat(UniformIntDistribution.of(0, maxCount));
	}

	/**
	 * Applies the {@code minecraft:range} decorator, which returns the input
	 * position with a y value from zero to {@code max}.
	 */
	default R rangeOf(int i, int j) {
		return this.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(i, i, j)));
	}

	/**
	 * Applies the {@code minecraft:square} decorator, which spreads positions
	 * horizontally a random amount between 0 and 15 blocks on both horizontal axes.
	 */
	default R spreadHorizontally() {
		return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
	}
}
