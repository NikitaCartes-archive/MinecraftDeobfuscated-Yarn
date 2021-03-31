package net.minecraft.world.gen.decorator;

import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.YOffset;

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
	default R repeat(IntProvider count) {
		return this.decorate(Decorator.COUNT.configure(new CountConfig(count)));
	}

	/**
	 * Applies the {@code minecraft:count} decorator, which repeats
	 * the input positions {@code count} times.
	 * 
	 * @param count the repetition count
	 */
	default R repeat(int count) {
		return this.repeat(ConstantIntProvider.create(count));
	}

	/**
	 * Applies the {@code minecraft:count} decorator, which repeats
	 * the input positions by a random number between 0 and {@code maxCount}.
	 * 
	 * @param maxCount the maximum repetition count
	 */
	default R repeatRandomly(int maxCount) {
		return this.repeat(UniformIntProvider.create(0, maxCount));
	}

	default R rangeOf(YOffset bottom, YOffset top) {
		return this.range(new RangeDecoratorConfig(bottom, top));
	}

	default R range(RangeDecoratorConfig config) {
		return this.decorate(Decorator.RANGE.configure(config));
	}

	/**
	 * Applies the {@code minecraft:square} decorator, which spreads positions
	 * horizontally a random amount between 0 and 15 blocks on both horizontal axes.
	 */
	default R spreadHorizontally() {
		return this.decorate(Decorator.SQUARE.configure(NopeDecoratorConfig.INSTANCE));
	}

	default R averageDepth(YOffset baseline, int spread) {
		return this.decorate(Decorator.DEPTH_AVERAGE.configure(new DepthAverageDecoratorConfig(baseline, spread)));
	}
}
